package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

/**
 *
 * @author Hwaipy
 */
public class BandPassFilter implements Filter {

    private final double center;
    private final double halfWidth;
    private final double edge;

    public BandPassFilter(double center, double width, double edge) {
        this.center = center;
        this.halfWidth = width / 2;
        this.edge = edge;
    }

    @Override
    public double transmittance(double lamda) {
        double delta = Math.abs(lamda - center);
        if (delta < halfWidth) {
            return 1;
        }
        double overflow = delta - halfWidth;
        if (overflow < edge) {
            return (1 - overflow / edge);
        }
        return 0;
    }
}
