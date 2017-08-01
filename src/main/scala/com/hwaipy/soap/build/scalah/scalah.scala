package com.hwaipy.soap.build.scalah

import scala.io.Source
import java.io.{File, PrintWriter}
import java.nio.file._
import java.util.concurrent.LinkedBlockingQueue
import java.util.regex.Pattern
import scala.collection.JavaConverters._

object ScalaH {
  val isMac = System.getProperty("os.name") match {
    case s if s.contains("Mac OS X") => true
    case _ => false
  }
  private val utilPath = System.getProperty("os.name") match {
    case s if s.contains("Mac OS X") => "/Users/Hwaipy/Documents/Data/ScalaH/"
    case s if s.contains("Windows") => "C:/ScalaH/tmp/"
    case _ => ""
  }

  def main(args: Array[String]) {
    try {
      if (args.size == 0) {
        println(
          s"""|ScalaH 0.1.0 (Hwaipy)
              |Usage: scalah [-command] script
              |     The script must be a single scala file. The extension ".scala" itself is neglectable.
              |     -run      default, run the script
              |     -compile  compile the script into .\\{SCRIPT_NAME}.scalah\\classes\\
              |""".stripMargin)
        return
      }
      val command = args.filter(arg => arg.startsWith("-")) match {
        case a if a.size == 1 => a.head.substring(1)
        case a if a.size == 0 => "run"
        case _ => throw new RuntimeException("Too many commands.")
      }
      val scriptName = args.filterNot(arg => arg.startsWith("-")) match {
        case a if a.size == 1 => a.head
        case a if a.size == 0 => throw new RuntimeException("No script specified.")
        case _ => throw new RuntimeException("Too many script specified.")
      }
      val scriptFileName = scriptName match {
        case sn if sn.toLowerCase.endsWith(".scala") => sn
        case sn => sn + ".scala"
      }
      val dependences = parseDependences(new File(scriptFileName))

      command match {
        case "run" => process(Array("scala", "-classpath", dependences, scriptFileName))
        case "compile" => {
          val cd = s"${scriptFileName.dropRight(6)}.scalah/classes"
          Files.createDirectories(Paths.get(cd))
          process(Array("scalac", "-classpath", dependences, scriptFileName, "-d", cd))
        }
        case "package" => {
          val cd = s"${scriptFileName.dropRight(6)}.scalah/dist/"
          Files.createDirectories(Paths.get(cd))
          val libPath = Files.createDirectories(Paths.get(cd, "lib"))
          dependences.split(":").foreach(dep => {
            val originalPath = Paths.get(dep)
            val targetPath = libPath.resolve(originalPath.getFileName)
            Files.copy(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)

          })
          process(Array("scalac", "-classpath", dependences, scriptFileName, "-d", s"$cd${scriptFileName.dropRight(6)}.jar"))
          process(Array("jar", "-xf", s"${scriptFileName.dropRight(6)}.jar", "META-INF/MANIFEST.MF"), new File(s"$cd"))
          val cp = s"Class-Path: ${Files.list(libPath).iterator.asScala.filter(p => p.getFileName.toString.toLowerCase.endsWith(".jar")).map(f => s"lib/${f.getFileName}").mkString(" ")}"
          val lines = Source.fromFile(new File(s"$cd/META-INF/MANIFEST.MF")).getLines.toList.filterNot(s => s.size == 0)
          val pw = new PrintWriter(new File(s"$cd/META-INF/MANIFEST.MF"))
          lines.foreach(pw.println)
          pw.println(cp)
          pw.close
          process(Array("jar", "-ufm", s"${scriptFileName.dropRight(6)}.jar", "META-INF/MANIFEST.MF"), new File(s"$cd"))
          Files.delete(Paths.get(s"$cd/META-INF/MANIFEST.MF"))
          Files.delete(Paths.get(s"$cd/META-INF"))
        }
        case "packageSingle" => {
          val cd = s"${scriptFileName.dropRight(6)}.scalah/jar/"
          Files.createDirectories(Paths.get(cd))
          val libPath = Files.createDirectories(Paths.get(cd, "lib"))
          dependences.split(":").foreach(dep => {
            val originalPath = Paths.get(dep)
            val targetPath = libPath.resolve(originalPath.getFileName)
            Files.copy(originalPath, targetPath, StandardCopyOption.REPLACE_EXISTING)
          })
          process(Array("scalac", "-classpath", dependences, scriptFileName, "-d", s"$cd${scriptFileName.dropRight(6)}.jar"))
          Files.list(libPath).iterator.asScala.filter(p => p.getFileName.toString.toLowerCase.endsWith(".jar")).foreach(f => {
            process(Array("jar", "-xf", f.getFileName.toString), libPath.toFile)
            Files.delete(f)
          })
          Files.delete(libPath.resolve("META-INF/MANIFEST.MF"))
          Files.move(Paths.get(cd, s"${scriptFileName.dropRight(6)}.jar"), libPath.resolve(s"${scriptFileName.dropRight(6)}.jar"))
          process(Array("jar", "-xf", s"${scriptFileName.dropRight(6)}.jar"), libPath.toFile)
          Files.delete(libPath.resolve(s"${scriptFileName.dropRight(6)}.jar"))
          process(Array("jar", "-cfm", s"${scriptFileName.dropRight(6)}.jar", "META-INF/MANIFEST.MF", "."), libPath.toFile)
          Files.move(libPath.resolve(s"${scriptFileName.dropRight(6)}.jar"), libPath.getParent.resolve(s"${scriptFileName.dropRight(6)}.jar"))
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
//    println(s"In Pro: ${cmd.toList.toString}   ${dir}")
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

  def parseDependences(file: File) = {
    val classpathPattern = Pattern.compile("//+[ \t]*classpath:(.+)")
    val lines = Source.fromFile(file).getLines.map(line => line.trim).toList
    val depsAll = lines.map(line => {
      val matcher = classpathPattern.matcher(line)
      matcher.find match {
        case true => Some(matcher.group(1))
        case false => None
      }
    })
    val deps = depsAll.slice(0, depsAll.indexOf(None)).map(dep => dep.get)
    val jarDep = parseJarDependences(deps)
    val ivyDep = parseIvyDependences(deps, file.getName match {
      case name if name.toLowerCase.endsWith(".scala") => name.substring(0, name.length - 6)
      case name => name
    }).split(":").drop(1).mkString(":")

    jarDep match {
      case d if d.length > 0 => ivyDep + ":" + jarDep
      case _ => ivyDep
    }
  }

  private def parseIvyDependences(deps: List[String], name: String) = {
    val ivyPattern = Pattern.compile("[ \t]*ivy[ \t]*:[ \t]*(.+?)[ \t]*(%%?)[ \t]*(.+?)[ \t]*(%)[ \t]*(.+)")
    val ivyReps = deps.map(dep => {
      val matcher = ivyPattern.matcher(dep)
      matcher.find match {
        case true => Some((1 to 5).toList.map(i => matcher.group(i)))
        case false => None
      }
    }).filter(dep => dep != None).map(dep => dep.get).map(dep => dep.zipWithIndex.map(zip => zip._2 match {
      case i if i % 2 == 0 => "\"" + zip._1 + "\""
      case _ => zip._1
    })).map(dep => dep.mkString(" "))
    val repBufferFile = new File(utilPath, s"/repBuf/${name}.rep")
    val sbtPath = new File(utilPath, s"/sbt/${name}")
    val needUpdate = repBufferFile.exists match {
      case true => {
        val oldLines = Source.fromFile(repBufferFile).getLines.toList
        oldLines != ivyReps
      }
      case false => true
    }
    if (needUpdate) {
      repBufferFile.getParentFile.mkdirs
      val pw = new PrintWriter(repBufferFile)
      ivyReps.foreach(pw.println)
      pw.close
    }
    val sbtDP = new SbtDependenceParser(name, ivyReps, sbtPath, needUpdate)
    sbtDP.error match {
      case Some(error) => {
        println(error)
        throw new ScalaHException("Error in parsing IVY or MAVEN reps.")
      }
      case _ => sbtDP.dependences
    }
  }

  private def parseJarDependences(deps: List[String]) = {
    val jarDepPattern = Pattern.compile("[ \t]*jar[ \t]*:[ \t]*(.+)")
    deps.map(dep => {
      val matcher = jarDepPattern.matcher(dep)
      matcher.find match {
        case true => Some(matcher.group(1))
        case false => None
      }
    }).filter(dep => dep != None).map(dep => dep.get).mkString(":")
  }
}

private class SbtDependenceParser(name: String, deps: List[String], sbtPath: File, update: Boolean) {
  val error = update match {
    case true => {
      sbtPath.mkdirs
      val bfpw = new PrintWriter(new File(sbtPath, "build.sbt"))
      bfpw.println("name := \"" + name + "\"")
      bfpw.println("version := \"1.0\"")
      bfpw.println("scalaVersion := \"2.12.1\"")
      deps.foreach(r => bfpw.println(s"libraryDependencies += $r"))
      bfpw.close
      val scalaFile = new File(sbtPath, "/src/main/scala/main.scala")
      scalaFile.getParentFile.mkdirs
      val scpw = new PrintWriter(scalaFile)
      scpw.println("object Main{def main(args:Array[String]){}}")
      scpw.close
      val sbtProcess = Runtime.getRuntime.exec(ScalaH.isMac match {
        case true => "sbt run"
        case false => "java C:\\ScalaH\\sbt\\bin\\sbt-launch.jar run"
      }, new Array[String](0), sbtPath)
      val sbtResult = new LinkedBlockingQueue[List[String]]
      val threadIn = new Thread(new Runnable {
        override def run {
          val results = Source.fromInputStream(sbtProcess.getInputStream).getLines().toList
          sbtResult.offer(results)
        }
      }, "SbtProcess Thread: Input")
      threadIn.setDaemon(true)
      threadIn.start
      sbtProcess.waitFor
      val result = sbtResult.take
      println(result)
      result.last.contains("success") match {
        case true => None
        case false => Some(result.mkString("\n"))
      }
    }
    case false => None
  }
  val dependences = error match {
    case Some(e) => ""
    case None => Source.fromFile(new File(sbtPath, "/target/streams/runtime/fullClasspath/$global/streams/export")).getLines.next
  }
}

private class ScalaHException(message: String) extends Exception(message)