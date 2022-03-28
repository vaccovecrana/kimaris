package io.vacco.kimaris;

import com.google.gson.GsonBuilder;
import io.vacco.kimaris.impl.KmPico;
import io.vacco.kimaris.io.KmCascades;
import io.vacco.kimaris.io.KmImage;
import io.vacco.kimaris.schema.*;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;
import java.net.URL;
import java.util.Objects;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmIoTest {

  private static final URL imageRgbNoFaceUrl = Objects.requireNonNull(KmIoTest.class.getResource("/sample-no-face.png"));
  private static final URL imageRgbUrl = Objects.requireNonNull(KmIoTest.class.getResource("/sample.jpg"));
  private static final URL imageGraUrl = Objects.requireNonNull(KmIoTest.class.getResource("/sample-gray.png"));

  static {
    it("Detects faces/no-faces in images", () -> {
      var g = new GsonBuilder().setPrettyPrinting().create();
      var ipb = new KmImageParams();
      var images = new URL[] { imageRgbNoFaceUrl, imageRgbUrl, imageGraUrl };

      for (URL img : images) {
        ipb = KmImage.grayPixelsOf(img, ipb);
        var fa = KmPico.detectFaces(KmCascades.loadCascades(), KmScanParams.defaultParams(), ipb);
        System.out.println(g.toJson(fa));
      }
    });
  }
}
