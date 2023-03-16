package io.vacco.kimaris.schema;

public class KmPoint {

  public int x, y;

  public KmPoint with(int x, int y) {
    this.x = x;
    this.y = y;
    return this;
  }

  public static KmPoint pt(int x, int y) {
    var p = new KmPoint();
    return p.with(x, y);
  }

  public static KmPoint pt(float x, float y) {
    return pt((int) x, (int) y);
  }

  @Override public String toString() {
    return String.format("[x: %d, y: %d]", x, y);
  }

}
