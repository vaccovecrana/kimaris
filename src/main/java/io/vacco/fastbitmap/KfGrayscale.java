package io.vacco.fastbitmap;

/**
 * Base class for image gray scaling.
 * Supported types: RGB.
 * Coordinate System: Independent.
 *
 * @author Diego Catalano
 */
public class KfGrayscale {

  double redCoefficient = 0.2125, greenCoefficient = 0.7154, blueCoefficient = 0.0721;

  public enum Algorithm {
    /** (Max(red, green, blue) + Min(red, green, blue)) / 2 */
    Lightness,
    /** (red + green + blue) / 3 */
    Average,
    /** (red * green * blue) ^ 1/3 */
    GeometricMean,
    /** 0.2125R + 0.7154G + 0.0721B */
    Luminosity,
    /*** Min(red, green, max) */
    MinimumDecomposition,
    /** Max(red, green, blue) */
    MaximumDecomposition
  }

  private Algorithm grayscaleMethod;
  private boolean isAlgorithm;

  public KfGrayscale() {}

  /**
   * @param redCoefficient   Portion of red channel's value to use during conversion from RGB to grayscale.
   * @param greenCoefficient Portion of green channel's value to use during conversion from RGB to grayscale.
   * @param blueCoefficient  Portion of blue channel's value to use during conversion from RGB to grayscale.
   */
  public KfGrayscale(double redCoefficient, double greenCoefficient, double blueCoefficient) {
    this.redCoefficient = redCoefficient;
    this.greenCoefficient = greenCoefficient;
    this.blueCoefficient = blueCoefficient;
    this.isAlgorithm = false;
  }

  public KfGrayscale(Algorithm grayscaleMethod) {
    this.grayscaleMethod = grayscaleMethod;
    this.isAlgorithm = true;
  }

  public double getRedCoefficient() {
    return redCoefficient;
  }
  public void setRedCoefficient(double redCoefficient) {
    this.redCoefficient = redCoefficient;
  }

  public double getGreenCoefficient() {
    return greenCoefficient;
  }
  public void setGreenCoefficient(double greenCoefficient) {
    this.greenCoefficient = greenCoefficient;
  }

  public double getBlueCoefficient() {
    return blueCoefficient;
  }
  public void setBlueCoefficient(double blueCoefficient) {
    this.blueCoefficient = blueCoefficient;
  }

  public Algorithm getGrayscaleMethod() {
    return grayscaleMethod;
  }
  public void setGrayscaleMethod(Algorithm grayscaleMethod) {
    this.grayscaleMethod = grayscaleMethod;
  }

  public void applyInPlace(KfBitmap fastBitmap) {
    if (!isAlgorithm) {
      double r, g, b, gray;
      KfBitmap fb = new KfBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), KfColorSpace.Grayscale);

      int[] pixelsRGB = fastBitmap.getRGBData();
      byte[] pixelsG = fb.getGrayData();
      for (int i = 0; i < pixelsG.length; i++) {
        r = pixelsRGB[i] >> 16 & 0xFF;
        g = pixelsRGB[i] >> 8 & 0xFF;
        b = pixelsRGB[i] & 0xFF;

        gray = (r * redCoefficient + g * greenCoefficient + b * blueCoefficient);

        pixelsG[i] = (byte) gray;
      }

      fb.setGrayData(pixelsG);
      fastBitmap.setImage(fb);
    } else {
      apply(fastBitmap, this.grayscaleMethod);
    }
  }

  private void apply(KfBitmap fastBitmap, Algorithm grayMethod) {
    double r, g, b, gray;

    KfBitmap fb = new KfBitmap(fastBitmap.getWidth(), fastBitmap.getHeight(), KfColorSpace.Grayscale);
    int[] pixelsRGB = fastBitmap.getRGBData();
    byte[] pixelsG = fb.getGrayData();

    switch (grayMethod) {
      case Lightness:

        double max, min;
        for (int i = 0; i < pixelsG.length; i++) {
          r = pixelsRGB[i] >> 16 & 0xFF;
          g = pixelsRGB[i] >> 8 & 0xFF;
          b = pixelsRGB[i] & 0xFF;

          max = Math.max(r, g);
          max = Math.max(max, b);
          min = Math.min(r, g);
          min = Math.min(min, b);
          gray = (max + min) / 2;

          pixelsG[i] = (byte) gray;
        }
        break;

      case Average:
        for (int i = 0; i < pixelsG.length; i++) {
          r = pixelsRGB[i] >> 16 & 0xFF;
          g = pixelsRGB[i] >> 8 & 0xFF;
          b = pixelsRGB[i] & 0xFF;

          gray = (r + g + b) / 3;

          pixelsG[i] = (byte) gray;
        }
        break;

      case GeometricMean:
        for (int i = 0; i < pixelsG.length; i++) {
          r = pixelsRGB[i] >> 16 & 0xFF;
          g = pixelsRGB[i] >> 8 & 0xFF;
          b = pixelsRGB[i] & 0xFF;

          gray = Math.pow(r * g * b, 0.33);

          pixelsG[i] = (byte) gray;
        }
        break;

      case Luminosity:
        for (int i = 0; i < pixelsG.length; i++) {
          r = pixelsRGB[i] >> 16 & 0xFF;
          g = pixelsRGB[i] >> 8 & 0xFF;
          b = pixelsRGB[i] & 0xFF;

          gray = (r * 0.2125 + g * 0.7154 + b * 0.0721);

          pixelsG[i] = (byte) gray;
        }
        break;

      case MinimumDecomposition:
        for (int i = 0; i < pixelsG.length; i++) {
          gray = Math.min(pixelsRGB[i] >> 16 & 0xFF, pixelsRGB[i] >> 8 & 0xFF);
          gray = Math.min(gray, pixelsRGB[i] & 0xFF);

          pixelsG[i] = (byte) gray;
        }
        break;

      case MaximumDecomposition:
        for (int i = 0; i < pixelsG.length; i++) {
          gray = Math.max(pixelsRGB[i] >> 16 & 0xFF, pixelsRGB[i] >> 8 & 0xFF);
          gray = Math.max(gray, pixelsRGB[i] & 0xFF);

          pixelsG[i] = (byte) gray;
        }
        break;
    }

    fastBitmap.setImage(fb);
  }
}