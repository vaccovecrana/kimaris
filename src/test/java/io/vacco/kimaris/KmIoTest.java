package io.vacco.kimaris;

import com.google.gson.GsonBuilder;
import io.vacco.kimaris.impl.KmPico;
import io.vacco.kimaris.io.KmIo;
import io.vacco.kimaris.schema.KmScanParams;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmIoTest {
  static {
    it("Detects faces in an image", () -> {
      var g = new GsonBuilder().setPrettyPrinting().create();
      var fa = KmPico.detectFaces(KmIo.loadCascades(), KmScanParams.defaultParams(), KmIoTest.class.getResource("/sample.jpg"));
      System.out.println(g.toJson(fa));
    });
  }
}
