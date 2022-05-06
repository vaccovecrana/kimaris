package io.vacco.kimaris;

import com.google.gson.*;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.impl.KmImage;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.io.File;
import java.util.stream.IntStream;

import static j8spec.J8Spec.*;
import static io.vacco.sabnock.SkJson.*;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmMbLbpTest {

  private static final Gson g = new GsonBuilder().setPrettyPrinting().create();

  static {
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

      KmIntImage.apply(imgIn, imgOut);
      KmMbLbp.applyToRegion(imgOut, out, 3, 6, 1, 1, null);
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
      KmMbLbp.applyToRegion(imgOut, out0, 3, 4, 2, 1, null);
      KmMbLbp.applyToRegion(imgOut, out0, 3, 4, 2, 2, null);
      KmMbLbp.applyToRegion(imgOut, out0, 3, 4, 2, 3, null);

      // MLBP - average pixel intensity
      KmMbLbp.applyToRegion(imgOut, out0, 3, 4, 2, 2, v -> (short) (v / (2 * 2)));
      System.out.println(KmMbLbp.unsignedFrom(out0));
    });

    it("Extracts MBLBP values from an image", () -> {
      // var img = KmMbLbpTest.class.getResource("/reference-01.png");
      var img = KmMbLbpTest.class.getResource("/sample.jpg");
      var ip = KmImage.grayPixelsOf(img, null);
      KmMbLbp.mbLbpImageOf(
          ip, 1, 1,
          (dim, data) -> KmImage.writePng(dim.col, dim.row, data, new File("./build/mblbp-out.png"))
      );
    });
    it("Extracts MBLBP histograms from an image", () -> {
      var img = KmMbLbpTest.class.getResource("/sample.jpg");
      var ip = KmImage.grayPixelsOf(img, null);
      var lbpH = KmMbLbp.mbLbpHistogramOf(ip, 2, 2);
      var chart = obj(
          kv("tooltip", obj(kv("show", true))),
          kv("xAxis", obj(
              kv("type", "category"),
              kv("data", IntStream.range(0, 256).toArray())
          )),
          kv("yAxis", obj(kv("type", "value"))),
          kv("series", new Object[] {
              obj(
                  kv("data", lbpH),
                  kv("type", "bar")
              )
          })
      );
      System.out.println(g.toJson(chart));
    });
  }
}
