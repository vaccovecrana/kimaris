package datasets.face_synthetics;

import java.util.Objects;
import static datasets.face_synthetics.KmIBugPt.*;

public enum KmIBugMark {

  EyePup(9, 20, 1.5f, new int[] {3, 4}, "eye-pup", IB69, IB70), // TODO ok, just needs more training data.
  EyeCornerIn(9, 20, 1.25f, new int[] {3, 4}, "eye-corner-in", IB40, IB43), // TODO ok, just needs more training data.
  EyeCornerOut(9, 20, 1.20f, new int[] {3, 4}, "eye-corner-out", IB37, IB46), // TODO ok, just needs more training data.

  MouthCornerOut(9, 16, 1.15f, new int[] {10, 11}, "mouth-corner-out", IB49, IB55)
  ;

  public final String cascadeName;
  public final KmIBugPt[] points;
  public final int[] requiredClasses;

  public final int maxTreeDepth, maxTreesPerStage;
  public final float trainScale;

  KmIBugMark(int maxTreeDepth, int maxTreesPerStage,
             float trainScale, int[] requiredClasses,
             String cascadeName, KmIBugPt ... points) {
    this.maxTreeDepth = maxTreeDepth;
    this.maxTreesPerStage = maxTreesPerStage;
    this.trainScale = trainScale;
    this.cascadeName = Objects.requireNonNull(cascadeName);
    this.points = Objects.requireNonNull(points);
    this.requiredClasses = Objects.requireNonNull(requiredClasses);
  }

  @Override public String toString() {
    return String.format(
      "%s [maxTreeDepth: %d, maxTrees: %d, scale: %.04f]",
      cascadeName, maxTreeDepth, maxTreesPerStage, trainScale
    );
  }

}
