package com.hwaipy.soap.build.scalah

import scala.io.Source
import java.io.{File, PrintWriter}
import java.nio.file._
import scala.collection.JavaConverters._

object SbtH {

  private val sbtCmd = System.getProperties.getProperty("os.name") match {
    case osName if osName.startsWith("Windows") => "sbt.bat"
    case _ => "sbt"
  }
  private val sperate = System.getProperties.getProperty("os.name") match {
    case osName if osName.startsWith("Windows") => ";"
    case _ => ":"
  }

  def main(args: Array[String]) {
    try {
      if (args.size == 0) {
        println(
          s"""|SbtH 0.1.0 (Hwaipy)
              |Usage: sbth command
              |     -package      package sbt project to a JAR
              |     -packageSingle  package sbt project and corresponding classes to a JAR
              |""".stripMargin)
        return
      }
      if (args.size > 1) {
        println("Too many args.")
        return
      }
      val command = args(0)

      command match {
        case "clean" => {
          process(Array(sbtCmd, "clean"), new File("."))
        }
        case "package" => {
          process(Array(sbtCmd, "package"), new File("."))
          val dist = new File("target/dist-package")
          if (!dist.exists) dist.mkdirs
          val generatedJar = new File("target/scala-2.12/").listFiles.toList.filter(_.getName.toLowerCase.endsWith(".jar")).sortBy(f => f.getName.size).apply(0).toPath
          val mainJar = new File(dist, generatedJar.getFileName.toString).toPath
          Files.copy(generatedJar, mainJar, StandardCopyOption.REPLACE_EXISTING)
          val dependences = Source.fromFile(new File("target/streams/compile/dependencyClasspath/$global/streams/export")).getLines.next
          val libPath = dist.toPath.resolve("lib")
          if (!Files.exists(libPath)) Files.createDirectories(libPath)
          dependences.split(sperate).filter(d => d.toLowerCase.endsWith(".jar")).foreach(dep => {
            val originalPath = Paths.get(dep)
            val targetPath = libPath.resolve(originalPath.getFileName)
            Files.copy(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
          })
          val cp = Files.list(libPath).iterator.asScala.filter(p => p.getFileName.toString.toLowerCase.endsWith(".jar")).map(f => s"lib/${f.getFileName}").mkString(" ")
          val classpathLine = s"Class-Path: $cp"
          val classpathBuffer = classpathLine.size <= 72 match {
            case true => classpathLine :: Nil
            case false => {
              def splitToLines(line: String): List[String] = {
                line.size <= 71 match {
                  case true => " " + line :: Nil
                  case false => " " + line.substring(0, 71) :: splitToLines(line.substring(71))
                }
              }

              classpathLine.substring(0, 72) :: splitToLines(classpathLine.substring(72))
            }
          }
          Files.createDirectories(dist.toPath.resolve("META-INF/"))
          val pw = new PrintWriter(new File(dist, "/META-INF/MANIFEST.MF"))
          classpathBuffer.foreach(pw.println)
          pw.close
          process(Array("jar", "-ufm", mainJar.getFileName.toString, "META-INF/MANIFEST.MF"), dist)
          Files.delete(new File(dist, "/META-INF/MANIFEST.MF").toPath)
          Files.delete(new File(dist, "/META-INF").toPath)
        }
        case "packageSingle" => {
          process(Array(sbtCmd, "package"), new File("."))
          val dist = new File("target/dist-packageSingle")
          if (!dist.exists) dist.mkdirs
          val generatedJar = new File("target/scala-2.12/").listFiles.toList.filter(_.getName.toLowerCase.endsWith(".jar")).sortBy(f => f.getName.size).apply(0).toPath
          val dependences = Source.fromFile(new File("target/streams/compile/dependencyClasspath/$global/streams/export")).getLines.next
          val libPath = dist.toPath.resolve("lib")
          if (!Files.exists(libPath)) Files.createDirectories(libPath)
          dependences.split(sperate).filter(d => d.toLowerCase.endsWith(".jar")).foreach(dep => {
            val originalPath = Paths.get(dep)
            val targetPath = libPath.resolve(originalPath.getFileName)
            Files.copy(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
          })
          Files.list(libPath).iterator.asScala.filter(p => p.getFileName.toString.toLowerCase.endsWith(".jar")).foreach(f => {
            process(Array("jar", "-xf", f.getFileName.toString), libPath.toFile)
            Files.delete(f)
          })
          val mainJar = new File(libPath.toFile, generatedJar.getFileName.toString).toPath
          Files.copy(generatedJar, mainJar, StandardCopyOption.REPLACE_EXISTING)
          process(Array("jar", "-xf", mainJar.getFileName.toString), libPath.toFile)
          Files.delete(mainJar)
          process(Array("jar", "-cfm", mainJar.getFileName.toString, "META-INF/MANIFEST.MF", "."), libPath.toFile)
          Files.move(libPath.resolve(mainJar.getFileName.toString), libPath.getParent.resolve(mainJar.getFileName.toString))
          Files.walk(libPath, Int.MaxValue).iterator.asScala.toList.reverse.foreach(Files.delete)
        }
        case _ => {
          println(s"Command $command not recgonized.")
        }
      }
    } catch {
      case e: ScalaHException => println(e.getMessage)
    }
  }

  def process(cmd: Array[String], dir: File = new File(".")) {
    val process = Runtime.getRuntime().exec(cmd, null, dir)
    val threadOut = new Thread(new Runnable {
      override def run: Unit = {
        val out = process.getOutputStream
        Source.stdin.foreach(c => {
          out.write(c.toByte)
          out.flush()
        })
      }
    }, "Process Thread: Output")
    val threadIn = new Thread(new Runnable {
      override def run {
        Source.fromInputStream(process.getInputStream).foreach(print)
      }
    }, "Process Thread: Input")
    val threadErr = new Thread(new Runnable {
      override def run: Unit = {
        Source.fromInputStream(process.getErrorStream).foreach(System.err.print)
      }
    }, "Process Thread: Error")
    threadOut.setDaemon(true)
    threadIn.setDaemon(true)
    threadErr.setDaemon(true)
    threadOut.start
    threadIn.start
    threadErr.start
    process.waitFor
  }
}
