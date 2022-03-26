package io.vacco.kimaris.schema;

public class KmScanParams {

  public int minSize;
  public int maxSize;
  public double shiftFactor;
  public double scaleFactor;

  public double iouThreshold, qThreshold;
  public int faceScaleThreshold;

  public static KmScanParams from(int minSize, int maxSize,
                                  double shiftFactor, double scaleFactor,
                                  double iouThreshold, double qThreshold,
                                  int faceScaleThreshold) {
    var cp = new KmScanParams();
    cp.minSize = minSize;
    cp.maxSize = maxSize;
    cp.shiftFactor = shiftFactor;
    cp.scaleFactor = scaleFactor;

    cp.iouThreshold = iouThreshold;
    cp.qThreshold = qThreshold;
    cp.faceScaleThreshold = faceScaleThreshold;

    return cp;
  }

  public static KmScanParams defaultParams() {
    return from(
        20, 1000, 0.2, 1.1,
        0.1, 5.0, // TODO confirm these two
        50
    );
  }

}
