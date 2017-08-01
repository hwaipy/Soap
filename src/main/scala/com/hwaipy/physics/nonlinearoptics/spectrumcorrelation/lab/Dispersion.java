/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.lab;

import com.hwaipy.physics.crystaloptics.Mediums;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.BandPassFilter;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.CorrelationFunction;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.CorrelationPloter;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.FabryPerotCaviry;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.Filter;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.GaussianFilter;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.HOMVisibilityCalculator;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.JointFunction;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.PumpFunction;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.QuasiPhaseMatchFunction;
import com.hwaipy.utilities.system.PathsUtilities;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

/**
 * @author Hwaipy 2015-3-19
 */
public class Dispersion {

  private static double minOmigaS = 1548.5;
  private static double maxOmigaS = 1551.5;
  private static double minOmigaI = 1548.5;
  private static double maxOmigaI = 1551.5;
  private static final Filter bandPass780_3 = new BandPassFilter(780, 3, 0);
  private static final Filter bandPass780_1 = new BandPassFilter(780, 1, 0);
  private static final Filter gaussian390_02 = new GaussianFilter(390, 0.2 / 2.35);
  private static final Filter gaussian390_0015 = new GaussianFilter(390, 0.015 / 2.35);
  private static final Filter gaussian780_0030 = new GaussianFilter(780, 0.030 / 2.35);
  private static final Filter fpEtalon390_0015 = new FabryPerotCaviry(0.855, 0.2535 / 1000);
  private static final Filter fpEtalon780_0030 = new FabryPerotCaviry(0.855, 0.507 / 1000);

  public static void main(String[] args) throws IOException {
    CorrelationFunction functionPump = new PumpFunction(775, 0.36);
//    functionPump.filterPump(gaussian390_02);
//    functionPump.filterPump(fpEtalon390_0015);
//    CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, 2, 7.9482);
    CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, 30, -45.04);
    CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
//    functionJoin.filterIdle(fpEtalon780_0030);
//    functionJoin.filterSignal(fpEtalon780_0030);
//    plot(functionPump, "pump");
//    plot(functionPhaseMatch, "phaseMatch");
//    plot(functionJoin, "filtered");
//    System.out.println(_HOM(functionJoin));
//    for (int i = -0; i >= -500; i--) {
//    System.out.println(HOM_traditional(functionJoin, 2e-12 * 5));
//    double delta = 10 * Math.pow(0.96, i);
    double delta = 2;
//      System.out.print(i + "\t" + (1.5 / 500 * i) + "\t");
    minOmigaS = 1550 - delta;
    maxOmigaS = 1550 + delta;
    minOmigaI = 1550 - delta;
    maxOmigaI = 1550 + delta;
    plot(functionJoin, "filtered");
//      System.out.println(_HOM(functionJoin));
//    System.out.println(HOM_dispertion(functionJoin, 40e-12));
    System.out.println(HOM_traditional(functionJoin, 5e-12));
//    }
  }

  private static void plot(CorrelationFunction function, String name) throws IOException {
    CorrelationPloter correlationPloter = new CorrelationPloter(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, 1000, 1000);
    correlationPloter.calculate();
    BufferedImage image = correlationPloter.createImage();
    Path path = PathsUtilities.getDataStoragyPath();
    ImageIO.write(image, "png", new File(path.toFile(), name + ".png"));
  }

  private static double _HOM(CorrelationFunction function) throws IOException {
    HOMVisibilityCalculator homCalculator = new HOMVisibilityCalculator(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, 200);
    double visibility = homCalculator.calculate();
//    System.out.println("HOM Visibility is " + visibility);
    return visibility;
  }

  private static double HOM_traditional(CorrelationFunction function, double delta) throws IOException {
    Integrator E = new TRADITIONAL.EIntegrator(function, minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, delta);
    Integrator A = new TRADITIONAL.AIntegrator(function, minOmigaS, maxOmigaS, minOmigaI, maxOmigaI);
    double e = E.integrate(0.0001);
    double a = A.integrate(0.0001);
    return e / a;
  }

  private static double HOM_dispertion(CorrelationFunction function, double delta) throws IOException {
    Integrator E = new DISPERSION.EIntegrator(function, minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, delta);
    Integrator A = new DISPERSION.AIntegrator(function, minOmigaS, maxOmigaS, minOmigaI, maxOmigaI);
    double e = E.integrate(0.0001);
    double a = A.integrate(0.0001);
//    System.out.println(delta + "\t" + (e / a));
    return e / a;
  }

  private static class DISPERSION {

    private static class EIntegrator extends Integrator {

      private final double delta;

      public EIntegrator(CorrelationFunction function, double min1, double max1, double min2, double max2, double delta) {
        super(function, min1, max1, min2, max2);
        this.delta = delta;
      }

      @Override
      protected double function() {
        double r1 = getNextRandom(min1, max1);
        double r1p = getNextRandom(min1, max1);
        double r2 = getNextRandom(min2, max2);
        double r2p = getNextRandom(min2, max2);
        return function.value(r1, r2) * function.value(r1 + delta, r2p) * function.value(r1p - delta, r2) * function.value(r1p, r2p);
      }

    }

    private static class AIntegrator extends Integrator {

      public AIntegrator(CorrelationFunction function, double min1, double max1, double min2, double max2) {
        super(function, min1, max1, min2, max2);
      }

      @Override
      protected double function() {
        double r1 = getNextRandom(min1, max1);
        double r1p = getNextRandom(min1, max1);
        double r2 = getNextRandom(min2, max2);
        double r2p = getNextRandom(min2, max2);
        double result = function.value(r1, r2) * function.value(r1p, r2p);
        return result * result;
      }

    }
  }

  private static class TRADITIONAL {

    private static class EIntegrator extends Integrator {

      private final double delta;

      public EIntegrator(CorrelationFunction function, double min1, double max1, double min2, double max2, double delta) {
        super(function, min1, max1, min2, max2);
        this.delta = delta;
      }

      @Override
      protected double function() {
        double r1 = getNextRandom(min1, max1);
        double r1p = getNextRandom(min1, max1);
        double r2 = getNextRandom(min2, max2);
        double r2p = getNextRandom(min2, max2);
        return function.value(r1, r2) * function.value(r1, r2p) * function.value(r1p, r2) * function.value(r1p, r2p)
                * cos(delta * (convert(r2) - convert(r2p)));
      }

      private double convert(double lambda) {
        lambda = lambda / 1e9;
        return 3e8 / lambda * 2 * Math.PI;
      }

      private double cos(double theta) {
        int d = (int) (theta / Math.PI);
        theta -= Math.PI * d;
        return Math.cos(theta);
      }

    }

    private static class AIntegrator extends Integrator {

      public AIntegrator(CorrelationFunction function, double min1, double max1, double min2, double max2) {
        super(function, min1, max1, min2, max2);
      }

      @Override
      protected double function() {
        double r1 = getNextRandom(min1, max1);
        double r1p = getNextRandom(min1, max1);
        double r2 = getNextRandom(min2, max2);
        double r2p = getNextRandom(min2, max2);
        double result = function.value(r1, r2) * function.value(r1p, r2p);
        return result * result;
      }

    }
  }
}
