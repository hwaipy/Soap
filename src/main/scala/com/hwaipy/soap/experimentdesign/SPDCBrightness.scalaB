package com.hwaipy.soap.experimentdesign

import java.io.File
import javax.imageio.ImageIO

import com.hwaipy.measure.unit.Units
import com.hwaipy.physics.crystaloptics.{Axis, Medium, Mediums, MonochromaticWave}
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation._
import org.jscience.physics.amount.Amount

object SPDCBrightness extends App {

  //  val spdc = new SPDCProcessType0(Mediums.KTiOPO4, (Axis.Z, Axis.Z, Axis.Z), 15e-3, 800e-9, 820e-9)
  //  println(spdc.approxBrightness)
  //  Range(350, 900,5).foreach(pump =>
  //    new SPDCProcessType0(Mediums.KTiOPO4, (Axis.Z, Axis.Z, Axis.Z), pump * 2, pump * 2, s"degenerate pump $pump")
  //  )
  Range(600, 1064, 4).foreach(signal => {
    val idle = 1 / (1.0 / 532 - 1.0 / signal)
    new SPDCProcessType0(Mediums.KTiOPO4, (Axis.Z, Axis.Z, Axis.Z), signal, idle, s"nondegenerate $signal $idle")
  })

  class SPDCProcessType0(medium: Medium, axises: Tuple3[Axis, Axis, Axis], lambdaSignal: Double, lambdaIdle: Double, tag: String) {
    val viewRangeSignal = 20 * math.pow(lambdaSignal / 700, 2)
    val viewRangeIdle = 20 * math.pow(lambdaIdle / 700, 2)
    val minOmigaS = lambdaSignal - viewRangeSignal
    val maxOmigaS = lambdaSignal + viewRangeSignal
    val minOmigaI = lambdaIdle - viewRangeIdle
    val maxOmigaI = lambdaIdle + viewRangeIdle
    val grid = 1000

    val lambdaPump = 1 / (1 / lambdaSignal + 1 / lambdaIdle)
    val pump = MonochromaticWave.byWaveLength(Amount.valueOf(lambdaPump, Units.NANOMETRE))
    val signal = MonochromaticWave.byWaveLength(Amount.valueOf(lambdaSignal, Units.NANOMETRE))
    val idle = MonochromaticWave.byWaveLength(Amount.valueOf(lambdaIdle, Units.NANOMETRE))
    val kPump = 2 * math.Pi * medium.getIndex(pump, axises._1) / (lambdaPump / 1e9)
    val kSignal = 2 * math.Pi * medium.getIndex(signal, axises._1) / (lambdaSignal / 1e9)
    val kIdle = 2 * math.Pi * medium.getIndex(idle, axises._1) / (lambdaIdle / 1e9)
    val period = 2 * math.Pi / (kPump - kSignal - kIdle)

    val functionPump = new PumpFunction(lambdaPump, 0.01)
    val functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, QuasiPhaseMatchFunction.Type.O, 15, period * 1e6)
    val functionJoin = new JointFunction(functionPhaseMatch, functionPump)

    val correlationPloter = new CorrelationPloter(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, functionJoin, grid, grid)
    correlationPloter.calculate
    val image = correlationPloter.createImage
    ImageIO.write(image, "png", new File(s"$tag.png"))
    val wS = FWHM(correlationPloter.statistics(true))
    val wI = FWHM(correlationPloter.statistics(false))
    println(s"$tag\t${wS * 2 * viewRangeSignal}\t${wI * 2 * viewRangeIdle}")

    def FWHM(stat: Array[Double]) = {
      val max = stat.max
      stat.map(d => (d / max + 0.5).toInt).sum.toDouble / stat.size
    }

    //    val Npump = medium.getGroupIndex(pump, axises._1)
    //    val Nsignal = medium.getGroupIndex(signal, axises._2)
    //    val Nidle = medium.getGroupIndex(idle, axises._3)
    //    val width = 5.6 * 3e8 / length / math.sqrt(math.pow(Npump - Nsignal, 2) + math.pow(Npump - Nidle, 2))
    //    val theta = math.atan2(Nsignal - Npump, Npump - Nidle)
    //    val dOmega = math.abs(width / math.sin(math.Pi / 4 + theta) * 2 * math.Pi)
    //
    //    val brightness = dOmega * brightnessPerOmegaPumpPower
    //
    //    def approxBrightness = brightness / 200
  }

  //  class SPDCProcessType0(medium: Medium, axises: Tuple3[Axis, Axis, Axis], length: Double, lambdaSignal: Double, lambdaIdle: Double) {
  //    val signal = MonochromaticWave.byWaveLength(Amount.valueOf(lambdaSignal, METER))
  //    val idle = MonochromaticWave.byWaveLength(Amount.valueOf(lambdaIdle, METER))
  //    val pump = MonochromaticWave.byAngularFrequency(signal.getAngularFrequency.plus(idle.getAngularFrequency))
  //
  //    val omegaSignal = signal.getAngularFrequency.doubleValue(HERTZ)
  //    val omegaIdle = idle.getAngularFrequency.doubleValue(HERTZ)
  //    val omegaPump = pump.getAngularFrequency.doubleValue(HERTZ)
  //    val nPump = medium.getIndex(pump, axises._1)
  //    val nSignal = medium.getIndex(signal, axises._2)
  //    val nIdle = medium.getIndex(idle, axises._3)
  //    val d = 13.7e-12
  //    val w = 25e-6
  //    val A = math.pow(w, 2) * 9
  //    val powerPump = 1e-3
  //    val brightnessPerOmegaPumpPower = 8 * d * d * length * length * omegaSignal * omegaIdle * powerPump /
  //      8.85e-12 / math.pow(math.Pi, 2) / math.pow(3e8, 3) / (nPump * nSignal * nIdle) / A
  //
  //    val Npump = medium.getGroupIndex(pump, axises._1)
  //    val Nsignal = medium.getGroupIndex(signal, axises._2)
  //    val Nidle = medium.getGroupIndex(idle, axises._3)
  //    val width = 5.6 * 3e8 / length / math.sqrt(math.pow(Npump - Nsignal, 2) + math.pow(Npump - Nidle, 2))
  //    val theta = math.atan2(Nsignal - Npump, Npump - Nidle)
  //    val dOmega = math.abs(width / math.sin(math.Pi / 4 + theta) * 2 * math.Pi)
  //
  //    val brightness = dOmega * brightnessPerOmegaPumpPower
  //
  //
  //    //    println(brightnessPerOmegaPumpPower)
  //    //    println(dOmega)
  //    //    println(brightness)
  //    //    println(brightness/200)
  //    def approxBrightness = brightness / 200
  //  }

}
