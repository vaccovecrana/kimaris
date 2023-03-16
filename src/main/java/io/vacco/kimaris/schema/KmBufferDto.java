package io.vacco.kimaris.schema;

public class KmBufferDto {

  public long version;

  public int nObjs;
  public int nTrees;
  public int treeDepth;
  public int[][] tCodes;
  public float[][] luts;
  public float[] thresholds;
  public KmBoundBox boundBox;

}
