package io.vacco.kimaris.schema;

public class KmAvg3F {

  public KmAvgF av0, av1, av2;

  public KmAvg3F init(int size) {
    this.av0 = new KmAvgF().init(size);
    this.av1 = new KmAvgF().init(size);
    this.av2 = new KmAvgF().init(size);
    return this;
  }

  public void update(KmBounds b) {
    av0.update(b.r);
    av1.update(b.c);
    av2.update(b.s);
  }

}
