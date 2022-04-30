package io.vacco.kimaris;

import io.vacco.kimaris.impl.KmMbLbp;
import io.vacco.kimaris.io.KmImage;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmMbLbpTest {
  static {
    it("Extracts MBLBP histograms from an image", () -> {
      var img = KmMbLbpTest.class.getResource("/reference-00.png");
      var ip = KmImage.grayPixelsOf(img, null);
      KmMbLbp.scan(ip, 2, 4, 2, 4, hst -> {
        System.out.println("lol");
      });
    });
  }
}
