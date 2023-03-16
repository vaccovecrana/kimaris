package io.vacco.kimaris.schema;

import java.util.Objects;

public class KmBuffer {

  /*
   * Attributes used for serialization and runtime detection.
   */
  public int nTrees;
  public int treeDepth;
  public int[][] tCodes = new int[KmConfig.Kb0][KmConfig.Kb1];
  public float[][] luts = new float[KmConfig.Kb0][KmConfig.Kb1];
  public float[] thresholds = new float[KmConfig.Kb0];
  public KmBoundBox boundBox;

  /*
   * Attributes used for training.
   *
   * These arrays hold image indices, tree classification values and outputs.
   * Indices get shuffled several times during training for data sampling.
   *
   * Lord help me...
   */
  public transient int nObjs;
  public transient KmObj[] s;
  public transient int[] iinds;
  public transient float[] tVals;
  public transient float[] os;

  public KmBuffer initForDetection(int treeDepth, KmBoundBox bb) {
    this.boundBox = Objects.requireNonNull(bb);
    this.treeDepth = treeDepth;
    return this;
  }

  public KmBuffer initForTraining() {
    this.nObjs = KmConfig.TrainMaxObjects;
    this.s      = new KmObj[2 * nObjs];
    this.iinds = new int[2 * nObjs];
    this.tVals = new float[2 * nObjs];
    this.os     = new float[2 * nObjs];
    return this;
  }

}
