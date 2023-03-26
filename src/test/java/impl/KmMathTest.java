package impl;

import io.vacco.kimaris.impl.KmMath;
import io.vacco.kimaris.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static io.vacco.kimaris.schema.KmBounds.bounds;
import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmMathTest {

  public static final long RSeed = 572148941;

  static {
    it("Shifts bound locations", () ->{
      var b = bounds(256, 256, 128);

      var s0 = bounds().copyFrom(b).shift(0, 0).resize(.5f);
      assertEquals(192, s0.r);
      assertEquals(192, s0.c);
      assertEquals(64, s0.s);

      var s1 = bounds().copyFrom(b).shift(1, 1).resize(.5f);
      assertEquals(320, s1.r);
      assertEquals(320, s1.c);
      assertEquals(64, s1.s);

      var s2 = bounds().copyFrom(b).shift(0, 1).resize(.5f);
      assertEquals(192, s2.r);
      assertEquals(320, s2.c);
      assertEquals(64, s2.s);

      var s3 = bounds().copyFrom(b).shift(0, 2).resize(.5f);
      assertEquals(192, s3.r);
      assertEquals(448, s3.c);
      assertEquals(64, s3.s);

      var s4 = bounds().copyFrom(b).shift(0.5f, 0.5f).resize(.5f);
      assertEquals(256, s4.r);
      assertEquals(256, s4.c);
      assertEquals(64,  s4.s);

      var s5 = bounds().copyFrom(b).shift(-1, -1).resize(.5f);
      assertEquals(64, s5.r);
      assertEquals(64, s5.c);
      assertEquals(64,  s5.s);
    });
    it("Generates a seed-based random number", () -> {
      var r = new KmRand().smwcRand(RSeed);
      var rand = r.mwcrand();
      var str = String.format("%X", rand);
      assertEquals("20DE2886", str);
    });
    it("Generates random tree codes", () -> {
      var r = new KmRand().smwcRand(RSeed);
      var t = KmMath.getRandomTCode(KmBoundBox.getDefault(), r);
      assertEquals(2098341422, t);
    });
  }
}
