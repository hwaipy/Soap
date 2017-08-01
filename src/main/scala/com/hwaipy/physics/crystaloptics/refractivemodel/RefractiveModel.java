package com.hwaipy.physics.crystaloptics.refractivemodel;

import com.hwaipy.physics.crystaloptics.MonochromaticWave;

/**
 *
 * @author Hwaipy
 */
public interface RefractiveModel {

    /**
     * @param monochromaticWave
     * @return
     */
    public double getIndex(MonochromaticWave monochromaticWave);

    /**
     * @param monochromaticWave
     * @return
     */
    public double getGroupIndex(MonochromaticWave monochromaticWave);
}
