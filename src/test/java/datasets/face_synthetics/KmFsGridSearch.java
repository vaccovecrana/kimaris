package datasets.face_synthetics;

import impl.KmMathTest;
import io.vacco.kimaris.impl.KmGen;
import io.vacco.kimaris.schema.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class KmFsGridSearch {

  public static final int[] treeDepthA  = { 6, 8, 10 };
  public static final int[] maxTreesA = { 16, 20, 24 };
  public static final float[] scales    = { 1.1f, 1.3f, 1.5f, 1.7f, 1.9f };

  public static KmBuffer train(KmFsMark mk, KmImageList images, boolean thread) {
    var reg = KmRegion
      .trainDefault()
      .withSizeMin(images.sizeMin)
      .withSizeMax(images.sizeMax)
      .withScale(mk.trainScale);
    var kr = new KmRand().smwcRand(KmMathTest.RSeed);
    return KmGen.learnCascade(
      KmBoundBox.getDefault(), images, kr, reg,
      mk.maxTreesPerStage, mk.maxTreeDepth, thread
    );
  }

  public static Map<Float, KmFsMark> apply(KmImageList images) {
    KmConfig.TrainMaxObjects = 500_000;

    var models = new ConcurrentHashMap<Float, KmFsMark>();
    var candidates = new ArrayList<KmFsMark>();
    var topTpr = new ConcurrentHashMap<Float, Float>();

    for (int treeDepth : treeDepthA) {
      for (int maxTrees : maxTreesA) {
        for (float scale : scales) {
          var mk = new KmFsMark();
          mk.maxTreesPerStage = maxTrees;
          mk.maxTreeDepth = treeDepth;
          mk.trainScale = scale;
          candidates.add(mk);
        }
      }
    }

    candidates.parallelStream().forEach(mk -> {
      try {
        var kc = train(mk, images, false);
        if (kc.trainSmp.eTpr > 0.6) {
          System.out.println("Potential parameter set: " + mk);
          mk.cascade = kc;
          models.put(mk.cascade.trainSmp.eTpr, mk);
        }
        topTpr.put(-kc.trainSmp.eTpr, -kc.trainSmp.eTpr);
        System.out.println("Top TPR so far: " + Math.abs(new TreeMap<>(topTpr).values().iterator().next()));
      } catch (Exception e) {
        System.out.printf("WARN: %s %s - %s%n", e.getClass().getSimpleName(), e.getMessage(), mk);
        if (!(e instanceof IllegalStateException)) {
          e.printStackTrace();
        }
      }
    });

    return new TreeMap<>(models);
  }

}
