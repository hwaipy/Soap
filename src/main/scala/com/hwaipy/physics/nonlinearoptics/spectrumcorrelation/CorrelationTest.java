package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import com.hwaipy.measure.unit.Units;
import com.hwaipy.physics.crystaloptics.Axis;
import com.hwaipy.physics.crystaloptics.Medium;
import com.hwaipy.physics.crystaloptics.Mediums;
import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.QuasiPhaseMatchFunction.Type;
import com.hwaipy.utilities.system.PathsUtilities;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;

import org.jscience.physics.amount.Amount;

/**
 * @author Hwaipy
 */
public class CorrelationTest {

    private static final double minOmigaS = 770.0;
    private static final double maxOmigaS = 790.0;
    private static final double minOmigaI = 1293.0;
    private static final double maxOmigaI = 1313.0;
    private static final int width = 1000;
    private static final int height = 1000;
    private static final Filter bandPass780_3 = new BandPassFilter(780, 3, 0);
    private static final Filter bandPass780_1 = new BandPassFilter(780, 1, 0);
    private static final Filter gaussian390_02 = new GaussianFilter(390, 0.2 / 2.35);
    private static final Filter gaussian390_0015 = new GaussianFilter(390, 0.015 / 2.35);
    private static final Filter gaussian780_0030 = new GaussianFilter(780, 0.030 / 2.35);
    private static final Filter fpEtalon390_0015 = new FabryPerotCaviry(0.855, 0.2535 / 1000);
    private static final Filter fpEtalon780_0030 = new FabryPerotCaviry(0.855, 0.507 / 1000);

    public static void main(String[] args) throws IOException {
//    calcPeriod();
//    correlation405to810TypeII("results/405to810TypeII/");
//    correlation405to810Type0("results/405to810Type0/");
//    correlation532To810And1550Type0("results/532.810.1550/");
//    correlation488To780And1303Type0("results/488.780.1303/");

        Medium medium = Mediums.KTiOPO4;
        MonochromaticWave wave = MonochromaticWave.byWaveLength(Amount.valueOf(840, Units.NANOMETRE));
        System.out.println(medium.getGroupIndex(wave, Axis.Z));
    }

    private static void calcPeriod() {
        double lamdaSignal = 780;
        double lamdaIdler = 1303.5;
        double lamdaPump = 1 / (1 / lamdaSignal + 1 / lamdaIdler);
        Medium medium = Mediums.KTiOPO4;
        Type type = Type.O;
        MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaPump, Units.NANOMETRE));
        MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaSignal, Units.NANOMETRE));
        MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(lamdaIdler, Units.NANOMETRE));
        double kPump = pump.getWaveNumber(medium, type.getAxis(0)).doubleValue(Units.RECIPROCALMETRE);
        double kSignal = signal.getWaveNumber(medium, type.getAxis(1)).doubleValue(Units.RECIPROCALMETRE);
        double kIdler = idle.getWaveNumber(medium, type.getAxis(2)).doubleValue(Units.RECIPROCALMETRE);
        double period = 2 * Math.PI / (kPump - kSignal - kIdler);
        System.out.println(period);
    }

    private static void correlation405to810TypeII(String resultDir) throws IOException {
        CorrelationFunction functionPump = new PumpFunction(405, 0.01);
        plot(functionPump, resultDir + "pump");
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, QuasiPhaseMatchFunction.Type.II, 15, 10.105);
        plot(functionPhaseMatch, resultDir + "phaseMatch");
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        plot(functionJoin, resultDir + "join");
    }

    private static void correlation405to810Type0(String resultDir) throws IOException {
        CorrelationFunction functionPump = new PumpFunction(405, 0.01);
        plot(functionPump, resultDir + "pump");
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, QuasiPhaseMatchFunction.Type.O, 15, 3.516);
        plot(functionPhaseMatch, resultDir + "phaseMatch");
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        plot(functionJoin, resultDir + "join");
    }

    private static void correlation532To810And1550Type0(String resultDir) throws IOException {
        CorrelationFunction functionPump = new PumpFunction(532, 0.01);
        plot(functionPump, resultDir + "pump");
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, QuasiPhaseMatchFunction.Type.O, 15, 9.77);
        plot(functionPhaseMatch, resultDir + "phaseMatch");
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        plot(functionJoin, resultDir + "join");
    }

    private static void correlation488To780And1303Type0(String resultDir) throws IOException {
        CorrelationFunction functionPump = new PumpFunction(488, 0.01);
        plot(functionPump, resultDir + "pump");
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, QuasiPhaseMatchFunction.Type.O, 30, 7.177);
        plot(functionPhaseMatch, resultDir + "phaseMatch");
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        plot(functionJoin, resultDir + "join");
    }

    public static void mainOld(String[] args) throws IOException {
        CorrelationFunction functionPump = new PumpFunction(390, 1.6);
        functionPump.filterPump(gaussian390_02);
        functionPump.filterPump(fpEtalon390_0015);
        plot(functionPump, "pump");
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, 10, 7.9482);
        plot(functionPhaseMatch, "phaseMatch");
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        plot(functionJoin, "join");
////        functionJoin.filterSignal(bandPass780_1);
////        functionJoin.filterSignal(bandPass780_3);
////        functionJoin.filterIdle(bandPass780_3);
////        functionJoin.filterSignal(fpEtalon780_0030);
////    functionJoin.filterIdle(fpEtalon780_0030);
//    functionJoin.filterSignal(fpEtalon780_0030);
////    functionJoin.filterSignal(gaussian780_0030);
////    functionJoin.filterIdle(gaussian780_0030);
////    functionJoin.filterSignal(new BandPassFilter(780, 0.20, 0));
////    functionJoin.filterIdle(new BandPassFilter(780, 0.20, 0));
//    plot(functionJoin, "filtered");
////    purity(functionJoin);
////
//    HOM(functionJoin);
    }

    private static void plot(CorrelationFunction function, String name) throws IOException {
        CorrelationPloter correlationPloter = new CorrelationPloter(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, width, height);
        correlationPloter.calculate();
        BufferedImage image = correlationPloter.createImage();
        Path path = PathsUtilities.getDataStoragyPath();
        ImageIO.write(image, "png", new File(path.toFile(), name + ".png"));
//    double[] statistics1 = correlationPloter.statistics(true);
//    double[] statistics2 = correlationPloter.statistics(false);
//    for (int i = 0; i < statistics1.length; i++) {
//      System.out.println(statistics1[i] + "\t" + statistics2[i]);
//    }
    }

    private static void purity(CorrelationFunction function) throws IOException {
        PurityCalculator purityCalculator = new PurityCalculator(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, width, height);
        double purity = purityCalculator.calculate(true);
        System.out.println("Purity is " + purity);
        System.out.println("g2 = " + (1 + purity));
    }

    private static void HOM(CorrelationFunction function) throws IOException {
        HOMVisibilityCalculator homCalculator = new HOMVisibilityCalculator(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, width);
        double visibility = homCalculator.calculate();
        System.out.println("HOM Visibility is " + visibility);
    }
}
