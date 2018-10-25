package com.hwaipy.soap.computer

import java.nio.ByteBuffer
import java.nio.file.{Files, Path, Paths, StandardOpenOption}
import java.security.MessageDigest

import scala.collection.JavaConverters._

object FileMerger extends App {
  println("Start merge files.")

  val sourceRoot = Paths.get("/Users/hwaipy/Google Drive copy")
  val targetRoot = Paths.get("/Users/hwaipy/Google Drive")

  val pathStream = Files.walk(sourceRoot).iterator.asScala.toStream
  pathStream.filter(p => Files.isRegularFile(p)).filterNot(p => p.getFileName.toString == ".DS_Store").foreach(sourcePath => {
    val targetPath = targetRoot.resolve(sourceRoot.relativize(sourcePath))
    if (Files.exists(targetPath)) {
      val sourceMD5 = calculateMD5(sourcePath)
      val targetMD5 = calculateMD5(targetPath)
      if (sourceMD5 == targetMD5) Files.delete(sourcePath)
    }
  })

  def calculateMD5(path: Path) = {
    val md5 = MessageDigest.getInstance("MD5")
    val channel = Files.newByteChannel(path, StandardOpenOption.READ)
    val buffer = ByteBuffer.allocate(100000000)
    while (channel.position < channel.size) {
      channel.read(buffer)
      buffer.flip
      md5.update(buffer)
      buffer.rewind
    }
    val hash = md5.digest()
    channel.close()
    hash.map(b => if ((0xff & b) < 0x10) "0" + Integer.toHexString((0xFF & b)) else Integer.toHexString(0xFF & b)).mkString("")
  }
}