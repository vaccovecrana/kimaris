package io.vacco.kimaris.schema;

public class KmCoord {

  public int row;
  public int col;
  public double scale;

  public boolean valid() {
    return row > 0 && col > 0;
  }

  public static KmCoord from(int row, int col, double scale) {
    var c = new KmCoord();
    c.col = col;
    c.row = row;
    c.scale = scale;
    return c;
  }

  @Override public String toString() {
    return String.format("crd[r: %d, c: %d, s: %.3f]", row, col, scale);
  }
}
