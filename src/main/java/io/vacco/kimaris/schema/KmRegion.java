package io.vacco.kimaris.schema;

import static java.lang.Math.min;

public class KmRegion {

  public float[] rcsq;
  public int[] a;

  public KmBounds[] detections;
  public int detectCount, detectMax;
  public float detectThreshold, overlapThreshold;
  public float scale, sizeMin, sizeMax;
  public float stride;

  public KmRegion init(int detectMax,
                       float detectThreshold, float overlapThreshold,
                       float scale, float sizeMin, float sizeMax,
                       float stride) {
    this.detectMax = detectMax;
    this.detectThreshold = detectThreshold;
    this.overlapThreshold = overlapThreshold;
    this.scale = scale;
    this.sizeMin = sizeMin;
    this.sizeMax = sizeMax;
    this.stride = stride;
    return this.withDetectMax(detectMax);
  }

  public KmRegion withDetectMax(int detectMax) {
    this.detectMax = detectMax;
    this.rcsq = new float[4 * detectMax];
    this.detections = new KmBounds[detectMax];
    return this;
  }

  public KmRegion withDetectMaxConn(int detectMaxConn) {
    this.a = new int[detectMaxConn];
    return this;
  }

  public KmRegion withDetectThreshold(float detectThreshold) {
    this.detectThreshold = detectThreshold;
    return this;
  }

  public KmRegion withOverlapThreshold(float overlapThreshold) {
    this.overlapThreshold = overlapThreshold;
    return this;
  }

  public KmRegion withSizeMin(float sizeMin) {
    this.sizeMin = sizeMin;
    return this;
  }

  public KmRegion withSizeMax(float sizeMax) {
    this.sizeMax = sizeMax;
    return this;
  }

  public KmRegion withScale(float scale) {
    this.scale = scale;
    return this;
  }

  public void fitTo(KmImage img) {
    this.sizeMax = min(this.sizeMax, min(img.width, img.height));
  }

  public void clearDetections() {
    this.detectCount = 0;
    if (detections != null) {
      for (var det : detections) {
        if (det != null) {
          det.invalidate();
        }
      }
    }
  }

  public static KmRegion trainDefault() {
    return new KmRegion().init(
      8192, 0.0f, 0.6f,
      1.1f, 24.0f, 1000.0f, 0.1f
    );
  }

  public static KmRegion detectDefault() {
    return new KmRegion().init(
      2048, 5.0f, 0.3f,
      1.1f, 128.0f, 1024.0f, 0.1f
    ).withDetectMaxConn(KmConfig.DetectMaxConn);
  }

}
