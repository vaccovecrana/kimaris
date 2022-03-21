package io.vacco.kimaris.schema;

public class KmFaceCascade {
  public long treeDepth;        // uint32
  public long treeNum;          // uint32
  public byte[] treeCodes;      // []int8
  public float[] treePred;      // []float32
  public float[] treeThreshold; // []float32
}
