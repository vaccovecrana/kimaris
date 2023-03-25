package datasets.caltech_10k_web_faces;

import impl.*;
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

public class KmCtFacesGen {

  public static KmImage from(File imgFile, List<String[]> rows) {
    if (!imgFile.exists()) {
      System.out.println("Missing image file: " + imgFile.getAbsolutePath());
      return null;
    }
    var ki = new KmImage().withImagePath(imgFile.getPath());
    tryRun(() -> setMeta(ki, ImageIO.read(imgFile), true));

    for (var row : rows) {
      float lex = Float.parseFloat(row[1]);
      float ley = Float.parseFloat(row[2]);
      float rex = Float.parseFloat(row[3]);
      float rey = Float.parseFloat(row[4]);
      float nox = Float.parseFloat(row[5]);
      float noy = Float.parseFloat(row[6]);
      float mox = Float.parseFloat(row[7]);
      float moy = Float.parseFloat(row[8]);

      var smp = new KmObj()
        .add(pt(lex, ley)).add(pt(rex, rey))
        .add(pt(nox, noy)).add(pt(mox, moy));
      float  edx = lex - rex;
      float  edy = ley - rey;
      double edt = pow(pow(edx, 2) + pow(edy, 2), 0.5);
      double r = (ley + rey) / 2.0 + 0.25 * edt;
      double c = (lex + rex) / 2.0;
      double s = 2.0 * 1.5 * edt;

      smp.withBounds(bounds(r, c, s));
      ki.add(smp);
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
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    var kcOut = new File("./src/test/resources/facefinder-java");
    var reg = KmRegion.trainDefault();
    var kc = KmGen.learnCascade(KmBoundBox.getDefault(), loadImages(), kr, reg, false);
    KmCascades.savePico(new FileOutputStream(kcOut), kc);
  }
}
