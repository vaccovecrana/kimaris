package datasets.face_synthetics;

import io.vacco.kimaris.schema.KmBuffer;
import java.util.Objects;

public class KmFsMark {

  public String cascadeName;
  public int[] ibIdx;
  public int maxTreeDepth, maxTreesPerStage;
  public float trainScale;

  public KmBuffer cascade;

  public static KmFsMark mark(String cascadeName,
                              int maxTreeDepth, int maxTreesPerStage,
                              float trainScale, int ... ibIdx) {
    var m = new KmFsMark();
    m.cascadeName = Objects.requireNonNull(cascadeName);
    m.ibIdx = Objects.requireNonNull(ibIdx);
    m.maxTreeDepth = maxTreeDepth;
    m.maxTreesPerStage = maxTreesPerStage;
    m.trainScale = trainScale;
    return m;
  }

  @Override public String toString() {
    return String.format(
      "[maxTreeDepth: %d, maxTrees: %d, scale: %.04f]",
      maxTreeDepth, maxTreesPerStage, trainScale
    );
  }
}
