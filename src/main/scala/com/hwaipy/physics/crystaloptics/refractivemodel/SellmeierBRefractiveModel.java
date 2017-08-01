package com.hwaipy.physics.crystaloptics.refractivemodel;

import com.hwaipy.physics.crystaloptics.MonochromaticWave;
import com.hwaipy.measure.unit.Units;

/**
 *
 * n<sup>2</sup> - 1 = Aλ<sup>2</sup> / (λ<sup>2</sup> - B) + Cλ<sup>2</sup> /
 * (λ<sup>2</sup> - D) + Eλ<sup>2</sup> / (λ<sup>2</sup> - F)
 *
 * @author Hwaipy
 */
public class SellmeierBRefractiveModel implements RefractiveModel {

    private final double A;
    private final double B;
    private final double C;
    private final double D;
    private final double E;
    private final double F;

    /**
     * n<sup>2</sup> - 1 = Aλ<sup>2</sup> / (λ<sup>2</sup> - B) + Cλ<sup>2</sup>
     * / (λ<sup>2</sup> - D) + Eλ<sup>2</sup> / (λ<sup>2</sup> - F)
     *
     * @param A
     * @param B
     * @param C
     * @param D
     * @param E
     * @param F
     */
    public SellmeierBRefractiveModel(double A, double B, double C, double D, double E, double F) {
        this.A = A;
        this.B = B;
        this.C = C;
        this.D = D;
        this.E = E;
        this.F = F;
    }

    @Override
    public double getIndex(MonochromaticWave monochromaticWave) {
        double lamda = monochromaticWave.getWaveLength().doubleValue(Units.MICROMETRE);
        double lamda2 = lamda * lamda;
        double n2 = 1 + A * lamda2 / (lamda2 - B) + C * lamda2 / (lamda2 - D) + E * lamda2 / (lamda2 - F);
        return Math.sqrt(n2);
    }

    @Override
    public double getGroupIndex(MonochromaticWave monochromaticWave) {
        double lamda = monochromaticWave.getWaveLength().doubleValue(Units.MICROMETRE);
        double lamda2 = lamda * lamda;
        double n = getIndex(monochromaticWave);
        double dndlamda = -lamda / n * (A * B / (Math.pow(lamda2 - B, 2)) + C * D / (Math.pow(lamda2 - D, 2)) + E * F / (Math.pow(lamda2 - F, 2)));
        double result = 1 / ((1 + lamda / n * dndlamda) / n);
        return result;
    }
}
