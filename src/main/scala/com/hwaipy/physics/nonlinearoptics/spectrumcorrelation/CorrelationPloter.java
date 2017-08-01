package com.hwaipy.physics.nonlinearoptics.spectrumcorrelation;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author Hwaipy
 */
public class CorrelationPloter {

  private final double minArg1;
  private final double maxArg1;
  private final double minArg2;
  private final double maxArg2;
  private final double[][] results;
  private final CorrelationFunction function;
  private final int width;
  private final int height;

  public CorrelationPloter(double minArg1, double maxArg1, double minArg2, double maxArg2,
          CorrelationFunction function, int width, int height) {
    this.minArg1 = minArg1;
    this.maxArg1 = maxArg1;
    this.minArg2 = minArg2;
    this.maxArg2 = maxArg2;
    results = new double[height][width];
    this.function = function;
    this.width = width;
    this.height = height;
  }

  private int maxValueOfX = -1;
  private int maxValueOfY = -1;
  private double maxValue = Double.MIN_VALUE;
  private int minValueOfX = -1;
  private int minValueOfY = -1;
  private double minValue = Double.MAX_VALUE;

  public void calculate() {
    double stepOfArg1 = (maxArg1 - minArg1) / (width - 1);
    double stepOfArg2 = (maxArg2 - minArg2) / (height - 1);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double arg1 = minArg1 + x * stepOfArg1;
        double arg2 = maxArg2 - y * stepOfArg2;
        double result = function.value(arg1, arg2);
        results[y][x] = result;
        if (result > maxValue) {
          maxValue = result;
          maxValueOfX = x;
          maxValueOfY = y;
        }
        if (result < minValue) {
          minValue = result;
          minValueOfX = x;
          minValueOfY = y;
        }
      }
    }
  }

  public BufferedImage createImage() {
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double result = results[y][x];
        result *= result;
//                int gray = (int) ((maxValue - result) / (maxValue - minValue) * 255);
        int gray = (int) ((1 - result) * 255);
        image.setRGB(x, y, new Color(gray, gray, gray).getRGB());
      }
    }
    return image;
  }

  public double[] statistics(boolean horizontal) {
    double[] result = new double[horizontal ? width : height];
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        result[horizontal ? x : y] += results[y][x] * results[y][x];
      }
    }
    return result;
  }

  public double totalIntensity() {
    double result = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        result += results[y][x];
      }
    }
    return result;
  }

  public double filtedIntensity(double centerX, double centerY, double r) {
    double stepOfArg1 = (maxArg1 - minArg1) / (width - 1);
    double stepOfArg2 = (maxArg2 - minArg2) / (height - 1);
    BufferedImage image = createImage();
    double result = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        double argX = minArg1 + x * stepOfArg1;
        double argY = maxArg2 - y * stepOfArg2;
        double distanceX = argX - centerX;
        double distanceY = argY - centerY;
        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY);
        if (distance <= r) {
          result += results[y][x];
          image.setRGB(x, y, Color.RED.brighter().getRGB());
        }
      }
    }
    try {
      ImageIO.write(image, "png", new File("test.png"));
    } catch (IOException ex) {
      Logger.getLogger(CorrelationPloter.class.getName()).log(Level.SEVERE, null, ex);
    }
    return result;
  }

}
