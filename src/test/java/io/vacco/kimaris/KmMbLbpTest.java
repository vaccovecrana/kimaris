package io.vacco.kimaris;

import com.google.gson.GsonBuilder;
import io.vacco.kimaris.impl.KmMbLbp;
import io.vacco.kimaris.io.KmImage;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.util.stream.IntStream;

import static j8spec.J8Spec.*;
import static io.vacco.sabnock.SkJson.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmMbLbpTest {
  static {
    it("Extracts MBLBP histograms from an image", () -> {
      var g = new GsonBuilder().setPrettyPrinting().create();
      var img = KmMbLbpTest.class.getResource("/sample.jpg");
      var ip = KmImage.grayPixelsOf(img, null);

      var lbpH = KmMbLbp.scan(ip, 16, 16, 16, 16);
      // var lbpH = KmMbLbp.scan(ip, 1, 1, 1, 1);

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
