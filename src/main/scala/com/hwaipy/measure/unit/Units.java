package com.hwaipy.measure.unit;

import com.hwaipy.measure.quantity.WaveNumber;

import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

import static javax.measure.unit.SI.*;

/**
 * <p>
 * This class contains useful units which are not defined in JScience..</p>
 *
 * @author hwaipy
 * @see javax.measure.unit.SI
 * @see javax.measure.unit.NonSI
 */
public class Units {

    public static final Unit<Length> MICROMETRE = METRE.divide(1e6);
    public static final Unit<Length> NANOMETRE = METRE.divide(1e9);
    public static final Unit<Length> PICOMETRE = METRE.divide(1e12);
    public static final Unit<Frequency> GIGAHERTZ = HERTZ.times(1e9);
    public static final Unit<Frequency> TERAHERTZ = HERTZ.times(1e12);
    public static final Unit<WaveNumber> RECIPROCALMETRE = (Unit<WaveNumber>) Unit.ONE.divide(SI.METRE);
}
