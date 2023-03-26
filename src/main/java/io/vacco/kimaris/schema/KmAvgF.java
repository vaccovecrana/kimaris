package io.vacco.kimaris.schema;

public class KmAvgF {

  public float[] va;
  public float val;

  public KmAvgF init(int size) {
    this.va = new float[size];
    return this;
  }

  public KmAvgF update(float v) {
    for (int j = 0; j < va.length - 1; j++) {
      va[j] = va[j + 1];
    }
    va[va.length - 1] = v;
    val = 0;
    for (float value : va) {
      val = val + value;
    }
    val = val / va.length;
    return this;
  }

}
