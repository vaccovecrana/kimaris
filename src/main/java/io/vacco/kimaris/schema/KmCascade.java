package io.vacco.kimaris.schema;

public class KmCascade {
  public String id;
  public long stages;       // uint32
  public float scales;
  public long trees;        // uint32
  public long treeDepth;    // uint32
  public byte[] treeCodes;
  public float[] treePreds;
}
