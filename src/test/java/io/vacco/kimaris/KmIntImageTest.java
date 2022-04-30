package io.vacco.kimaris;

import io.vacco.kimaris.impl.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmIntImageTest {

  static {
    it("Calculates integral image values", () -> {
      short[][] in0 = new short[][] {
          {5, 2, 5, 2},
          {3, 6, 3, 6},
          {5, 2, 5, 2}
      };
      short[][] out0 = new short[3][4];
      KmIntImage.apply(in0, out0);
    });

    it("Calculates an arbitrary region at four points", () -> {
      short[][] in = new short[][] {
          {5, 2, 5, 2},
          {3, 6, 3, 6},
          {5, 2, 5, 2},
          {3, 6, 3, 6}
      };
      short[][] out = new short[4][4];

      KmIntImage.apply(in, out);
      assertEquals(64.0, out[3][3], 0.01);

      double sum = KmIntImage.areaOf(out, 1, 1, 1, 3, 3, 3, 3, 1);
      assertEquals(16.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 2, 0, 2, 3);
      assertEquals(24.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 3, 3);
      assertEquals(39.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 0, 0, 3, 3);
      assertEquals(36.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 2, 3);
      assertEquals(24.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 1, 1);
      assertEquals(6.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 1, 2);
      assertEquals(9.0, sum, 0.01);
    });

    it("Calculates MLBP features on an input region", () -> {
      assertEquals(0, KmMbLbp.unsignedFrom(new boolean[] {
          false, false, false, false, false, false, false, false
      }));
      assertEquals(1, KmMbLbp.unsignedFrom(new boolean[] {
          false, false, false, false, false, false, false, true
      }));
      assertEquals(128, KmMbLbp.unsignedFrom(new boolean[] {
          true, false, false, false, false, false, false, false
      }));
      assertEquals(255, KmMbLbp.unsignedFrom(new boolean[] {
          true, true, true, true, true, true, true, true
      }));

      short[][] imgIn = new short[][] {
          { 0,  1,  3,  5,  2,  7, 10,  7, 10,  9},
          { 3,  7,  6,  9,  3,  8,  1,  8,  5,  8},
          { 1, 11,  0,  7, 13,  7,  8, 12, 13,  1},
          {14,  3,  2,  7,  1,  8,  9, 11,  2, 12},
          { 1,  5, 15,  3,  6,  6, 20, 19, 10,  6},
          { 8,  1,  2,  6,  7,  3,  2, 11,  0, 15},
          { 7,  7,  6,  0,  9,  5, 10,  3,  8,  1},
          {12,  5,  6, 10, 11,  3,  6,  7,  9,  1}
      };
      short[][] imgOut = new short[8][10];
      boolean[] out = new boolean[8];

      KmMbLbp.apply(imgIn, out, 3, 6, 1, 1, null);
      assertFalse(out[0]);
      assertFalse(out[1]);
      assertTrue(out[2]);
      assertTrue(out[3]);
      assertTrue(out[4]);
      assertTrue(out[5]);
      assertFalse(out[6]);
      assertFalse(out[7]);
      System.out.println(KmMbLbp.unsignedFrom(out));

      boolean[] out0 = new boolean[8];
      KmIntImage.apply(imgIn, imgOut);
      KmMbLbp.apply(imgOut, out0, 3, 4, 2, 1, null);
      KmMbLbp.apply(imgOut, out0, 3, 4, 2, 2, null);
      KmMbLbp.apply(imgOut, out0, 3, 4, 2, 3, null);

      // MLBP - average pixel intensity
      KmMbLbp.apply(imgOut, out0, 3, 4, 2, 2, v -> v / (2 * 2));
      System.out.println(KmMbLbp.unsignedFrom(out0));
    });

  }
}
