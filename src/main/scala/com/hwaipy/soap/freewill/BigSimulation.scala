import java.io.{File, PrintWriter}
import java.time.format.DateTimeFormatter
import java.time.{Duration, LocalDateTime}

import math._
import scala.collection.mutable.ListBuffer
import scala.io.Source

object Experiment {
  val lowestAltitude = 15.0
}

object TrackDetail {
  val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
}

class TrackDetail(file: File) {
  val lines = Source.fromFile(file, "UTF-8").getLines

  val trackPoints = lines.drop(1).toList.map(line => new TrackPoint(line))
  val beginTime = trackPoints.head.time
  val endTime = trackPoints.last.time
  val maxAltitude = trackPoints.map(tp => tp.altitude).max

  def isOverlap(other: TrackDetail) = (beginTime.isBefore(other.beginTime) && endTime.isAfter(other.beginTime)) || (beginTime.isBefore(other.endTime) && endTime.isAfter(other.endTime))

  def isBefore(other: TrackDetail) = endTime.isBefore(other.beginTime)

  val isAtNight = beginTime.getHour > 18 || beginTime.getHour < 6

  override def toString: String = s"TrackDetail[begin at ${beginTime}, end at ${endTime}]"
}

class TrackPoint(line: String) {
  val items = line.split(",").map(_.trim)
  val accurateTime = LocalDateTime.parse(items(0), TrackDetail.dateTimeFormatter)
  val altitude = items(1).toDouble
  val azimuth = items(2).toDouble
  val range = items(3).toDouble
  val time = accurateTime.minusNanos(accurateTime.getNano)

  override def toString: String = s"TrackPoint[${time} (${altitude}, ${azimuth})]"
}

object CommonViewTrackDetail {
  def extractCommonViewTracks(trackDataFileList1: List[File], trackDataFileList2: List[File]) = {
    val it1 = trackDataFileList1.toIterator
    val it2 = trackDataFileList2.toIterator
    var trackData1 = new TrackDetail(it1.next)
    var trackData2 = new TrackDetail(it2.next)
    val commonViewTrackDetails = new ListBuffer[CommonViewTrackDetail]
    while (trackData1 != null && trackData2 != null) {
      if (trackData1.isOverlap(trackData2)) {
        val cvtd = new CommonViewTrackDetail(trackData1, trackData2)
        if (cvtd.isAtNight && cvtd.commonViewDuration > 0) commonViewTrackDetails += cvtd
        trackData1 = null
        trackData2 = null
      } else if (trackData1.isBefore(trackData2)) trackData1 = null else trackData2 = null
      if (trackData1 == null && it1.hasNext) trackData1 = new TrackDetail(it1.next)
      if (trackData2 == null && it2.hasNext) trackData2 = new TrackDetail(it2.next)
    }
    commonViewTrackDetails.toList
  }
}

class CommonViewTrackDetail(trackDetail1: TrackDetail, trackDetail2: TrackDetail) {
  val isAtNight = trackDetail1.isAtNight
  val maxAltitudes = (trackDetail1.maxAltitude, trackDetail2.maxAltitude)
  private val beginTime = if (trackDetail1.beginTime.isBefore(trackDetail2.beginTime)) trackDetail2.beginTime else trackDetail1.beginTime
  private val endTime = if (trackDetail1.endTime.isBefore(trackDetail2.beginTime)) trackDetail1.endTime else trackDetail2.endTime
  val validTrackPoints = {
    val validTrackPoints1 = trackDetail1.trackPoints.filterNot(tp => tp.time.isBefore(beginTime) || tp.time.isAfter(endTime))
    val validTrackPoints2 = trackDetail2.trackPoints.filterNot(tp => tp.time.isBefore(beginTime) || tp.time.isAfter(endTime))
    val trackPoints = validTrackPoints1.zip(validTrackPoints2)
    val valid = trackPoints.filter(tp => tp._1.altitude > Experiment.lowestAltitude && tp._2.altitude > Experiment.lowestAltitude)
    valid.map(tps => new CommonViewTrackPoint(tps._1, tps._2))
  }
  val commonViewDuration = validTrackPoints.size
}

class CommonViewTrackPoint(trackPoint1: TrackPoint, trackPoint2: TrackPoint) {
  if (trackPoint1.time != trackPoint2.time) throw new IllegalArgumentException("Time not the same!")
  val time = trackPoint1.time
  val range1 = trackPoint1.range
  val range2 = trackPoint2.range
}

class LocalityCondition(dg: Double, dtCDF: (Double) => Double, sync: Boolean) {
  private val c = 3e8

  def verify(commonViewTrackDetails: List[CommonViewTrackDetail]): Double = commonViewTrackDetails.map(td => verify(td)).sum

  def verify(commonViewTrackDetail: CommonViewTrackDetail): Double = commonViewTrackDetail.validTrackPoints.map(tp => verify(tp)).sum

  def verify(commonViewTrackPoint: CommonViewTrackPoint): Double = {
    val minD1 = min(2000 * commonViewTrackPoint.range1, dg)
    val minD2 = min(2000 * commonViewTrackPoint.range1, dg)
    val upperBoundDt1 = minD1 / c
    val upperBoundDt2 = minD2 / c
    val p1 = dtCDF(upperBoundDt1)
    val p2 = dtCDF(upperBoundDt2)
    if (p1 < 0 || p1 > 1 || p2 < 0 || p2 > 1) throw new RuntimeException("Bad CDF !!!!!")
    if (sync) min(p1, p2) else p1 * p2
  }
}

object BigSimulate extends App {
  def loadTrackData = {
    val trackDataFileListLJ = new File("0.示例轨道数据/LJ/").listFiles.toList.filter(f => f.getName.toLowerCase.endsWith(".csv"))
    val trackDataFileListDLH = new File("0.示例轨道数据/DLH/").listFiles.toList.filter(f => f.getName.toLowerCase.endsWith(".csv"))
    CommonViewTrackDetail.extractCommonViewTracks(trackDataFileListLJ, trackDataFileListDLH)
  }

  println("Start.....")
  val commonViewTrackDetails = loadTrackData
  println(s"${commonViewTrackDetails.size} common view pass loaded. Total valid time is ${commonViewTrackDetails.map(d => d.validTrackPoints.size).sum}.")
  val simulationResultPath = new File("2.simuRes")

  def simulationHashingMode {
    val pw = new PrintWriter(new File(simulationResultPath, "0.HashingMode.csv"))
    pw.println("dtc (ms), effective experimental time (sync), effective experimental time (non-sync)")
    Range(0, 1000).map(i => {
      val dtc = 5e-3 / 1000 * i
      val delayCDF = (dt: Double) => if (dt < dtc) 0.0 else if (dt > 2 * dtc) 1.0 else (dt - dtc) / dtc
      pw.println(s"${dtc * 1000}," +
        s" ${new LocalityCondition(dg = 1203000, delayCDF, true).verify(commonViewTrackDetails)}," +
        s" ${new LocalityCondition(dg = 1203000, delayCDF, false).verify(commonViewTrackDetails)}")
    })
    pw.close
  }

  def simulationBlinkMode {
    val pw = new PrintWriter(new File(simulationResultPath, "1.BlinkMode.csv"))
    pw.println("dtc (ms), N=1, N=3, N=5, N=7, N=10, N=50")
    val bc = 3
    Range(0, 1000).map(i => {
      val dtc = 5e-3 / 1000 * i
      val eets = List(1, 3, 5, 7, 10, 20).map(N => {
        val delayCDF = (dt: Double) => 1 - pow(1 - N * bc * (max(dt - dtc, 0)), N * bc)
        new LocalityCondition(dg = 1203000, delayCDF, true).verify(commonViewTrackDetails)
      })
      pw.println(s"${dtc * 1000}, ${eets.mkString(", ")}")
    })
    pw.close
  }

  def simulationHashingBlinkMode {
    val pw = new PrintWriter(new File(simulationResultPath, "2.HashingBlinkMode.csv"))
    pw.println("dtc (ms), N=10, N=20, N=50, N=70, N=100, N=150")
    val Thb = 0.6
    val bc = 10
    val R = 0.1
    val IBLi = 0.0
    val Ns = List(10, 20, 50, 70, 100, 150)

    def factorial(n: Int): Double = if (n <= 1) 1.0 else n * factorial(n - 1)

    def permutation(n: Int, k: Int) = factorial(n) / factorial(k) / factorial(n - k)

    Range(0, 1000).map(i => {
      val dtc = 5e-3 / 1000 * i
      val IBHi = bc / R * dtc
      val eets = Ns.map(N => {
        val k0 = (Thb - N * IBLi) / (IBHi - IBLi)
        val k0Int = ceil(k0).toInt
        val PBN = (k: Int) => permutation(N, k) * pow(R, k) * pow(1 - R, N - k)
        val PIB = Range(k0Int, N + 1).map(k => PBN(k)).sum
        val delayCDF = (dt: Double) => PIB * {
          if (dt < dtc) 0.0 else if (dt > 2 * dtc) 1.0 else (dt - dtc) / dtc
        }
        new LocalityCondition(dg = 1203000, delayCDF, false).verify(commonViewTrackDetails)
      })
      pw.println(s"${dtc * 1000}, ${eets.mkString(", ")}")
    })
    pw.close
  }

  //  simulationHashingMode
  //  simulationBlinkMode
  simulationHashingBlinkMode
}


