package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import com.hwaipy.measure.unit.Units;
import com.hwaipy.physics.crystaloptics.Axis;
import com.hwaipy.physics.crystaloptics.Medium;
import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Hwaipy
 */
public class QuasiPhaseMatchFunction extends CorrelationFunction {

  private final Medium medium;
  private final double lengthOfCrystal;
  private final double period;
  private final Type type;

  public QuasiPhaseMatchFunction(Medium medium, double lengthOfCrystalInMM, double periodInUM) {
    this(medium, Type.II, lengthOfCrystalInMM, periodInUM);
  }

  public QuasiPhaseMatchFunction(Medium medium, Type type, double lengthOfCrystalInMM, double periodInUM) {
    this.medium = medium;
    this.lengthOfCrystal = lengthOfCrystalInMM / 1000;
    this.period = periodInUM / 1e6;
    this.type = type;
  }

  @Override
  public double correlationValue(double lamdaSignal, double lamdaIdler) {
    return correlationValueDirect(lamdaSignal, lamdaIdler);
//        return correlationValueApproximate(lamdaSignal, lamdaIdler);
  }

  public double correlationValueDirect(double lamdaSignal, double lamdaIdler) {
    double lamdaPump = 1 / (1 / lamdaSignal + 1 / lamdaIdler);
    MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaPump, Units.NANOMETRE));
    MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaSignal, Units.NANOMETRE));
    MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaIdler, Units.NANOMETRE));
    double kPump = pump.getWaveNumber(medium, type.getAxis(0)).doubleValue(Units.RECIPROCALMETRE);
    double kSignal = signal.getWaveNumber(medium, type.getAxis(1)).doubleValue(Units.RECIPROCALMETRE);
    double kIdler = idle.getWaveNumber(medium, type.getAxis(2)).doubleValue(Units.RECIPROCALMETRE);

    double arg = lengthOfCrystal / 2 * (kPump - kSignal - kIdler - 2 * Math.PI / period);
    double result = Math.sin(arg) / arg;
    return result;
  }

//  public double correlationValueApproximate(double lamdaSignal, double lamdaIdler) {
//    double lamdaPump = 1 / (1 / lamdaSignal + 1 / lamdaIdler);
//    MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaPump, Units.NANOMETRE));
//    MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaSignal, Units.NANOMETRE));
//    MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaIdler, Units.NANOMETRE));
//    double ngPump = medium.getGroupIndex(pump, Axis.Y);
//    double ngSignal = medium.getGroupIndex(signal, Axis.Y);
//    double ngIdle = medium.getGroupIndex(idle, Axis.Z);
//    MonochromaticWave pdcCenter = MonochromaticWave.byWaveLength(Amount.valueOf(780, Units.NANOMETRE));
//    double omegaPDCCenter = pdcCenter.getAngularFrequency().doubleValue(SI.HERTZ);
//    double omegaPDCSignal = signal.getAngularFrequency().doubleValue(SI.HERTZ);
//    double omegaPDCIdle = idle.getAngularFrequency().doubleValue(SI.HERTZ);
//    double vo = omegaPDCSignal - omegaPDCCenter;
//    double ve = omegaPDCIdle - omegaPDCCenter;
//    double c = Constants.c.doubleValue(SI.METERS_PER_SECOND);
//    double kpp = ngPump / c;
//    double kop = ngSignal / c;
//    double kep = ngIdle / c;
//    double r = (vo * (kop - kpp) + ve * (kep - kpp)) * lengthOfCrystal / 2;
//    return Math.sin(r) / r;
//  }
//  public static void main(String[] args) {
//    MonochromaticWave pump1 = MonochromaticWave.byWaveLength(Amount.valueOf(780, Units.NANOMETRE));
//    MonochromaticWave pump2 = MonochromaticWave.byWaveLength(Amount.valueOf(780.1, Units.NANOMETRE));
//    System.out.println(pump2.getAngularFrequency().doubleValue(Units.TERAHERTZ) - pump1.getAngularFrequency().doubleValue(Units.TERAHERTZ));
//  }
  public enum Type {
//    I{
//      @Override
//      public Axis getAxis(int index) {
//        return new Axis []{Axis}
//      }
//    },
    II {
      @Override
      public Axis getAxis(int index) {
        return (new Axis[]{Axis.Y, Axis.Y, Axis.Z})[index];
      }
    },
    O {
      @Override
      public Axis getAxis(int index) {
        return (new Axis[]{Axis.Z, Axis.Z, Axis.Z})[index];
      }
    };

    public Axis getAxis(int index) {
      return null;
    }
  }
}
