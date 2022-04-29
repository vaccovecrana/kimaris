package io.vacco.kimaris.impl;

public class KmIntImage {

  public static void apply(double[][] in, double[][] out) {
    double v;
    for (int i = 0; i < in[0].length; i++) { // columns
      for (int j = 0; j < in.length; j++) { // rows
        v = in[j][i];
        if (i > 0) { v = v + out[j][i - 1]; }
        if (j > 0) { v = v + out[j - 1][i]; }
        if (i > 0 && j > 0) {
          v = v - out[j - 1][i -1];
        }
        out[j][i] = v;
      }
    }
  }

  /**
   * Retrieves the area under a rectangular within points
   * <code>a, b, c, d</code> as <code>Row,Col</code> coordinates
   * in clock-wise order.
   *
   * <code>
   *    aR,aC         bR,bC
   *        *---------*
   *        |         |
   *        |         |
   *        *---------*
   *    cR,cC         dR,dC
   *</code>
   *
   * @param in an integral image rectangle
   * @return integral image area
   */
  public static double areaOf(double[][] in,
                              int aR, int aC, int bR, int bC,
                              int dR, int dC, int cR, int cC) {
    double a = (aR == -1 || aC == -1) ? 0 : in[aR][aC];
    double b = bR == -1 ? 0 : in[bR][bC];
    double c = cC == -1 ? 0 : in[cR][cC];
    double d = in[dR][dC];
    return a + d - b - c;
  }

  public static double areaOf(double[][] in, int r, int c, int rows, int cols) {
    c = c - 1;
    r = r - 1;
    return areaOf(in, r, c, r, c + cols, r + rows, c + cols, r + rows, c);
  }

}
