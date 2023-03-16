package io.vacco.kimaris.schema;

public class KmSample {

  public int np = 0, nn = 0;
  public float eTpr, eFpr;

  @Override public String toString() {
    return String.format(
      "[np: %d, nn: %d, tpr: %.4f, fpr: %.4f]",
      np, nn, eTpr, eFpr
    );
  }
}
