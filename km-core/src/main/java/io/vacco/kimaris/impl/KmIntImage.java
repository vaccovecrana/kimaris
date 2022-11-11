package io.vacco.kimaris.impl;

public class KmIntImage {

  public static void apply(short[][] in, short[][] out) {
    short v;
    for (int i = 0; i < in[0].length; i++) { // columns
      for (int j = 0; j < in.length; j++) { // rows
        v = in[j][i];
        if (i > 0) { v = (short) (v + out[j][i - 1]); }
        if (j > 0) { v = (short) (v + out[j - 1][i]); }
        if (i > 0 && j > 0) {
          v = (short) (v - out[j - 1][i -1]);
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
  public static short areaOf(short[][] in,
                             int aR, int aC, int bR, int bC,
                             int dR, int dC, int cR, int cC) {
    short a = (aR == -1 || aC == -1) ? 0 : in[aR][aC];
    short b = bR == -1 ? 0 : in[bR][bC];
    short c = cC == -1 ? 0 : in[cR][cC];
    short d = in[dR][dC];
    return (short) (a + d - b - c);
  }

  public static short areaOf(short[][] in, int r, int c, int rows, int cols) {
    c = c - 1;
    r = r - 1;
    return areaOf(in, r, c, r, c + cols, r + rows, c + cols, r + rows, c);
  }

}
