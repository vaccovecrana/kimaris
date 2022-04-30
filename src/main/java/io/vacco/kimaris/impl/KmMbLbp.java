package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.KmImageParams;
import java.util.function.*;
import static io.vacco.kimaris.impl.KmIntImage.*;

public class KmMbLbp {

  public static short unsignedFrom(boolean[] in) {
    byte b = (byte) (
        (in[0]?1<<7:0) + (in[1]?1<<6:0) +
        (in[2]?1<<5:0) + (in[3]?1<<4:0) +
        (in[4]?1<<3:0) + (in[5]?1<<2:0) +
        (in[6]?1<<1:0) + (in[7]?1:0));
    return (short) (b & 0xff);
  }

  public static void apply(double nw, double n, double ne,
                           double cw, double c, double ce,
                           double sw, double s, double se, boolean[] out) {
    out[0] = nw > c; out[1] = n > c; out[2] = ne > c;
    out[7] = cw > c;                 out[3] = ce > c;
    out[6] = sw > c; out[5] = s > c; out[4] = se > c;
  }

  public static void apply(short[][] in, boolean[] out,
                           int row, int col, int rS, int cS,
                           Function<Double, Double> valFn) {
    double nw, n, ne,
           cw, c, ce,
           sw, s, se;
    if (cS == 1 && rS == 1) { // base lbp case
      nw = in[row-1][col-1]; n = in[row-1][col]; ne = in[row-1][col+1];
      cw = in[row  ][col-1]; c = in[row  ][col]; ce = in[row  ][col+1];
      sw = in[row+1][col-1]; s = in[row+1][col]; se = in[row+1][col+1];
    } else {
      int rmh = row - rS, rph = row + rS;
      int cmw = col - cS, cpw = col + cS;
      nw = areaOf(in, rmh, cmw, rS, cS); n = areaOf(in, rmh, col, rS, cS); ne = areaOf(in, rmh, cpw, rS, cS);
      cw = areaOf(in, row, cmw, rS, cS); c = areaOf(in, row, col, rS, cS); ce = areaOf(in, row, cpw, rS, cS);
      sw = areaOf(in, rph, cmw, rS, cS); s = areaOf(in, rph, col, rS, cS); se = areaOf(in, rph, cpw, rS, cS);
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

  public static short[] scan(KmImageParams ip,
                             int blkRows, int blkCols,
                             int blkRowStride, int blkColStride) {
    var intImgBuf = ip.blankBuf();
    var lbpBuf = new boolean[8];
    var lbpRows = blkRows * 3;
    var lbpCols = blkCols * 3;
    var lbpHist = new short[256];
    KmIntImage.apply(ip.grayMat, intImgBuf);
    KmConvolve.apply(lbpRows, lbpCols, blkRowStride, blkColStride, intImgBuf, reg -> {
      apply(reg, lbpBuf, blkRows, blkCols, blkRows, blkCols, val -> val / (blkRows * blkCols));
      var lbp = KmMbLbp.unsignedFrom(lbpBuf);
      lbpHist[lbp] = (short) (lbpHist[lbp] + 1);
    });
    return lbpHist;
  }

}
