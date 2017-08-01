package com.hwaipy.physics.crystaloptics.refractivemodel;

import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import com.hwaipy.measure.unit.Units;

/**
 *
 * n<sup>2</sup> = A + B / (λ<sup>2</sup> - C) + D / (λ<sup>2</sup> - E)
 *
 * @author Hwaipy
 */
public class SellmeierARefractiveModel implements RefractiveModel {

    private final double A;
    private final double B;
    private final double C;
    private final double D;
    private final double E;

    /**
     * n<sup>2</sup> = A + B / (λ<sup>2</sup> - C) + D / (λ<sup>2</sup> - E)
     *
     * @param A
     * @param B
     * @param C
     * @param D
     * @param E
     */
    public SellmeierARefractiveModel(double A, double B, double C, double D, double E) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.E = E;
    }

    @Override
    public double getIndex(MonochromaticWave monochromaticWave) {
        double lamda = monochromaticWave.getWaveLength().doubleValue(Units.MICROMETRE);
        double lamda2 = lamda * lamda;
        double n2 = A + B / (lamda2 - C) + D / (lamda2 - E);
        return Math.sqrt(n2);
    }

    @Override
    public double getGroupIndex(MonochromaticWave monochromaticWave) {
        double lamda = monochromaticWave.getWaveLength().doubleValue(Units.MICROMETRE);
        double lamda2 = lamda * lamda;
        double n = getIndex(monochromaticWave);
        double dndlamda = -lamda / n * (B / (Math.pow(lamda2 - C, 2)) + D / (Math.pow(lamda2 - E, 2)));
        double result = 1 / ((1 + lamda / n * dndlamda) / n);
        return result;
    }
}
