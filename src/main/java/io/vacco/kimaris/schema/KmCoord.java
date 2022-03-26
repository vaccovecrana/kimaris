package io.vacco.kimaris.schema;

public class KmCoord {

  public int row;
  public int col;
  public int scale;

  public static KmCoord from(int row, int col, int scale) {
    var c = new KmCoord();
    c.col = col;
    c.row = row;
    c.scale = scale;
    return c;
  }

}
