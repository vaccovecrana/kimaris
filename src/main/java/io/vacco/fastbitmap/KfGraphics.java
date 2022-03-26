package io.vacco.fastbitmap;

/**
 * Fast Graphics.
 * Allows drawing over Fast Bitmap.
 *
 * @author Diego Catalano
 */
public class KfGraphics {

  private KfBitmap fastBitmap;
  private KfColor color;
  private int gray = 0;

  /**
   * Initialize a new instance of the FastGraphics class.
   *
   * @param fastBitmap Image to be processed.
   */
  public KfGraphics(KfBitmap fastBitmap) {
    this.fastBitmap = fastBitmap;
  }

  /**
   * Draw Circle.
   *
   * @param x      X axis coordinate.
   * @param y      Y axis coordinate.
   * @param radius Radius.
   */
  public void drawCircle(int x, int y, int radius) {

    if (fastBitmap.isRGB()) {
      int i = radius, j = 0;
      int radiusError = 1 - i;

      while (i >= j) {
        fastBitmap.setRGB(i + x, j + y, color.r, color.g, color.b);
        fastBitmap.setRGB(j + x, i + y, color.r, color.g, color.b);
        fastBitmap.setRGB(-i + x, j + y, color.r, color.g, color.b);
        fastBitmap.setRGB(-j + x, i + y, color.r, color.g, color.b);
        fastBitmap.setRGB(-i + x, -j + y, color.r, color.g, color.b);
        fastBitmap.setRGB(-j + x, -i + y, color.r, color.g, color.b);
        fastBitmap.setRGB(i + x, -j + y, color.r, color.g, color.b);
        fastBitmap.setRGB(j + x, -i + y, color.r, color.g, color.b);

        j++;
        if (radiusError < 0)

          radiusError += 2 * j + 1;
        else {
          i--;
          radiusError += 2 * (j - i + 1);
        }
      }
    } else {
      int i = radius, j = 0;
      int radiusError = 1 - i;

      while (i >= j) {
        fastBitmap.setGray(i + x, j + y, gray);
        fastBitmap.setGray(j + x, i + y, gray);
        fastBitmap.setGray(-i + x, j + y, gray);
        fastBitmap.setGray(-j + x, i + y, gray);
        fastBitmap.setGray(-i + x, -j + y, gray);
        fastBitmap.setGray(-j + x, -i + y, gray);
        fastBitmap.setGray(i + x, -j + y, gray);
        fastBitmap.setGray(j + x, -i + y, gray);

        j++;
        if (radiusError < 0)

          radiusError += 2 * j + 1;
        else {
          i--;
          radiusError += 2 * (j - i + 1);
        }
      }
    }
  }

  /**
   * http://tech-algorithm.com/articles/drawing-line-using-bresenham-algorithm/
   *
   * @param x  X axis coordinate.
   * @param y  Y axis coordinate.
   * @param x2 X2 axis coordinate.
   * @param y2 Y2 axis coordinate.
   */
  public void drawLine(int x, int y, int x2, int y2) {

    if (fastBitmap.isRGB()) {
      int w = x2 - x;
      int h = y2 - y;
      int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
      if (w < 0) dx1 = -1;
      else if (w > 0) dx1 = 1;
      if (h < 0) dy1 = -1;
      else if (h > 0) dy1 = 1;
      if (w < 0) dx2 = -1;
      else if (w > 0) dx2 = 1;
      int longest = Math.abs(w);
      int shortest = Math.abs(h);
      if (!(longest > shortest)) {
        longest = Math.abs(h);
        shortest = Math.abs(w);
        if (h < 0) dy2 = -1;
        else if (h > 0) dy2 = 1;
        dx2 = 0;
      }
      int numerator = longest >> 1;
      for (int i = 0; i <= longest; i++) {
        fastBitmap.setRGB(x, y, color.r, color.g, color.b);
        numerator += shortest;
        if (!(numerator < longest)) {
          numerator -= longest;
          x += dx1;
          y += dy1;
        } else {
          x += dx2;
          y += dy2;
        }
      }
    } else {
      int w = x2 - x;
      int h = y2 - y;
      int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0;
      if (w < 0) dx1 = -1;
      else if (w > 0) dx1 = 1;
      if (h < 0) dy1 = -1;
      else if (h > 0) dy1 = 1;
      if (w < 0) dx2 = -1;
      else if (w > 0) dx2 = 1;
      int longest = Math.abs(w);
      int shortest = Math.abs(h);
      if (!(longest > shortest)) {
        longest = Math.abs(h);
        shortest = Math.abs(w);
        if (h < 0) dy2 = -1;
        else if (h > 0) dy2 = 1;
        dx2 = 0;
      }
      int numerator = longest >> 1;
      for (int i = 0; i <= longest; i++) {
        fastBitmap.setGray(x, y, gray);
        numerator += shortest;
        if (!(numerator < longest)) {
          numerator -= longest;
          x += dx1;
          y += dy1;
        } else {
          x += dx2;
          y += dy2;
        }
      }
    }
  }

  /**
   * Draw image over an image.
   *
   * @param image Image.
   * @param x     X axis coordinate.
   * @param y     Y axis coordinate.
   */
  public void drawImage(KfBitmap image, int x, int y) {
    int width = image.getWidth();
    int height = image.getHeight();

    if (image.getCoordinateSystem() == KfCoordinateSystem.Matrix) {
      if (image.isGrayscale()) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setGray(i + x, j + y, image.getGray(i, j));
          }
        }
      } else if (image.isRGB()) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setRGB(i + x, j + y, image.getRGB(i, j));
          }
        }
      } else {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setARGB(i + x, j + y, image.getARGB(i, j));
          }
        }
      }
    } else {
      if (image.isGrayscale()) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setGray(j + x, i + y, image.getGray(j, i));
          }
        }
      } else if (image.isRGB()) {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setRGB(j + x, i + y, image.getRGB(j, i));
          }
        }
      } else {
        for (int i = 0; i < height; i++) {
          for (int j = 0; j < width; j++) {
            fastBitmap.setARGB(j + x, i + y, image.getARGB(j, i));
          }
        }
      }
    }
  }

  /**
   * Draw Polygon.
   *
   * @param x X axis coordinate.
   * @param y Y axis coordinate.
   */
  public void drawPolygon(int[] x, int[] y) {
    drawPolygon(x, y, x.length);
  }

  /**
   * Draw Polygon.
   *
   * @param x X axis coordinate.
   * @param y Y axis coordinate.
   * @param n Number of points.
   */
  public void drawPolygon(int[] x, int[] y, int n) {
    if (x.length > 2 && y.length > 2) {
      if (x.length == y.length) {
        for (int i = 1; i < n; i++) {
          drawLine(x[i], y[i], x[i - 1], y[i - 1]);
        }
        drawLine(x[n - 1], y[n - 1], x[0], y[0]);
      } else {
        throw new IllegalArgumentException("Draw Polygon: X and Y must be the same size.");
      }
    } else {
      throw new IllegalArgumentException("Draw Polygon: X and Y needs at least 3 points.");
    }
  }

  /**
   * Draw Rectangle.
   *
   * @param x      X axis coordinate.
   * @param y      Y axis coordinate.
   * @param width  Width of rectangle.
   * @param height Height of rectangle.
   */
  public void drawRectangle(int x, int y, int width, int height) {

    if (fastBitmap.isRGB()) {

      for (int j = y; j < y + width; j++) {
        fastBitmap.setRGB(x, j, color);
      }

      for (int j = y; j < y + width; j++) {
        fastBitmap.setRGB(x + height, j, color);
      }

      for (int i = x; i < x + height; i++) {
        fastBitmap.setRGB(i, y, color);
        fastBitmap.setRGB(i, y + width, color);
      }

    } else {

      for (int j = y; j < y + width; j++) {
        fastBitmap.setGray(x, j, gray);
      }

      for (int j = y; j < y + width; j++) {
        fastBitmap.setGray(x + height, j, gray);
      }

      for (int i = x; i < x + height; i++) {
        fastBitmap.setGray(i, y, gray);
        fastBitmap.setGray(i, y + width, gray);
      }
    }
  }

  public void setColor(KfColor color) {
    this.color = color;
  }
  public void setColor(int red, int green, int blue) {
    color = new KfColor(red, green, blue);
  }
  public void setColor(int gray) {
    this.gray = gray;
  }

  public void setImage(KfBitmap fastBitmap) {
    this.fastBitmap = fastBitmap;
  }

}