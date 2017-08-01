package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import com.hwaipy.measure.unit.Units;
import com.hwaipy.physics.crystaloptics.Mediums;
import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import com.hwaipy.utilities.system.PathsUtilities;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.measure.quantity.Frequency;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Hwaipy
 */
public class CorrelationCompare {

    private static final double minOmigaS = 779.5;
    private static final double maxOmigaS = 780.5;
    private static final double minOmigaI = 779.5;
    private static final double maxOmigaI = 780.5;
    private static final int width = 200;
    private static final int height = 200;
    private static final Filter bandPass780_3 = new BandPassFilter(780, 3, 0);
    private static final Filter bandPass780_1 = new BandPassFilter(780, 1, 0);
    private static final Filter gaussian390_02 = new GaussianFilter(390, 0.2 / 2.35);
    private static final Filter gaussian390_0015 = new GaussianFilter(390, 0.015 / 2.35);
    private static final Filter gaussian780_0030 = new GaussianFilter(780, 0.030 / 2.35);
    private static final Filter fpEtalon390_0015 = new FabryPerotCaviry(0.855, 0.2535 / 1000);
    private static final Filter fpEtalon780_0030 = new FabryPerotCaviry(0.855, 0.507 / 1000);

    public static void main(String[] args) throws IOException {
        double sigmaPumpA = 0.968;//nm
        double sigmaPumpI = sigmaPumpA / Math.sqrt(2);
        double FWHMPumpI = sigmaPumpI * 2.35;
//        System.out.println(FWHMPumpI);
        CorrelationFunction functionPump = new PumpFunction(390, FWHMPumpI);
        CorrelationFunction functionPhaseMatch = new QuasiPhaseMatchFunction(Mediums.KTiOPO4, 1, 7.9482);
        CorrelationFunction functionJoin = new JointFunction(functionPhaseMatch, functionPump);
        MonochromaticWave pdcCenter = MonochromaticWave.byWaveLength(Amount.valueOf(780, Units.NANOMETRE));
        Amount<Frequency> pdcCenterOmega = pdcCenter.getAngularFrequency();
        Amount<Frequency> deltaOmega = Amount.valueOf(0.76927, Units.TERAHERTZ);
        Amount<Frequency> pdcOmega1 = pdcCenterOmega.plus(deltaOmega);
        Amount<Frequency> pdcOmega2 = pdcOmega1.plus(deltaOmega);
        double pdcWL1 = MonochromaticWave.byAngularFrequency(pdcOmega1).getWaveLength().doubleValue(Units.NANOMETRE);
        double pdcWL2 = MonochromaticWave.byAngularFrequency(pdcOmega2).getWaveLength().doubleValue(Units.NANOMETRE);
//        System.out.println(pdcWL);

//        double v = functionJoin.value(pdcWL, pdcWL);
//        System.out.println(v);
//        System.out.println("Pump: " + functionPump.value(pdcWL, pdcWL));
        HOMVisibilityCalculator homCalculator = new HOMVisibilityCalculator(pdcWL2, pdcWL1, pdcWL2, pdcWL1, functionJoin, 2);
        double visibility = homCalculator.calculate();
        System.out.println("HOM Visibility is " + visibility);
//        HOM(functionJoin);
    }

    private static void plot(CorrelationFunction function, String name) throws IOException {
        CorrelationPloter correlationPloter = new CorrelationPloter(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, width, height);
        correlationPloter.calculate();
        BufferedImage image = correlationPloter.createImage();
        Path path = PathsUtilities.getDataStoragyPath();
        ImageIO.write(image, "png", new File(path.toFile(), name + ".png"));
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

    private static void HOMTest(CorrelationFunction function, double lamda) throws IOException {
//        HOMVisibilityCalculator homCalculator = new HOMVisibilityCalculator(minOmigaS, maxOmigaS, minOmigaI, maxOmigaI, function, width);
//        double visibility = homCalculator.calculate();
//        System.out.println("HOM Visibility is " + visibility);
        double c = function.value(lamda, lamda);
        double v = (c * c * c * c);
        System.out.println(v);
    }
}
