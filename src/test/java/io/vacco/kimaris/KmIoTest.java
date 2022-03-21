package io.vacco.kimaris;

import io.vacco.kimaris.io.KmIo;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmIoTest {
  static {
    it("Reads the pupil cascade file", () -> {
      var u = KmIoTest.class.getResource("/io/vacco/kimaris/lp42");
      var lol = KmIo.unpackCascade(u);

      var fu = KmIoTest.class.getResource("/io/vacco/kimaris/facefinder");
      var fc = KmIo.unpackFaceCascade(fu);

      System.out.println("Done");
    });
  }
}
