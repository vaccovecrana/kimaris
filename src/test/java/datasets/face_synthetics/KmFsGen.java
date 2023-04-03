package datasets.face_synthetics;

import impl.*;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.*;
import io.vacco.oruzka.io.OzIo;
import javax.imageio.ImageIO;
import java.awt.image.DataBufferByte;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import static io.vacco.kimaris.impl.KmImages.setMeta;
import static java.util.stream.Collectors.toList;

public class KmFsGen {

  public static File[] faceSynthetics() {
    return Objects.requireNonNull(
      new File("./datasets/face-synthetics/dataset_100000")
        .listFiles(pathname ->
          pathname.getName().endsWith(".png") && !pathname.getName().contains("_seg")
        )
    );
  }

  public static String[][] pointListOf(File imgFile) throws MalformedURLException {
    var meta = new File(imgFile.getParentFile(), imgFile.getName().replace(".png", "_ldmks.txt"));
    return Arrays.stream(OzIo.loadLinesFrom(meta.toURI().toURL()))
      .map(ln -> ln.split(" "))
      .toArray(String[][]::new);
  }

  // https://github.com/microsoft/FaceSynthetics#dataset-layout
  public static boolean hasSegmentationClasses(File segImgFile, KmIBugMark mark) {
    return OFnSupplier.tryGet(() -> {
      var segImg = ImageIO.read(segImgFile);
      var segBuf = ((DataBufferByte) segImg.getData().getDataBuffer()).getData();
      var classes = new int[24];
      var th = 16; // at least 16 class pixels for sampling
      for (byte b : segBuf) {
        if (b > 0) {
          classes[b] = classes[b] + 1;
        }
      }
      for (int mkc : mark.requiredClasses) {
        if (classes[mkc] < th) {
          return false;
        }
      }
      return true;
    });
  }

  public static KmImage from(File imgFile, KmIBugMark mark) {
    return OFnSupplier.tryGet(() -> {
      var ki = new KmImage().withImagePath(imgFile.getPath());
      setMeta(ki, ImageIO.read(imgFile), true);
      var xya = pointListOf(imgFile);
      for (var mk : mark.points) {
        ki.add(mk.map(xya));
      }
      return ki;
    });
  }

  public static KmImageList loadImages(KmIBugMark mark, int limit) {
    var images = faceSynthetics();
    var fl = Arrays.stream(images).collect(toList());
    var il = 0;
    var kil = new KmImageList();
    Collections.shuffle(fl);
    var it = fl.iterator();
    while (il < limit) {
      var img = it.next();
      var segF = new File(img.getParentFile(), img.getName().replace(".png", "_seg.png"));
      if (hasSegmentationClasses(segF, mark)) {
        kil.add(from(img, mark));
        il = il + 1;
      }
    }
    return kil;
  }

  public static KmBuffer train(int maxTreesPerStage, int maxTreeDepth, float trainScale,
                               KmImageList images, boolean thread) {
    System.out.printf("Training with [%d] images%n", images.size());
    var reg = KmRegion
      .trainDefault()
      .withSizeMin(images.sizeMin)
      .withSizeMax(images.sizeMax)
      .withScale(trainScale);
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    return KmGen.learnCascade(
      KmBoundBox.getDefault(), images, kr, reg,
      maxTreesPerStage, maxTreeDepth, thread
    );
  }

  public static void main(String[] args) {
    var mk = KmIBugMark.EyePup;
    var images = loadImages(mk, 8192).updateSizeRange();

    KmLogging.withLog(new KmTestLog().withLogInfo(true));
    var kc = train(mk.maxTreesPerStage, mk.maxTreeDepth, mk.trainScale, images, true);
    var kcOut = new File("./src/test/resources", mk.cascadeName);
    OFnBlock.tryRun(() -> KmCascades.savePico(new FileOutputStream(kcOut), kc));
    /*
    */

    /*
    KmLogging.withLog(new KmTestLog().withLogInfo(false));
    var models = KmFsGridSearch.apply(images);
    System.out.println(models);
    */
  }

}
