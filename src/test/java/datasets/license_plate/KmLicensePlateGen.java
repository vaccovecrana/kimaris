package datasets.license_plate;

import com.google.gson.Gson;
import datasets.KmCoco;
import impl.KmMathTest;
import impl.KmTestLog;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import javax.imageio.ImageIO;
import java.io.*;

public class KmLicensePlateGen {

  public static void main(String[] args) throws Exception {
    var g = new Gson();
    var coco = g.fromJson(new FileReader(
      "./datasets/license-plate/train/_annotations.coco.json"),
      KmCoco.class
    );
    var kl = new KmImageList();

    for (var ci : coco.images) {
      var bi = ImageIO.read(new File("./datasets/license-plate/train", ci.file_name));
      var ki = new KmImage().withImagePath(ci.file_name);
      KmImages.setMeta(ki, bi, true);
      coco.annotations.stream().filter(ann -> ann.image_id == ci.id).forEach(ann -> {
        var xMin = ann.bbox[0];
        var yMin = ann.bbox[1];
        var xMax = xMin + ann.bbox[2];
        var yMax = yMin + ann.bbox[3];
        var b = KmMath.boundsFrom(xMin, yMin, xMax, yMax);
        var obj = new KmObj().withBounds(b.withTag(coco.categories.get(0).name));
        ki.add(obj);
      });
      kl.add(ki);
    }

    KmLogging.withLog(new KmTestLog());
    KmConfig.MaxTreesPerStage = 20;
    KmConfig.MaxTreeDepth = 9;

    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    var kcOut = new File("./src/test/resources/license-plate");
    var reg = KmRegion.trainDefault();
    var kc = KmGen.learnCascade(KmBoundBox.getDefault(), kl, kr, reg);
    KmCascades.savePico(new FileOutputStream(kcOut), kc);
  }

}
