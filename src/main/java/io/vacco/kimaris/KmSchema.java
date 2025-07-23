package io.vacco.kimaris;

public class KmSchema {

  public static class KmCoord {
    public int row;
    public int col;

    public KmCoord at(int row, int col) {
      this.col = col;
      this.row = row;
      return this;
    }

    @Override public String toString() {
      return String.format("[r: %d, c: %d]", row, col);
    }
  }

  public static class KmImageParams {
    public short[][] grayMat;
    public int rows;
    public int cols;

    public short[][] blankBuf() {
      return new short[rows][cols];
    }
  }

  public static class KmMbLbpBlock {
    public KmCoord origin;
    public short[][] region;
    public short lbp;
    public boolean[] lbpBuf = new boolean[8];
  }

}
