/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.lab;

import com.hwaipy.physics.nonlinearoptics.spectrumcorrelation.CorrelationFunction;
import java.util.Random;

/**
 * @author Hwaipy 2015-3-19
 */
public abstract class Integrator {

  protected final double min1;
  protected final double max1;
  protected final double min2;
  protected final double max2;
  protected final CorrelationFunction function;
  private final Random random = new Random();

  public Integrator(CorrelationFunction function, double min1, double max1, double min2, double max2) {
    this.function = function;
    this.min1 = min1;
    this.max1 = max1;
    this.min2 = min2;
    this.max2 = max2;
  }

  public double integrate(double stopBy) {
    double[] valueSerial = new double[1000];
    int c = 0;
    while (true) {
      c++;
      double base = updateResult();
//      System.out.println(c + "\t" + base);
      double delta = base * stopBy;
      double max = base + delta;
      double min = base - delta;
      for (int i = 0; i < valueSerial.length; i++) {
        valueSerial[i] = updateResult();
      }
      boolean stop = true;
      for (double value : valueSerial) {
        if (value < min || value > max) {
          stop = false;
          break;
        }
      }
      if (stop) {
        break;
      }
    }
    return valueSerial[valueSerial.length - 1] / (max1 - min1) / (max2 - min2);
  }

  private double resultSum = 0;
  private int resultCount = 0;

  private double updateResult() {
    double r = function();
    resultSum += r;
    resultCount++;
    return resultSum / resultCount;
  }

  protected abstract double function();

  protected double getNextRandom(double min, double max) {
    double r = random.nextDouble();
    return min + (max - min) * r;
  }

}
