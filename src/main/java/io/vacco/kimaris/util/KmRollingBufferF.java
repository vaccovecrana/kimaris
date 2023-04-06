package io.vacco.kimaris.util;

public class KmRollingBufferF {

  public float[] va;

  public KmRollingBufferF init(int size) {
    this.va = new float[size];
    return this;
  }

  public KmRollingBufferF update(float v) {
    for (int j = 0; j < va.length - 1; j++) {
      va[j] = va[j + 1];
    }
    va[va.length - 1] = v;
    return this;
  }

}
