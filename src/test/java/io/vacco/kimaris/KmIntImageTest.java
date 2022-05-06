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
    it("Calculates integral values", () -> {
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
  }
}
