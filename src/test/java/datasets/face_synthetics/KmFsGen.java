package datasets.face_synthetics;

import impl.KmTestLog;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.*;
import io.vacco.oruzka.io.OzIo;
import javax.imageio.ImageIO;
import java.io.*;
import java.net.MalformedURLException;
import java.util.*;

import static io.vacco.kimaris.impl.KmMath.eclDist;
import static datasets.face_synthetics.KmFsMark.mark;
import static io.vacco.kimaris.impl.KmImages.setMeta;
import static io.vacco.kimaris.schema.KmBounds.bounds;
import static io.vacco.kimaris.schema.KmPoint.pt;
import static java.util.stream.Collectors.toList;
import static java.lang.Float.parseFloat;

public class KmFsGen {

  public static final KmFsMark eye  = mark("eye", 6, 16, 1.08f, 69, 70);

  /*
  {
    0.7109375=[maxTreeDepth: 10, maxTrees: 20, scale: 1.3000],
    0.734375=[maxTreeDepth: 10, maxTrees: 24, scale: 1.9000]
  }
   */
  public static final KmFsMark eyeCornerIn = mark("eye-corner-in", 10, 20, 1.3f, 40, 43);

  public static final KmFsMark mouthCornerOut = mark("mouth-corner-out", 6, 16, 1.08f, 49, 55);

  public static String[][] meta300w(File imgFile) throws MalformedURLException {
    var meta = new File(imgFile.getParentFile(), imgFile.getName().replace(".png", ".pts")); // "_ldmks.txt" - face synthetics dataset
    return Arrays.stream(OzIo.loadLinesFrom(meta.toURI().toURL()))
      .filter(ln -> !ln.startsWith("version"))
      .filter(ln -> !ln.startsWith("n_points"))
      .filter(ln -> !ln.startsWith("{"))
      .filter(ln -> !ln.startsWith("}"))
      .map(ln -> ln.split(" "))
      .toArray(String[][]::new);
  }

  public static String[][] metaFaceSynthetics(File imgFile) throws MalformedURLException {
    var meta = new File(imgFile.getParentFile(), imgFile.getName().replace(".png", "_ldmks.txt"));
    return Arrays.stream(OzIo.loadLinesFrom(meta.toURI().toURL()))
      .map(ln -> ln.split(" "))
      .toArray(String[][]::new);
  }

  public static KmPoint getPoint(String[][] xya, int ptIdx) {
    return pt(parseFloat(xya[ptIdx - 1][0]), parseFloat(xya[ptIdx - 1][1]));
  }

  public static KmImage from(File imgFile, KmFsMark mark) {
    return OFnSupplier.tryGet(() -> {
      var ki = new KmImage().withImagePath(imgFile.getPath());
      setMeta(ki, ImageIO.read(imgFile), true);

      // TODO these should be function arguments for other face points.
      //   but how do we calculate S for each input point?

      var xya = metaFaceSynthetics(imgFile);
      // get right eye corner
      var pt39 = getPoint(xya, 39);
      var pt40 = getPoint(xya, 40);
      var rec = new KmObj().add(pt40).withBounds(bounds(pt40.y, pt40.x, eclDist(pt39, pt40) * 1.3));

      // get left eye corner
      var pt43 = getPoint(xya, 43);
      var pt44 = getPoint(xya, 44);
      var lec = new KmObj().add(pt43).withBounds(bounds(pt43.y, pt43.x, eclDist(pt44, pt43) * 1.3));

      ki.add(rec).add(lec);

      return ki;
    });
  }

  public static KmImageList loadImages(KmFsMark mark, int limit) {
    // "./datasets/300W/01_Indoor"
    var images = Objects.requireNonNull(
      new File("./datasets/face-synthetics/dataset_100000")
        .listFiles(pathname ->
          pathname.getName().endsWith(".png") && !pathname.getName().contains("_seg")
        )
    );
    var fl = Arrays.stream(images).collect(toList());
    Collections.shuffle(fl);
    var list = fl.stream()
      .limit(limit)
      .map(img -> from(img, mark))
      .collect(toList());
    return new KmImageList().withAll(list);
  }

  public static void main(String[] args) {
    var images = loadImages(eyeCornerIn, 8192).updateSizeRange();
    var marks = new KmFsMark[] {
      eyeCornerIn
    };
    KmLogging.withLog(new KmTestLog().withLogInfo(true));
    for (var mk : marks) {
      var kc = KmFsGridSearch.train(mk, images, true);
      var kcOut = new File("./src/test/resources", mk.cascadeName);
      OFnBlock.tryRun(() -> KmCascades.savePico(new FileOutputStream(kcOut), kc));
    }
    /*
    KmLogging.withLog(new KmTestLog().withLogInfo(false));
    var models = KmFsGridSearch.apply(images);
    System.out.println(models);
    */
  }

}
