package com.hwaipy.physics.crystaloptics;

import com.hwaipy.physics.crystaloptics.refractivemodel.RefractiveModel;

/**
 *
 * @author Hwaipy
 */
public class Medium {

    private final RefractiveModel[] refractiveModels;

    public Medium(RefractiveModel refractiveModelX, RefractiveModel refractiveModelY, RefractiveModel refractiveModelZ) {
        this.refractiveModels = new RefractiveModel[]{refractiveModelX, refractiveModelY, refractiveModelZ};
    }

    public double getIndex(MonochromaticWave monochromaticWave, Axis axis) {
        RefractiveModel model = refractiveModels[axis.ordinal()];
        return model.getIndex(monochromaticWave);
    }

    public double getGroupIndex(MonochromaticWave monochromaticWave, Axis axis) {
        RefractiveModel model = refractiveModels[axis.ordinal()];
        return model.getGroupIndex(monochromaticWave);
    }

    public double getAbbeNumberVd(Axis axis) {
        double nd = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_d, axis);
        double nF = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_F, axis);
        double nC = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_C, axis);
        return (nd - 1) / (nF - nC);
    }

    public double getAbbeNumberVe(Axis axis) {
        double ne = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_e, axis);
        double nFp = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_Fp, axis);
        double nCp = getIndex(CharacteristicLines.MONOCHROMATIC_WAVE_Cp, axis);
        return (ne - 1) / (nFp - nCp);
    }
}
