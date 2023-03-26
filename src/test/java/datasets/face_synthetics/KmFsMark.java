package datasets.face_synthetics;

import java.util.Objects;

public class KmFsMark {

  public String cascadeName;
  public int[] ibIdx;
  public int sizeMin, sizeMax;
  public float trainScale;

  public static KmFsMark mark(String cascadeName, int sizeMin, int sizeMax, float trainScale, int ... ibIdx) {
    var m = new KmFsMark();
    m.cascadeName = Objects.requireNonNull(cascadeName);
    m.ibIdx = Objects.requireNonNull(ibIdx);
    m.sizeMin = sizeMin;
    m.sizeMax = sizeMax;
    m.trainScale = trainScale;
    return m;
  }

}
