package com.hwaipy.measure.quantity;

import com.hwaipy.measure.unit.Units;
import javax.measure.quantity.Quantity;
import javax.measure.unit.Unit;

public interface WaveNumber extends Quantity {

    /**
     * Holds the SI unit (Système International d'Unités) for this quantity.
     */
    public final static Unit<WaveNumber> UNIT = Units.RECIPROCALMETRE;

}
