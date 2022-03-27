package io.vacco.kimaris.schema;

public class KmDetection {

  public KmCoord coord;
  public double q;

  public static KmDetection from(int row, int col, int scale, double q) {
    var det = new KmDetection();
    det.coord = KmCoord.from(row, col, scale);
    det.q = q;
    return det;
  }

  @Override public String toString() {
    return String.format("det[%s, q: %.3f]", coord, q);
  }
}
