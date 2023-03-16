package datasets.caltech_10k_web_faces;

import impl.KmMathTest;
import impl.KmTestLog;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.OFnSupplier;
import io.vacco.oruzka.io.OzIo;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

import static io.vacco.kimaris.impl.KmImages.setMeta;
import static io.vacco.kimaris.schema.KmBounds.bounds;
import static io.vacco.kimaris.schema.KmPoint.pt;
import static io.vacco.oruzka.core.OFnBlock.tryRun;
import static java.lang.Math.pow;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.*;

public class KmCtPuplocGen {

  public static KmImage from(File imgFile, List<String[]> rows) {
    if (!imgFile.exists()) {
      return null;
    }

    var ki = new KmImage().withImagePath(imgFile.getPath());
    tryRun(() -> setMeta(ki, ImageIO.read(imgFile), true));

    for (var row : rows) {
      float lex = Float.parseFloat(row[1]);
      float ley = Float.parseFloat(row[2]);
      float rex = Float.parseFloat(row[3]);
      float rey = Float.parseFloat(row[4]);
      float edx = lex - rex;
      float edy = ley - rey;
      double edt = pow(pow(edx, 2) + pow(edy, 2), 0.5);
      double s = 2.0 * 1.5 * edt;

      var lEye = new KmObj().add(pt(lex, ley)).withBounds(bounds(ley, lex, s / 8));
      var rEye = new KmObj().add(pt(rex, rey)).withBounds(bounds(rey, rex, s / 8));
      ki.add(lEye).add(rEye);
    }

    return ki;
  }

  public static KmImageList loadImages() {
    var groundTruth = new File("./datasets/caltech-10k-web-faces/data/WebFaces_GroundThruth.txt");
    var groundTruthUrl = OFnSupplier.tryGet(() -> groundTruth.toURI().toURL());
    var wfRoot = groundTruth.getParentFile();
    var faces = new TreeMap<>(
      stream(OzIo.loadLinesFrom(groundTruthUrl))
        .map(row -> row.split(" "))
        .collect(groupingBy(arr -> arr[0]))
    );
    var images = faces.entrySet().stream()
      .map(e -> from(new File(wfRoot, e.getKey()), e.getValue()))
      .filter(Objects::nonNull)
      .collect(toList());
    return new KmImageList().withAll(images);
  }

  public static void main(String[] args) throws FileNotFoundException {
    KmLogging.withLog(new KmTestLog());
    KmConfig.MaxTreeDepth = 9;

    var reg = KmRegion.trainDefault().withSizeMin(24).withSizeMax(384);
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    var kcOut = new File("./src/test/resources/puploc-java");
    var kc = KmGen.learnCascade(KmBoundBox.getDefault(), loadImages(), kr, reg);
    KmCascades.savePico(new FileOutputStream(kcOut), kc);
  }
}
