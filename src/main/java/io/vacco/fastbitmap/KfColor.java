package io.vacco.fastbitmap;

import java.util.Objects;

/**
 * Represents RGB color.
 *
 * @author Diego Catalano
 */
public class KfColor {

  /**
   * Black.
   * R: 0  G: 0  B: 0
   */
  public final static KfColor Black = new KfColor(0, 0, 0);

  /**
   * Blue.
   * R: 0  G: 0  B: 255
   */
  public final static KfColor Blue = new KfColor(0, 0, 255);

  /**
   * Cyan.
   * R: 0  G: 255  B: 255
   */
  public final static KfColor Cyan = new KfColor(0, 255, 255);

  /**
   * Dark Gray.
   * R: 64  G: 64  B: 64
   */
  public final static KfColor DarkGray = new KfColor(64, 64, 64);

  /**
   * Gray.
   * R: 128  G: 128  B: 128
   */
  public final static KfColor Gray = new KfColor(128, 128, 128);

  /**
   * Green.
   * R: 0  G: 255  B: 0
   */
  public final static KfColor Green = new KfColor(0, 255, 0);

  /**
   * Light Gray.
   * R: 192  G: 192  B: 192
   */
  public final static KfColor LightGray = new KfColor(192, 192, 192);

  /**
   * Magenta.
   * R: 255  G: 0  B: 255
   */
  public final static KfColor Magenta = new KfColor(255, 0, 255);

  /**
   * Orange.
   * R: 255  G: 200  B: 0
   */
  public final static KfColor Orange = new KfColor(255, 200, 0);

  /**
   * Pink.
   * R: 255  G: 175  B: 175
   */
  public final static KfColor Pink = new KfColor(255, 175, 175);

  /**
   * Red.
   * R: 255  G: 0  B: 0
   */
  public final static KfColor Red = new KfColor(255, 0, 0);

  /**
   * Yellow.
   * R: 255  G: 200  B: 0
   */
  public final static KfColor Yellow = new KfColor(255, 200, 0);

  /**
   * White.
   * R: 255  G: 255  B: 255
   */
  public final static KfColor White = new KfColor(255, 255, 255);

  /**
   * Red channel's component.
   */
  public int r = 0;

  /**
   * Green channel's component.
   */
  public int g = 0;

  /**
   * Blue channel's component.
   */
  public int b = 0;

  /** Initialize a new instance of the Color class. */
  public KfColor() {}

  /**
   * Initialize a new instance of the Color class.
   *
   * @param red   Red component.
   * @param green Green component.
   * @param blue  Blue component.
   */
  public KfColor(int red, int green, int blue) {
    this.r = red;
    this.g = green;
    this.b = blue;
  }

  /**
   * Initialize a new instance of the Color class.
   *
   * @param rgb RGB array.
   */
  public KfColor(int[] rgb) {
    this.r = rgb[0];
    this.g = rgb[1];
    this.b = rgb[2];
  }

  /**
   * Initialize a new instance of the Color class.
   *
   * @param rgb Packed RGB.
   */
  public KfColor(int rgb) {
    this.r = rgb >> 16 & 0xFF;
    this.g = rgb >> 8 & 0xFF;
    this.b = rgb & 0xFF;
  }

  /**
   * Initialize a new instance of the Color class.
   *
   * @param hex Hex color representation.
   */
  public KfColor(String hex) {
    String t = hex.substring(5, 7);
    r = Integer.parseInt(hex.substring(1, 3), 16);
    g = Integer.parseInt(hex.substring(3, 5), 16);
    b = Integer.parseInt(hex.substring(5, 7), 16);
  }

  /**
   * Pack the rgb values into an int representation.
   *
   * @param red   Red channel's component.
   * @param green Green channel's component.
   * @param blue  Blue channel's component.
   * @return Packed RGB.
   */
  public static int toPackedRGB(int red, int green, int blue) {
    return red << 16 | green << 8 | blue;
  }

  /**
   * Count many colors has in the image.
   *
   * @param fastBitmap Image to be procesed.
   * @return Number of colors.
   */
  public static int count(KfBitmap fastBitmap) {

    if (fastBitmap.isGrayscale()) {

      byte[] maxColors = new byte[256];
      int colors = 0;

      byte[] data = fastBitmap.getGrayData();
      for (int i = 0; i < data.length; i++)
        maxColors[data[i]] = 1;

      for (int i = 0; i < 256; i++)
        if (maxColors[i] == 1) colors++;

      return colors;

    }

    byte[] maxColors = new byte[16777216];
    int colors = 0;

    int[] data = fastBitmap.getRGBData();
    for (int i = 0; i < data.length; i++)
      maxColors[data[i]] = 1;

    for (int i = 0; i < 16777216; i++)
      if (maxColors[i] == 1) colors++;

    return colors;
  }

  /**
   * Convert RGB to Hex representation.
   *
   * @param red   Red component.
   * @param green Green component.
   * @param blue  Blue component.
   * @return Hex color representation.
   */
  public static String toHex(int red, int green, int blue) {
    return String.format("#%02x%02x%02x", red, green, blue);
  }

  /**
   * Convert RGB to Hex representation.
   *
   * @return Hex color representation.
   */
  public String toHex() {
    return String.format("#%02x%02x%02x", r, g, b);
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof KfColor) {
      KfColor c2 = (KfColor) obj;
      return (r == c2.r) && (g == c2.g) && (b == c2.b);
    }
    return false;
  }

  @Override public int hashCode() { return Objects.hash(r, g, b); }

}