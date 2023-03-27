package datasets.face_synthetics;

import java.util.Objects;

public class KmFsMark {

  public String cascadeName;
  public int[] ibIdx;
  public int sizeMin, sizeMax, maxTreeDepth, maxTreesPerStage;
  public float trainScale;

  public static KmFsMark mark(String cascadeName,
                              int sizeMin, int sizeMax,
                              int maxTreeDepth, int maxTreesPerStage,
                              float trainScale, int ... ibIdx) {
    var m = new KmFsMark();
    m.cascadeName = Objects.requireNonNull(cascadeName);
    m.ibIdx = Objects.requireNonNull(ibIdx);
    m.sizeMin = sizeMin;
    m.sizeMax = sizeMax;
    m.maxTreeDepth = maxTreeDepth;
    m.maxTreesPerStage = maxTreesPerStage;
    m.trainScale = trainScale;
    return m;
  }

}
