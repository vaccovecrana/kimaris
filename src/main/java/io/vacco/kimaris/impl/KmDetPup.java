package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.Arrays;

public class KmDetPup {

  public static void classifyRegion(double r, double c, double s,
                                    int treeDepth, int nrows, int ncols,
                                    byte[] pixels, int dim, boolean flipV,
                                    double[] out, KmCascade plc) {
    int c1, c2, root = 0, px1, px2;

    for (long i = 0; i < plc.stages; i++) {
      double dr = 0, dc = 0;
      for (long j = 0; j < plc.trees; j++) {
        int idx = 0;
        for (long k = 0; k < plc.treeDepth; k++) {
          int r1 = Math.min(nrows - 1, Math.max(0, (int) (256 * r + plc.treeCodes[root + 4 * idx + 0] * Math.round(s)) >> 8));
          int r2 = Math.min(nrows - 1, Math.max(0, (int) (256 * r + plc.treeCodes[root + 4 * idx + 2] * Math.round(s)) >> 8));
          if (flipV) {
            c1 = Math.min(ncols - 1, Math.max(0, (int) (256 * c + (-plc.treeCodes[root + 4 * idx + 1]) * Math.round(s)) >> 8));
            c2 = Math.min(ncols - 1, Math.max(0, (int) (256 * c + (-plc.treeCodes[root + 4 * idx + 3]) * Math.round(s)) >> 8));
          } else {
            c1 = Math.min(ncols - 1, Math.max(0, (int) (256 * c + (plc.treeCodes[root + 4 * idx + 1]) * Math.round(s)) >> 8));
            c2 = Math.min(ncols - 1, Math.max(0, (int) (256 * c + (plc.treeCodes[root + 4 * idx + 3]) * Math.round(s)) >> 8));
          }
          px1 = pixels[r1 * dim + c1] & 0xff;
          px2 = pixels[r2 * dim + c2] & 0xff;
          idx = 2 * idx + 1 + (px1 > px2 ? 1 : 0);
        }
        int lutIdx = 2 * (int) (plc.trees * treeDepth * i + treeDepth * j + idx - (treeDepth - 1));
        dr += plc.treePreds[lutIdx + 0];
        if (flipV) {
          dc += -plc.treePreds[lutIdx + 1];
        } else {
          dc += plc.treePreds[lutIdx + 1];
        }
        root += 4 * treeDepth - 4;
      }
      r += dr * s;
      c += dc * s;
      s *= plc.scales;
    }
    out[0] = r;
    out[1] = c;
    out[2] = s;
  }

  public static KmCoord runCascade(int perturbs, KmCoord pl, KmImageParams img, boolean flipV, KmCascade plc) {
    var res = new double[3];
    var treeDepth = (int) Math.pow(2, plc.treeDepth);
    var detRows = new double[perturbs]; // TODO this needs optimization.
    var detCols = new double[perturbs];
    var detScale = new double[perturbs];

    double r0, r1, r2;

    for (int i = 0; i < perturbs; i++) {
      r0 = Math.random();
      r1 = Math.random();
      r2 = Math.random();

      double row = pl.row + pl.scale * 0.15 * (0.5 - r0);
      double col = pl.col + pl.scale * 0.15 * (0.5 - r1);
      double sc = pl.scale * (0.925 + 0.15 * r2);

      classifyRegion(row, col, sc, treeDepth, img.rows, img.cols, img.pixels, img.dim, flipV, res, plc);

      detRows[i] = res[0];
      detCols[i] = res[1];
      detScale[i] = res[2];
    }

    Arrays.sort(detRows);
    Arrays.sort(detCols);
    Arrays.sort(detScale);

    return KmCoord.from(
        (int) detRows[(int) Math.round(perturbs / 2d)],
        (int) detCols[(int) Math.round(perturbs / 2d)],
        detScale[(int) Math.round(perturbs / 2d)]
    ).withLabel(String.format("%s%s", plc.id, flipV ? "-flp" : ""));
  }

}
