package io.vacco.kimaris.impl;

import java.util.function.Function;
import static io.vacco.kimaris.impl.KmIntImage.*;

public class KmMbLbp {

  public static void apply(double nw, double n, double ne,
                           double cw, double c, double ce,
                           double sw, double s, double se, boolean[] out) {
    out[0] = nw > c; out[1] = n > c; out[2] = ne > c;
    out[7] = cw > c;                 out[3] = ce > c;
    out[6] = sw > c; out[5] = s > c; out[4] = se > c;
  }

  public static void apply(double[][] in, boolean[] out, int row, int col, int w, int h, Function<Double, Double> valFn) {
    double nw, n, ne,
           cw, c, ce,
           sw, s, se;
    if (w == 1 && h == 1) { // base lbp case
      nw = in[row-1][col-1]; n = in[row-1][col]; ne = in[row-1][col+1];
      cw = in[row  ][col-1]; c = in[row  ][col]; ce = in[row  ][col+1];
      sw = in[row+1][col-1]; s = in[row+1][col]; se = in[row+1][col+1];
    } else {
      int rmh = row - h, rph = row + h;
      int cmw = col - w, cpw = col + w;
      nw = areaOf(in, rmh, cmw, w, h); n = areaOf(in, rmh, col, w, h); ne = areaOf(in, rmh, cpw, w, h);
      cw = areaOf(in, row, cmw, w, h); c = areaOf(in, row, col, w, h); ce = areaOf(in, row, cpw, w, h);
      sw = areaOf(in, rph, cmw, w, h); s = areaOf(in, rph, col, w, h); se = areaOf(in, rph, cpw, w, h);
    }
    if (valFn != null) {
      nw = valFn.apply(nw); n = valFn.apply(n); ne = valFn.apply(ne);
      cw = valFn.apply(cw); c = valFn.apply(c); ce = valFn.apply(ce);
      sw = valFn.apply(sw); s = valFn.apply(s); se = valFn.apply(se);
    }
    apply(
        nw, n, ne,
        cw, c, ce,
        sw, s, se, out
    );
  }

}
