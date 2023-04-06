package io.vacco.kimaris.util;

import io.vacco.kimaris.schema.KmBounds;

public class KmAvg3F {

  public KmRollingBufferF av0, av1, av2;
  public float v0, v1, v2;

  public KmAvg3F init(int size) {
    this.av0 = new KmRollingBufferF().init(size);
    this.av1 = new KmRollingBufferF().init(size);
    this.av2 = new KmRollingBufferF().init(size);
    return this;
  }

  private float update(KmRollingBufferF bf) {
    var v = 0f;
    for (float value : bf.va) {
      v = v + value;
    }
    return v / bf.va.length;
  }

  public void update(KmBounds b) {
    av0.update(b.r);
    av1.update(b.c);
    av2.update(b.s);
    v0 = update(av0);
    v1 = update(av1);
    v2 = update(av2);
  }

  public void copyTo(KmBounds b) {
    b.with((int) v0, (int) v1, (int) v2);
  }

}
