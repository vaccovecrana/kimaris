package impl;

import io.vacco.kimaris.impl.KmMath;
import io.vacco.kimaris.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmMathTest {

  public static final long RSeed = 572148941;

  static {
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
