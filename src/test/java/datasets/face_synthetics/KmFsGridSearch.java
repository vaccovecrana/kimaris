package datasets.face_synthetics;

import io.vacco.kimaris.schema.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static datasets.face_synthetics.KmFsGen.train;

public class KmFsGridSearch {

  public static class Params {
    public int maxTreesPerStage, maxTreeDepth;
    public float trainScale;

    public static Params of(int maxTreesPerStage, int maxTreeDepth, float trainScale) {
      var p = new Params();
      p.maxTreeDepth = maxTreeDepth;
      p.maxTreesPerStage = maxTreesPerStage;
      p.trainScale = trainScale;
      return p;
    }

    @Override public String toString() {
      return String.format(
        "[maxTreeDepth: %d, maxTrees: %d, scale: %.04f]",
        maxTreeDepth, maxTreesPerStage, trainScale
      );
    }
  }

  public static final int[] treeDepthA  = { 6, 8, 10 };
  public static final int[] maxTreesA = { 16, 20, 24 };
  public static final float[] scales    = { 1.1f, 1.3f, 1.5f, 1.7f, 1.9f };

  public static Map<Float, Params> apply(KmImageList images) {
    KmConfig.TrainMaxObjects = 500_000;

    var models = new ConcurrentHashMap<Float, Params>();
    var candidates = new ArrayList<Params>();
    var topTpr = new ConcurrentHashMap<Float, Float>();

    for (int treeDepth : treeDepthA) {
      for (int maxTrees : maxTreesA) {
        for (float scale : scales) {
          candidates.add(Params.of(maxTrees, treeDepth, scale));
        }
      }
    }

    candidates.parallelStream().forEach(p -> {
      try {
        var kc = train(p.maxTreesPerStage, p.maxTreeDepth, p.trainScale, images, false);
        if (kc.trainSmp.eTpr > 0.6) {
          System.out.println("Potential parameter set: " + p);
          models.put(kc.trainSmp.eTpr, p);
        }
        topTpr.put(-kc.trainSmp.eTpr, -kc.trainSmp.eTpr);
        System.out.println("Top TPR so far: " + Math.abs(new TreeMap<>(topTpr).values().iterator().next()));
      } catch (Exception e) {
        System.out.printf("WARN: %s %s - %s%n", e.getClass().getSimpleName(), e.getMessage(), p);
        if (!(e instanceof IllegalStateException)) {
          e.printStackTrace();
        }
      }
    });

    return new TreeMap<>(models);
  }

}
