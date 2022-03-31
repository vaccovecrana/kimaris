package io.vacco.kimaris.schema;

public class KmCoord {

  public String label;
  public int row;
  public int col;
  public double scale;

  public boolean valid() {
    return row > 0 && col > 0;
  }

  public KmCoord withLabel(String label) {
    this.label = label;
    return this;
  }

  public static KmCoord from(int row, int col, double scale) {
    var c = new KmCoord();
    c.col = col;
    c.row = row;
    c.scale = scale;
    return c;
  }

  @Override public String toString() {
    return String.format("crd[l: %s, r: %d, c: %d, s: %.3f]", label, row, col, scale);
  }
}
