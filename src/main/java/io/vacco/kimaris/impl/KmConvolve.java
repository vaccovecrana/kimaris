package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.KmCoord;
import java.util.function.BiConsumer;

public class KmConvolve {

  public static void apply(int rows, int cols, int rowStride, int colStride, short[][] in, BiConsumer<KmCoord, short[][]> onRegion) {
    int r0 = 0, rN = r0 + rows;
    short[][] reg = new short[rows][cols];
    var crd = new KmCoord();
    do {
      int c0 = 0, cN = cols;
      do {
        int ri = 0;
        for (int i = r0; i < rN; i++) {
          int rj = 0;
          for (int j = c0; j < cN; j++) {
            reg[ri][rj] = in[i][j];
            rj++;
          }
          ri++;
        }
        onRegion.accept(crd.with(r0, c0), reg);
        c0 = c0 + colStride;
        cN = c0 + cols;
      } while(cN <= in[0].length);
      r0 = r0 + rowStride;
      rN = r0 + rows;
    } while(rN <= in.length);
  }

}
