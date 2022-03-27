package io.vacco.kimaris.schema;

public class KmScanParams {

  public int minSize;
  public int maxSize;
  public double shiftFactor;
  public double scaleFactor;

  public double iouThreshold, qThreshold;
  public int faceScaleThreshold;

  public double leftEyeOffsetRow, leftEyeOffsetCol, leftEyeOffsetScale;
  public double rightEyeOffsetRow, rightEyeOffsetCol, rightEyeOffsetScale;
  public int perturbations;

  public static KmScanParams defaultParams() {
    var cp = new KmScanParams();

    cp.minSize = 20;
    cp.maxSize = 1000;
    cp.shiftFactor = 0.15;
    cp.scaleFactor = 1.15;

    cp.iouThreshold = 0.15;
    cp.qThreshold = 5.0;
    cp.faceScaleThreshold = 50;

    cp.leftEyeOffsetRow = 0.075;
    cp.leftEyeOffsetCol = 0.175;
    cp.leftEyeOffsetScale = 0.25;

    cp.rightEyeOffsetRow = 0.075;
    cp.rightEyeOffsetCol = 0.185;
    cp.rightEyeOffsetScale = 0.25;

    cp.perturbations = 63;

    return cp;
  }

}
