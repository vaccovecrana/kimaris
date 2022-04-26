package io.vacco.kimaris.impl;

public class KmIntImage {

  public static void apply(double[][] in, double[][] out) {
    double v;
    for (int i = 0; i < in[0].length; i++) { // columns
      for (int j = 0; j < in.length; j++) { // rows
        v = in[i][j];
        if (i > 0) { v = v + out[i - 1][j]; }
        if (j > 0) { v = v + out[i][j - 1]; }
        if (i > 0 && j > 0) {
          v = v - out[i - 1][j -1];
        }
        out[i][j] = v;
      }
    }
  }

  /**
   * Retrieves the area under a rectangular within points
   * <code>a, b, c, d</code> as <code>X,Y</code> coordinates
   * in clock-wise order.
   *
   * <code>
   *    aX,aY         bX,bY
   *        *---------*
   *        |         |
   *        |         |
   *        *---------*
   *    cX,cY         dX,dY
   *</code>
   *
   * @param in an integral image rectangle
   * @return integral image area
   */
  public static double areaOf(double[][] in,
                              int aX, int aY, int bX, int bY,
                              int dX, int dY, int cX, int cY) {
    double a = (aY == -1 || aX == -1) ? 0 : in[aY][aX];
    double b = bY == -1 ? 0 : in[bY][bX];
    double c = cX == -1 ? 0 : in[cY][cX];
    double d = in[dY][dX];
    return a + d - b - c;
  }

  public static double areaOf(double[][] in, int aX, int aY, int rows, int cols) {
    aX = aX - 1;
    aY = aY - 1;
    return areaOf(in, aX, aY, aX + cols, aY, aX + cols, aY + rows, aX, aY + rows);
  }

}
