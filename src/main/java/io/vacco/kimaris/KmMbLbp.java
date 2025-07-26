package io.vacco.kimaris;

import java.util.ArrayList;
import java.util.function.*;
import static io.vacco.kimaris.KmArea.*;

public class KmMbLbp {

  private static final short[] uniformLut = new short[] {
    0, 1, 2, 3, 4, 58, 5, 6, 7, 58, 58, 58, 8, 58, 9, 10,
    11, 58, 58, 58, 58, 58, 58, 58, 12, 58, 58, 58, 13, 58, 14, 15,
    16, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    17, 58, 58, 58, 58, 58, 58, 58, 18, 58, 58, 58, 19, 58, 20, 21,
    22, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    23, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    24, 58, 58, 58, 58, 58, 58, 58, 25, 58, 58, 58, 26, 58, 27, 28,
    29, 30, 58, 31, 58, 58, 58, 32, 58, 58, 58, 58, 58, 58, 58, 33,
    58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 34,
    58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58,
    58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 35,
    36, 37, 58, 38, 58, 58, 58, 39, 58, 58, 58, 58, 58, 58, 58, 40,
    58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 58, 41,
    42, 43, 58, 44, 58, 58, 58, 45, 58, 58, 58, 58, 58, 58, 58, 46,
    47, 48, 58, 49, 58, 58, 58, 50, 51, 52, 58, 53, 54, 55, 56, 57
  };

  public static short unsignedFrom(boolean[] in) {
    byte b = (byte) (
        (in[0]?1<<7:0) + (in[1]?1<<6:0) +
        (in[2]?1<<5:0) + (in[3]?1<<4:0) +
        (in[4]?1<<3:0) + (in[5]?1<<2:0) +
        (in[6]?1<<1:0) + (in[7]?1:0)
    );
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

  public static void mbLbpScan(KmSchema.KmImageParams ip, int rows, int cols, boolean useUniform, Consumer<KmSchema.KmMbLbpBlock> onBlock) {
    var intImgBuf = ip.blankBuf();
    var blk = new KmSchema.KmMbLbpBlock();
    int rt3 = rows * 3, ct3 = cols * 3;

    KmArea.areaSum(ip.grayMat, intImgBuf);
    KmArea.convolve(rt3, ct3, rt3, ct3, intImgBuf, (crd, reg) -> {
      blk.region = reg;
      blk.origin = crd;
      applyToRegion(
        intImgBuf, blk.lbpBuf,
        crd.row + rows, crd.col + cols, rows, cols,
        val -> (short) (val / (rows * cols))
      );
      blk.lbp = KmMbLbp.unsignedFrom(blk.lbpBuf); // THIS is the MB-LBP "pixel" value.
      if (useUniform) {
        blk.lbp = uniformLut[blk.lbp];
      }
      onBlock.accept(blk);
    });
  }

  public static short[] mbLbpHistogramOf(KmSchema.KmImageParams ip, int blkRows, int blkCols, boolean useUniform) {
    var lbpHist = new short[useUniform ? 59 : 256];
    mbLbpScan(ip, blkRows, blkCols, useUniform, (blk) -> lbpHist[blk.lbp] = (short) (lbpHist[blk.lbp] + 1));
    return lbpHist;
  }

  public static void mbLbpImageOf(KmSchema.KmImageParams ip, int blkRows, int blkCols, boolean useUniform, BiConsumer<KmSchema.KmCoord, short[]> onData) {
    var rc = new int[] {-1, -1};
    var lrc = new int[] {0, 0};
    var lbpL = new ArrayList<Short>();
    var dim = new KmSchema.KmCoord();
    mbLbpScan(ip, blkRows, blkCols, useUniform, (blk) -> {
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
    var lbpA = new short[lbpL.size()];
    for (int i = 0; i < lbpA.length; i++) {
      lbpA[i] = lbpL.get(i);
    }
    onData.accept(dim.at(lrc[0], lrc[1]), lbpA);
  }

}
