package io.vacco.kimaris.schema;

public class KmImageParams {

  public short[][] grayMat;
  public int rows;
  public int cols;

  public short[][] blankBuf() {
    return new short[rows][cols];
  }
}
