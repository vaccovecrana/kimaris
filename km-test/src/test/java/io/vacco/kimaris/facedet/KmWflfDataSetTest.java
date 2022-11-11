package io.vacco.kimaris.facedet;

import io.vacco.oruzka.io.OzIo;
import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import java.io.File;

import static j8spec.J8Spec.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmWflfDataSetTest {
  static {
    it("Loads the WFLW facial landmark dataset", () -> {
      var trainFile = new File("./WFLW_annotations/list_98pt_rect_attr_train_test/list_98pt_rect_attr_train.txt");
      var lines = OzIo.loadLinesFrom(trainFile.toURI().toURL());
      for (String l : lines) {
        var attrs = l.split(" ");
        System.out.println(l);
      }
    });
  }
}
