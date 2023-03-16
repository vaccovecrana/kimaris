package io.vacco.kimaris.schema;

public class KmClass {

  public float o;
  public int label;

  public static KmClass classify(float o, int type) {
    var r = new KmClass();
    r.o = o;
    r.label = type;
    return r;
  }

}
