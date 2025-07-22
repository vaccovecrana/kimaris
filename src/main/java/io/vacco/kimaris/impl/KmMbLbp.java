package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.ArrayList;
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

  public static void apply(short nw, short n, short ne,
                           short cw, short c, short ce,
                           short sw, short s, short se, boolean[] out) {
    if (out != null) {
      out[0] = nw > c; out[1] = n > c; out[2] = ne > c;
      out[7] = cw > c;                 out[3] = ce > c;
      out[6] = sw > c; out[5] = s > c; out[4] = se > c;
    }
  }

  public static void applyToRegion(short[][] in, boolean[] out,
                                   int row, int col, int rS, int cS,
                                   Function<Short, Short> valFn) {
    short
        nw, n, ne,
        cw, c, ce,
        sw, s, se;
    int rmh = row - rS, rph = row + rS;
    int cmw = col - cS, cpw = col + cS;

    nw = areaOf(in, rmh, cmw, rS, cS); n = areaOf(in, rmh, col, rS, cS); ne = areaOf(in, rmh, cpw, rS, cS);
    cw = areaOf(in, row, cmw, rS, cS); c = areaOf(in, row, col, rS, cS); ce = areaOf(in, row, cpw, rS, cS);
    sw = areaOf(in, rph, cmw, rS, cS); s = areaOf(in, rph, col, rS, cS); se = areaOf(in, rph, cpw, rS, cS);

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

  public static void mbLbpScan(KmImageParams ip, int rows, int cols, Consumer<KmMbLbpBlock> onBlock) {
    var intImgBuf = ip.blankBuf();
    var blk = new KmMbLbpBlock();
    int rt3 = rows * 3, ct3 = cols * 3;

    KmIntImage.apply(ip.grayMat, intImgBuf);
    KmConvolve.apply(rt3, ct3, rt3, ct3, intImgBuf, (crd, reg) -> {
      blk.region = reg;
      blk.origin = crd;
      applyToRegion(
          intImgBuf, blk.lbpBuf,
          crd.row + rows, crd.col + cols, rows, cols,
          val -> (short) (val / (rows * cols))
      );
      blk.lbp = KmMbLbp.unsignedFrom(blk.lbpBuf); // THIS is the MB-LBP "pixel" value.
      onBlock.accept(blk);
    });
  }

  public static short[] mbLbpHistogramOf(KmImageParams ip, int blkRows, int blkCols) {
    var lbpHist = new short[256];
    mbLbpScan(ip, blkRows, blkCols, (blk) -> lbpHist[blk.lbp] = (short) (lbpHist[blk.lbp] + 1));
    return lbpHist;
  }

  public static void mbLbpImageOf(KmImageParams ip, int blkRows, int blkCols, BiConsumer<KmCoord, short[]> onData) {
    int[] rc = new int[] {-1, -1};
    int[] lrc = new int[] {0, 0};
    var lbpL = new ArrayList<Short>();
    var dim = new KmCoord();
    mbLbpScan(ip, blkRows, blkCols, (blk) -> {
      lbpL.add(blk.lbp);
      if (blk.origin.row > rc[0]) {
        rc[0] = blk.origin.row;
        lrc[0] = lrc[0] + 1;
      }
      if (blk.origin.col > rc[1]) {
        rc[1] = blk.origin.col;
        lrc[1] = lrc[1] + 1;
      }
    });
    short[] lbpA = new short[lbpL.size()];
    for (int i = 0; i < lbpA.length; i++) {
      lbpA[i] = lbpL.get(i);
    }
    onData.accept(dim.with(lrc[0], lrc[1]), lbpA);
  }

}
