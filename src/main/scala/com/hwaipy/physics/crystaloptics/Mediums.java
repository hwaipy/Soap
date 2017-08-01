package com.hwaipy.physics.crystaloptics;

import com.hwaipy.measure.quantity.WaveNumber;
import com.hwaipy.measure.unit.Units;
import com.hwaipy.physics.crystaloptics.refractivemodel.RefractiveModel;
import com.hwaipy.physics.crystaloptics.refractivemodel.SellmeierARefractiveModel;
import com.hwaipy.physics.crystaloptics.refractivemodel.SellmeierBRefractiveModel;
import com.hwaipy.physics.crystaloptics.refractivemodel.SellmeierCRefractiveModel;
import com.hwaipy.physics.crystaloptics.refractivemodel.VacuumRefractiveModel;
import java.util.HashMap;
import java.util.Map;
import javax.measure.quantity.Length;
import org.jscience.physics.amount.Amount;

/**
 *
 * @author Hwaipy
 */
public class Mediums {

    private static final Map<String, Medium> MEDIUMS = new HashMap<>();
    public static Medium VACUUM = newMedium("vacuum",
            VacuumRefractiveModel.INSTANCE,
            VacuumRefractiveModel.INSTANCE,
            VacuumRefractiveModel.INSTANCE);

    /**
     * 0.43~3.54µm
     *
     * Handbook of Optics, 3rd edition, Vol. 4. McGraw-Hill 2009
     */
    public static Medium KTiOPO4 = newMedium("KTiOPO4",
            new SellmeierARefractiveModel(3.29100, 0.04140, 0.03978, 9.35522, 31.45571),
            new SellmeierARefractiveModel(3.45018, 0.04341, 0.04597, 16.98825, 39.43799),
            new SellmeierARefractiveModel(4.59423, 0.06206, 0.04763, 110.80672, 86.12171));

    /**
     * 0.4~5µm
     *
     * D. E. Zelmon, D. L. Small, and D. Jundt. Infrared corrected Sellmeier
     * coefficients for congruently grown lithium niobate and 5 mol.% magnesium
     * oxide-doped lithium niobate, JOSA B 14, 3319-3322 (1997)
     * doi:10.1364/JOSAB.14.003319
     *
     * Handbook of Optics, 3rd edition, Vol. 4. McGraw-Hill 2009
     */
    public static Medium LiNbO3 = newMedium("LiNbO3",
            new SellmeierBRefractiveModel(2.6734, 0.01764, 1.2290, 0.05914, 12.614, 474.60),
            new SellmeierBRefractiveModel(2.9804, 0.02047, 0.5981, 0.0666, 8.9543, 416.08));

    /**
     * 0.22~1.06µm
     *
     * Handbook of Optics, 3rd edition, Vol. 4. McGraw-Hill 2009
     */
    public static Medium BBO = newMedium("BBO",
            new SellmeierCRefractiveModel(2.7405, 0.0184, 0.0179, -0.0155),
            new SellmeierCRefractiveModel(2.3730, 0.0128, 0.0156, -0.0044));

    private static Medium newMedium(String name, RefractiveModel refractiveModelX,
            RefractiveModel refractiveModelY, RefractiveModel refractiveModelZ) {
        Medium medium = new Medium(refractiveModelX, refractiveModelY, refractiveModelZ);
        MEDIUMS.put(name, medium);
        return medium;
    }

    private static Medium newMedium(String name, RefractiveModel refractiveModelO,
            RefractiveModel refractiveModelE) {
        Medium medium = new Medium(refractiveModelO, refractiveModelO, refractiveModelE);
        MEDIUMS.put(name, medium);
        return medium;
    }

    public static void main(String[] args) {
//        MonochromaticWave wave = MonochromaticWave.byWaveLength(Amount.valueOf(450, Units.NANOMETRE));
//        for (double lamda = 400; lamda < 2000; lamda += 1) {
//            MonochromaticWave waveP = MonochromaticWave.byWaveLength(Amount.valueOf(lamda, Units.NANOMETRE));
//            MonochromaticWave waveS = MonochromaticWave.byWaveLength(Amount.valueOf(lamda * 2, Units.NANOMETRE));
//            double ngP = LiNbO3.getGroupIndex(waveP, Axis.Z);
//            double ngS = LiNbO3.getGroupIndex(waveS, Axis.Y);
//            double ngI = LiNbO3.getGroupIndex(waveS, Axis.Z);
//            System.out.println(lamda + "\t" + (ngP - ngS) + "\t" + (ngP - ngI));
//        }

        //QPM
        MonochromaticWave pump = MonochromaticWave.byWaveLength(Amount.valueOf(405, Units.NANOMETRE));
        MonochromaticWave signal = MonochromaticWave.byWaveLength(Amount.valueOf(810, Units.NANOMETRE));
        MonochromaticWave idle = MonochromaticWave.byWaveLength(Amount.valueOf(810, Units.NANOMETRE));
        Amount<WaveNumber> kPump = pump.getWaveNumber(KTiOPO4, Axis.Z);
        Amount<WaveNumber> kSignal = signal.getWaveNumber(KTiOPO4, Axis.Z);
        Amount<WaveNumber> kIdle = idle.getWaveNumber(KTiOPO4, Axis.Z);
        Amount<WaveNumber> deltaK = kPump.minus(kSignal).minus(kIdle);
        Amount<Length> OMG = (Amount<Length>) Amount.ONE.times(2 * Math.PI).divide(deltaK);
        double omg = OMG.doubleValue(Units.MICROMETRE);
        System.out.println(omg);
    }
}
