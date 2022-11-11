package io.vacco.kimaris.obj;

import io.vacco.kimaris.facedet.Main;
import j8spec.junit.J8SpecRunner;
import javafx.application.Application;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;

@RunWith(J8SpecRunner.class)
public class MainTest {
  static {
    System.setProperty("prism.text", "t2k");
    System.setProperty("prism.lcdtext", "false");
    System.setProperty("prism.forceGPU", "true");

    it("LULZ", () -> {
      Application.launch(Main.class);
    });
  }
}
