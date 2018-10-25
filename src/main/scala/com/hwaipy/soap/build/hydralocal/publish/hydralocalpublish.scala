package com.hwaipy.soap.build.hydralocal.publish

import java.math.BigInteger
import java.nio.ByteBuffer
import java.nio.file.{Files, Path, Paths}
import java.security.MessageDigest

import com.hydra.io.MessageClient

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.collection.JavaConverters._

object HydraLocalPublish {

  val remoteRoot = Paths.get("/apps/hydralocal/")

  def main(args: Array[String]) {
    val filePairs = ListBuffer[Tuple2[String, String]]()
    val filePairLines = Source.fromFile("/Users/hwaipy/HydraLocal/.hydralocalpublish.list").getLines.toList

    def appendFilePair(localPath: Path, remotePath: Path): Unit = {
      if (localPath.getFileName().toString.startsWith(".")) return
      if (Files.isDirectory(localPath)) {
        Files.list(localPath).iterator.asScala.foreach(localPathNext => {
          val remotePathNext = remotePath.resolve(localPath.relativize(localPathNext))
          appendFilePair(localPathNext, remotePathNext)
        })
      }
      if (Files.isRegularFile(localPath)) {
        filePairs += Tuple2(localPath.toString, remoteRoot.resolve(remotePath).toString)
      }
    }

    Range(0, (filePairLines.size + 1) / 3).foreach(i => {
      val localPath = filePairLines(3 * i).trim
      val remotePath = filePairLines(3 * i + 1).trim
      appendFilePair(Paths.get(localPath.trim), Paths.get(remotePath.trim))
    })

    val servers = "localhost:20102" :: "192.168.25.27:20102" :: Nil
    servers.foreach(server => {
      val split = server.split(":")
      try {
        publish(split(0), split(1).toInt, filePairs.toList)
        println(s"Successfully published to $server")
      } catch {
        case e: Throwable => println(s"Error when publishing to $server: $e")
      }
    })
  }

  def publish(host: String, port: Int, filePairs: List[Tuple2[String, String]]) = {
    val client = MessageClient.newClient(host, port, "HydraLocalPublisher")
    val storage = client.blockingInvoker("StorageService")

    def calculateMD5(data: Array[Byte]) = {
      val md = MessageDigest.getInstance("MD5")
      md.update(data)
      new BigInteger(1, md.digest).toString(16)
    }

    def calculateLocalMD5(localPath: String) = {
      val data = Files.readAllBytes(Paths.get(localPath))
      val md5 = calculateMD5(data)
      md5
    }

    def calculateRemoteMD5(remotePath: String, size: Long) = {
      val blockSize = 1000000
      val buffer = ByteBuffer.allocate(size.toInt)
      val blocks = Math.ceil(size.toDouble / blockSize).toInt
      Range(0, blocks).foreach(block => {
        val start = block * blockSize
        val length = math.min(blockSize, size - start)
        val data = storage.read("", remotePath.toString, start, length).asInstanceOf[Array[Byte]]
        buffer.put(data)
      })
      calculateMD5(buffer.array())
    }

    filePairs.foreach(filePair => {
      val localPath = filePair._1
      val remotePath = filePair._2

      val remoteMetaData = storage.metaData("", remotePath).asInstanceOf[Map[String, Any]]
      if (remoteMetaData("Type") == "Collection") storage.delete("", remotePath)
      if (remoteMetaData("Type") == "NotExist") storage.createFile("", remotePath)
      val remoteHash = calculateRemoteMD5(remotePath, remoteMetaData.getOrElse("Size", 0).toString.toLong)

      val localHash = calculateLocalMD5(localPath)

      if (localHash != remoteHash) {
        storage.clear("", remotePath)

        val fileSize = Files.size(Paths.get(localPath))
        val blockSize = 1000000
        val localChannel = Files.newByteChannel(Paths.get(localPath))
        val buffer = ByteBuffer.allocate(blockSize)
        val blocks = Math.ceil(fileSize.toDouble / blockSize).toInt
        Range(0, blocks).foreach(block => {
          val start = block * blockSize
          val length = math.min(blockSize, fileSize - start)
          localChannel.read(buffer)
          storage.append("", remotePath, (length < blockSize) match {
            case true => buffer.array().slice(0, length.toInt)
            case false => buffer.array
          })
          buffer.rewind()
        })
        localChannel.close()
      }
    })

    case class FileEntry(val path: Path, val lastModified: Long, val size: Long, val hash: String)

    client.stop
  }
}