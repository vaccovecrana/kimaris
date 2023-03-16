package impl;

import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import javax.imageio.ImageIO;
import java.util.*;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmDetTest {

  public static void testFrame(KmDet det, KmRegion fr, KmRegion pr, KmImage ki) {
    System.out.println("================");
    det.processImage(ki, null);
    assertEquals(1, fr.detectCount);
    System.out.println("  face: " + Arrays.toString(fr.detections));
    System.out.println("  eyes: " + Arrays.toString(pr.detections));
  }

  static {
    it("Detects face objects", () -> {
      var ki0 = KmImages.setMeta(new KmImage(),
        ImageIO.read(Objects.requireNonNull(KmDetTest.class.getResource("/frame-00.jpg"))),
        true
      );
      var ki1 = KmImages.setMeta(new KmImage(),
        ImageIO.read(Objects.requireNonNull(KmDetTest.class.getResource("/frame-01.jpg"))),
        true
      );
      var ki2 = KmImages.setMeta(new KmImage(),
        ImageIO.read(Objects.requireNonNull(KmDetTest.class.getResource("/frame-02.jpg"))),
        true
      );

      var fr = KmRegion.detectDefault().withDetectMax(8);
      var pr = KmRegion.detectDefault()
        .withDetectMax(32)
        .withSizeMin(8)
        .withSizeMax(64)
        .withDetectThreshold(5);

      var fd = new KmDet(KmCascades.loadPico(KmDetTest.class.getResource("/facefinder-pico")), fr);
      var pd = new KmDet(KmCascades.loadPico(KmDetTest.class.getResource("/puploc-java")), pr);
      var det = fd.then(pd);

      testFrame(det, fr, pr, ki0);
      testFrame(det, fr, pr, ki1);
      testFrame(det, fr, pr, ki2);

      System.out.println();
    });
  }
}
