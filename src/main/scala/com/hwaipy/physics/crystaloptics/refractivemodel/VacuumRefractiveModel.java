package com.hwaipy.physics.crystaloptics.refractivemodel;

import com.hwaipy.physics.crystaloptics.MonochromaticWave;

/**
 *
 * @author Hwaipy
 */
public class VacuumRefractiveModel implements RefractiveModel {

    @Override
    public double getIndex(MonochromaticWave monochromaticWave) {
        return 1;
    }

    @Override
    public double getGroupIndex(MonochromaticWave monochromaticWave) {
        return 1;
    }
    public static VacuumRefractiveModel INSTANCE = new VacuumRefractiveModel();
}
