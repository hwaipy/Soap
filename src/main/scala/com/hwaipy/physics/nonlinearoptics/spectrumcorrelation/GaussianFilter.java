package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

/**
 *
 * @author Hwaipy
 */
public class GaussianFilter implements Filter {

    private final double center;
    private final double sigma;

    /**
     *
     * @param center
     * @param sigma 强度
     */
    public GaussianFilter(double center, double sigma) {
        this.center = center;
        this.sigma = sigma;
    }

    @Override
    public double transmittance(double lamda) {
        double result = Math.exp(-Math.pow((center - lamda) / sigma, 2) / 2);
        return result;
    }
}
