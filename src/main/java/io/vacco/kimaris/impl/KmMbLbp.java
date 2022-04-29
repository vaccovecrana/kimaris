package io.vacco.kimaris.impl;

import static io.vacco.kimaris.impl.KmIntImage.*;

public class KmMbLbp {

  public static void apply(double nw, double n, double ne,
                           double cw, double c, double ce,
                           double sw, double s, double se, boolean[] out) {
    out[0] = nw > c; out[1] = n > c; out[2] = ne > c;
    out[7] = cw > c;                 out[3] = ce > c;
    out[6] = sw > c; out[5] = s > c; out[4] = se > c;
  }

  public static void apply(double[][] in, boolean[] out, int row, int col, int w, int h) {
    if (w == 1 && h == 1) { // base lbp case
      apply(
          in[row-1][col-1], in[row-1][col], in[row-1][col+1],
          in[row  ][col-1], in[row  ][col], in[row  ][col+1],
          in[row+1][col-1], in[row+1][col], in[row+1][col+1],
          out
      );
    } else {
      int rmh = row - h, rph = row + h;
      int cmw = col - w, cpw = col + w;
      double
          nw = areaOf(in, rmh, cmw, w, h), n = areaOf(in, rmh, col, w, h), ne = areaOf(in, rmh, cpw, w, h),
          cw = areaOf(in, row, cmw, w, h), c = areaOf(in, row, col, w, h), ce = areaOf(in, row, cpw, w, h),
          sw = areaOf(in, rph, cmw, w, h), s = areaOf(in, rph, col, w, h), se = areaOf(in, rph, cpw, w, h);
      apply(
          nw, n, ne,
          cw, c, ce,
          sw, s, se, out
      );
    }
  }

}
