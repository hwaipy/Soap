package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

/**
 *
 * @author Hwaipy
 */
public class PumpFunction extends CorrelationFunction {

  public PumpFunction(double wavelength, double FWHM) {
    filterPump(new GaussianFilter(wavelength, FWHM / 2.35));
  }

  @Override
  public double correlationValue(double lamdaSignal, double lamdaIdler) {
    return 1;
  }

}
