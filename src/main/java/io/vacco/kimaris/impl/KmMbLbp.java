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
    }
    // double c = areaOf(in, )
  }

}
