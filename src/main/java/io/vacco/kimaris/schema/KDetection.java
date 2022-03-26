package io.vacco.kimaris.schema;

public class KDetection {

  public int row;
  public int col;
  public int scale;
  public double q;

  public static KDetection from(int row, int col, int scale, double q) {
    var det = new KDetection();
    det.row = row;
    det.col = col;
    det.scale = scale;
    det.q = q;
    return det;
  }
}
