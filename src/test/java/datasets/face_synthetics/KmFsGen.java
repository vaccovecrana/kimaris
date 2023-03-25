package datasets.face_synthetics;

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
import static java.util.stream.Collectors.toList;
import static java.lang.Float.parseFloat;

public class KmFsGen {

  public static Map<Integer, Integer> scaleOverrides = new TreeMap<>();
  static {
    scaleOverrides.put(68, 64); // eye pupils
    scaleOverrides.put(69, 64);
    scaleOverrides.put(49, 48); // left mouth corner (your left)
  }

  public static KmImage from(File imgFile, int ... flIdx) {
    return OFnSupplier.tryGet(() -> {
      var meta = new File(imgFile.getParentFile(), imgFile.getName().replace(".png", "_ldmks.txt"));
      var ki = new KmImage().withImagePath(imgFile.getPath());
      var cra = Arrays.stream(OzIo.loadLinesFrom(meta.toURI().toURL()))
        .map(ln -> ln.split(" "))
        .toArray(String[][]::new);
      setMeta(ki, ImageIO.read(imgFile), true);
      for (int k : flIdx) {
        var ck = cra[k];
        var pt = pt(parseFloat(ck[0]), parseFloat(ck[1]));
        var sc = scaleOverrides.get(k) != null ? scaleOverrides.get(k) : 6; // default scale
        ki.add(
          new KmObj().add(pt).withBounds(
            bounds(pt.y, pt.x, sc).withTag(Integer.toString(k))
          )
        );
      }
      return ki;
    });
  }

  public static KmImageList loadImages(int ... flIdx) {
    var images = Objects.requireNonNull(
      new File("./datasets/face-synthetics/dataset_100000")
        .listFiles(pathname ->
          pathname.getName().endsWith(".png") && !pathname.getName().contains("_seg")
        )
    );
    var rnd = new Random();
    var list = Arrays.stream(images)
      .filter(f -> rnd.nextBoolean())
      .map(img -> from(img, flIdx))
      .limit(10_000) // TODO this should be a parameter.
      .collect(toList());
    return new KmImageList().withAll(list);
  }

  // TODO define class which specifies individual face point configs.

  public static void main(String[] args) throws FileNotFoundException {
    KmLogging.withLog(new KmTestLog());
    KmConfig.MaxTreesPerStage = 24;
    KmConfig.MaxTreeDepth = 9;
    var reg = KmRegion.trainDefault()
      .withSizeMin(24)
      .withSizeMax(64) // TODO set training max size based on overrides index perhaps?
      .withScale(1.09f);
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    var kcOut = new File("./src/test/resources/left-mouth-corner");
    var kc = KmGen.learnCascade(KmBoundBox.getDefault(), loadImages(68, 69), kr, reg, true);
    KmCascades.savePico(new FileOutputStream(kcOut), kc);
  }

  /*
   - It takes about 20 minutes to do a whole training pass over a 5000 image set.
   - It takes about half an hour to do 1 training iteration over a 20,000 image set.
   */

}
