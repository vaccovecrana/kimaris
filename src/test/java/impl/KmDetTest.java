package impl;

import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import javax.imageio.ImageIO;
import java.util.*;

import static io.vacco.kimaris.schema.KmEns.ens;
import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmDetTest {

  public static void testFrame(KmEns ens, KmImage ki, Map<String, KmBounds> dets) {
    System.out.println("================");
    ens.run(ki, dets);
    assertEquals(3, dets.size());
    System.out.println(dets);
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

      var dets = new TreeMap<String, KmBounds>();
      var ens = ens(fd).withId("face")
        .then(ens(pd, kb -> kb.shift(.5f, .3f).resize(0.5f)).withId("left-eye"))
        .then(ens(pd, kb -> kb.shift(.5f, .7f).resize(0.5f)).withId("right-eye"));

      testFrame(ens, ki0, dets);
      testFrame(ens, ki1, dets);
      testFrame(ens, ki2, dets);

      System.out.println();
    });
  }
}
