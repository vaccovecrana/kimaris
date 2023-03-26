package datasets.face_synthetics;

import impl.*;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.OFnSupplier;
import io.vacco.oruzka.io.OzIo;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;

import static datasets.face_synthetics.KmFsMark.mark;
import static io.vacco.kimaris.impl.KmImages.setMeta;
import static io.vacco.kimaris.schema.KmBounds.bounds;
import static io.vacco.kimaris.schema.KmPoint.pt;
import static java.util.stream.Collectors.toList;
import static java.lang.Float.parseFloat;

public class KmFsGen {

  public static KmFsMark eyePup  = mark("pup-left", 24, 64, 1.08f, 69, 70);
  public static KmFsMark eyeCorner = mark("eye-corner", 12, 36, 1.1f, 37, 40, 43, 46);
  public static KmFsMark mouthCornerOut = mark("mouth-corner-out", 16, 48, 1.08f, 49, 55);

  public static KmImage from(File imgFile, KmFsMark mark) {
    return OFnSupplier.tryGet(() -> {
      var meta = new File(imgFile.getParentFile(), imgFile.getName().replace(".png", "_ldmks.txt"));
      var ki = new KmImage().withImagePath(imgFile.getPath());
      var cra = Arrays.stream(OzIo.loadLinesFrom(meta.toURI().toURL()))
        .map(ln -> ln.split(" "))
        .toArray(String[][]::new);
      setMeta(ki, ImageIO.read(imgFile), true);
      for (int i : mark.ibIdx) {
        int k  = i - 1;
        var ck = cra[k];
        var pt = pt(parseFloat(ck[0]), parseFloat(ck[1]));
        var sc = mark.sizeMax;
        ki.add(
          new KmObj().add(pt).withBounds(
            bounds(pt.y, pt.x, sc).withTag(Integer.toString(k))
          )
        );
      }
      return ki;
    });
  }

  public static KmImageList loadImages(KmFsMark mark, int limit) {
    var images = Objects.requireNonNull(
      new File("./datasets/face-synthetics/dataset_100000")
        .listFiles(pathname ->
          pathname.getName().endsWith(".png") && !pathname.getName().contains("_seg")
        )
    );
    var rnd = new Random();
    var list = Arrays.stream(images)
      .filter(f -> rnd.nextBoolean())
      .map(img -> from(img, mark))
      .limit(limit)
      .collect(toList());
    return new KmImageList().withAll(list);
  }

  public static void main(String[] args) throws FileNotFoundException {
    KmLogging.withLog(new KmTestLog());
    KmConfig.MaxTreesPerStage = 24;
    KmConfig.MaxTreeDepth = 7;

    var marks = new KmFsMark[] {
      eyeCorner
    };
    for (var mark : marks) {
      var reg = KmRegion
        .trainDefault()
        .withSizeMin(mark.sizeMin)
        .withSizeMax(mark.sizeMax)
        .withScale(mark.trainScale);
      var kr = new KmRand().smwcRand(KmMathTest.RSeed);
      var kcOut = new File("./src/test/resources", mark.cascadeName);
      var kc = KmGen.learnCascade(KmBoundBox.getDefault(), loadImages(mark, 8_000), kr, reg, true);
      KmCascades.savePico(new FileOutputStream(kcOut), kc);
    }
  }

  /*
   - It takes about 20 minutes to do a whole training pass over a 5000 image set.
   - It takes about 1 hour to do a whole training pass over a 10000 image set.
   - It takes about half an hour to do 1 training iteration over a 20,000 image set.
   */
}
