package io.vacco.kimaris.schema;

public class KmFace {
  public KmCoord loc;
  public KmCoord leftEye, rightEye;

  public KmCoord[] eyeMarks;
  public KmCoord[] mouthMarks;

  public KmFace withLoc(KmCoord l) {
    this.loc = l;
    return this;
  }
}
