package io.vacco.kimaris.schema;

public class KmCoord {

  public int row;
  public int col;

  public KmCoord with(int row, int col) {
    this.col = col;
    this.row = row;
    return this;
  }

  @Override public String toString() {
    return String.format("crd[r: %d, c: %d]", row, col);
  }
}
