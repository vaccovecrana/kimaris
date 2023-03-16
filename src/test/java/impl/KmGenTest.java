package impl;

import com.google.gson.*;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.OFnBlock;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.io.*;
import java.nio.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static io.vacco.kimaris.schema.KmBounds.bounds;
import static org.junit.Assert.*;
import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmGenTest {

  private static final Gson g = new GsonBuilder().setPrettyPrinting().create();
  private static final KmImageList wfTiny = new KmImageList();

  static {
    var imk = new AtomicInteger();
    var url = KmGenTest.class.getResource("/trdata-128");
    OFnBlock.tryRun(() -> {
      try (var is = Objects.requireNonNull(url).openStream()) {
        var bb = ByteBuffer.wrap(is.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
        while (bb.hasRemaining()) {
          var h = bb.getInt();
          var w = bb.getInt();
          var pixels = new byte[h * w];
          var pixelsF = new short[h * w];

          bb.get(pixels);
          for (int i = 0; i < pixels.length; i++) {
            pixelsF[i] = (short) Byte.toUnsignedInt(pixels[i]);
          }

          var img = new KmImage()
            .withImagePath(Integer.toString(imk.get()))
            .withSize(w, h)
            .withPixels(pixelsF);
          imk.set(imk.get() + 1);

          var boxes = bb.getInt();
          for (int i = 0; i < boxes; i++) {
            var r = bb.getInt();
            var c = bb.getInt();
            var s = bb.getInt();
            var obj = new KmObj()
              .withImage(img)
              .withBounds(bounds(r, c, s));
            img.add(obj);
          }
          wfTiny.add(img);
        }
      }
    });
    KmLogging.withLog(new KmTestLog());
  }

  static {
    it("Learns a cascade",  () -> {
      var kr = new KmRand().smwcRand(KmMathTest.RSeed);
      var kcOut = new File("./build/facefinder");
      var jsonOut = new File("./build/facefinder.json");

      var reg = KmRegion.trainDefault();
      var kc = KmGen.learnCascade(KmBoundBox.getDefault(), wfTiny, kr, reg);
      var kb = KmCascades.loadFrom(kc);
      var kc0 = KmCascades.loadFrom(kb);

      assertNotNull(kc0);
      assertNotNull(kc0.boundBox);
      assertTrue(kc0.treeDepth > 0);

      try (var fw = new FileWriter(jsonOut)) {
        g.toJson(kb, fw);
      }
      KmCascades.savePico(new FileOutputStream(kcOut), kc);
    });

    it("Calculates region overlaps", () -> {
      var o0 = KmMath.getOverlap(173, 156, 63, 55, 69, 58);
      var o1 = KmMath.getOverlap(121, 46, 85, 83, 108, 63);
      assertEquals(0, o0, 0.0);
      assertEquals(0.040141236, o1, 0.001);
    });
  }
}
