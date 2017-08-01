package com.hwaipy.physics.crystaloptics;

import com.hwaipy.measure.quantity.WaveNumber;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Length;
import org.jscience.physics.amount.Amount;
import org.jscience.physics.amount.Constants;

/**
 *
 * @author Hwaipy
 */
public class MonochromaticWave {

    private final Amount<Length> waveLength;
    private final Amount<Frequency> frequency;
    private final Amount<Frequency> angularFrequency;

    private MonochromaticWave(Amount<Length> waveLength, Amount<Frequency> frequency, Amount<Frequency> angularFrequency) {
        this.waveLength = waveLength;
        this.frequency = frequency;
        this.angularFrequency = angularFrequency;
    }

    public Amount<Length> getWaveLength() {
        return waveLength;
    }

    public Amount<Frequency> getFrequency() {
        return frequency;
    }

    public Amount<Frequency> getAngularFrequency() {
        return angularFrequency;
    }

    public Amount<WaveNumber> getWaveNumber(Medium medium, Axis axis) {
        double n = medium.getIndex(this, axis);
        return (Amount<WaveNumber>) Amount.ONE.times(2 * Math.PI * n).divide(getWaveLength());
    }

    public static MonochromaticWave byWaveLength(Amount<Length> waveLength) {
        Amount<Frequency> frequency = (Amount<Frequency>) Constants.c.divide(waveLength);
        Amount<Frequency> angularFrequency = frequency.times(2 * Math.PI);
        return new MonochromaticWave(waveLength, frequency, angularFrequency);
    }

    public static MonochromaticWave byFrequency(Amount<Frequency> frequency) {
        Amount<Length> waveLength = (Amount<Length>) Constants.c.divide(frequency);
        Amount<Frequency> angularFrequency = frequency.times(2 * Math.PI);
        return new MonochromaticWave(waveLength, frequency, angularFrequency);
    }

    public static MonochromaticWave byAngularFrequency(Amount<Frequency> angularFrequency) {
        Amount<Frequency> frequency = angularFrequency.divide(2 * Math.PI);
        Amount<Length> waveLength = (Amount<Length>) Constants.c.divide(frequency);
        return new MonochromaticWave(waveLength, frequency, angularFrequency);
    }
}
