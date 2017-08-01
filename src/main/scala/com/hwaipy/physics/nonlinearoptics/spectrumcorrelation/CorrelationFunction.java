package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import java.util.ArrayList;

/**
 *
 * @author Hwaipy
 */
public abstract class CorrelationFunction {

  private final ArrayList<Filter> pumpFilters = new ArrayList<>();
  private final ArrayList<Filter> signalFilters = new ArrayList<>();
  private final ArrayList<Filter> idleFilters = new ArrayList<>();

  public void filterPump(Filter filter) {
    pumpFilters.add(filter);
  }

  public void filterSignal(Filter filter) {
    signalFilters.add(filter);
  }

  public void filterIdle(Filter filter) {
    idleFilters.add(filter);
  }

  public double value(double signalLamda, double idleLamda) {
    double result = correlationValue(signalLamda, idleLamda);
    double pumpLamda = 1 / (1 / signalLamda + 1 / idleLamda);
    result = pumpFilters.stream().map((pumpFilter)
            -> Math.sqrt(pumpFilter.transmittance(pumpLamda))).
            reduce(result, (accumulator, _item) -> accumulator * _item);
    result = signalFilters.stream().map((signalFilter)
            -> Math.sqrt(signalFilter.transmittance(signalLamda)))
            .reduce(result, (accumulator, _item) -> accumulator * _item);
    result = idleFilters.stream().map((idleFilter)
            -> Math.sqrt(idleFilter.transmittance(idleLamda)))
            .reduce(result, (accumulator, _item) -> accumulator * _item);
    return result;
  }

  protected abstract double correlationValue(double signalLamda, double idleLamda);

}
