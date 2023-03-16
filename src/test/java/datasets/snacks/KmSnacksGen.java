package datasets.snacks;

import impl.KmMathTest;
import impl.KmTestLog;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.OFnSupplier;
import io.vacco.oruzka.io.OzIo;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static io.vacco.kimaris.schema.KmPoint.pt;
import static java.lang.String.format;

public class KmSnacksGen {

  public static KmImage map(List<String[]> rows) {
    var img0 = rows.get(0);
    var localFile = new File("./datasets/snacks/data/train", format("%s/%s.jpg", img0[6], img0[0]));
    if (!localFile.exists()) {
      System.out.println("Missing file: " + localFile);
      return null;
    }

    var bi = OFnSupplier.tryGet(() -> ImageIO.read(localFile));
    var ki = new KmImage().withImagePath(localFile.getPath());

    KmImages.setMeta(ki, bi, true);

    for (var arr : rows) {
      double xMin = Double.parseDouble(arr[1]);
      double xMax = Double.parseDouble(arr[2]);
      double yMin = Double.parseDouble(arr[3]);
      double yMax = Double.parseDouble(arr[4]);
      var className = arr[5];

      // Us mere mortals just need image pixel X/Y coordinates... sigh...
      xMin = ki.width  * xMin;
      xMax = ki.width  * xMax;
      yMin = ki.height * yMin;
      yMax = ki.height * yMax;

      var b = KmMath.boundsFrom(xMin, yMin, xMax, yMax);

      ki.add(
        new KmObj()
          .add(pt((int) xMin, (int) yMin))
          .add(pt((int) xMax, (int) yMax))
          .withBounds(b.withTag(className))
      );
    }

    return ki;
  }

  public static KmImageList loadTrainingSet(String classFilter) {
    var trainCsv = new File("./datasets/snacks/train.csv");
    return OFnSupplier.tryGet(() -> {
      var imgIdx = Arrays.stream(OzIo.loadLinesFrom(trainCsv.toURI().toURL()))
        .filter(line -> !line.startsWith("image_id"))
        .map(line -> line.split(","))
        .collect(Collectors.groupingBy(arr -> arr[0]));

      var snacks = imgIdx.values().stream()
        .map(KmSnacksGen::map)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

      for (var sni : snacks) {
        sni.objects = sni.objects.stream()
          .filter(obj -> obj.bounds.tag.equals(classFilter))
          .collect(Collectors.toList());
      }

      var snackIdx = snacks.stream()
        .flatMap(sl -> sl.objects.stream())
        .collect(Collectors.groupingBy(obj -> obj.bounds.tag));

      for (var e : snackIdx.entrySet()) {
        System.out.printf("[%s -> %d]%n", e.getKey(), e.getValue().size());
      }

      return new KmImageList().withAll(snacks);
    });
  }

  public static void main(String[] args) throws FileNotFoundException {

    var targetSnack = "strawberry"; // "banana" "apple"

    KmLogging.withLog(new KmTestLog());
    KmConfig.MaxTreesPerStage = 20;
    KmConfig.MaxTreeDepth = 8;

    var reg = KmRegion.trainDefault().withScale(1.02f);
    var list = loadTrainingSet(targetSnack);
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    var kcOut = new File("./src/test/resources", targetSnack);
    var kc = KmGen.learnCascade(KmBoundBox.getDefault(), list, kr, reg);
    KmCascades.savePico(new FileOutputStream(kcOut), kc);
  }

}
