package io.vacco.kimaris;

import io.vacco.kimaris.impl.KmIntImage;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmIntImageTest {

  public static final double[][] in = new double[][] {
      { 5, 2, 5, 2 },
      { 3, 6, 3, 6 },
      { 5, 2, 5, 2 },
      { 3, 6, 3, 6 },
  };

  public static final double[][] out = new double[4][4];

  static {
    it("Calculates integral image values", () -> {
      KmIntImage.apply(in, out);
      assertEquals(64.0, out[3][3], 0.01);
    });
    it("Calculates an arbitrary region at four points", () -> {
      double sum = KmIntImage.areaOf(out, 1, 1, 3, 1, 3, 3, 1, 3);
      assertEquals(16.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 3, 3);
      assertEquals(39.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 2, 3);
      assertEquals(24.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 1, 1);
      assertEquals(6.0, sum, 0.01);

      sum = KmIntImage.areaOf(out, 1, 1, 1, 2);
      assertEquals(9.0, sum, 0.01);
    });
  }
}
