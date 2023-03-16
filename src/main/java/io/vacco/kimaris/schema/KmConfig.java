package io.vacco.kimaris.schema;

/**
 * These are *most* of the hard-coded constants I could
 * find in Nenad's code. If needed, change them before
 * training a cascade, or performing detection.
 */
public class KmConfig {

  public static final long PicoVersion = 3;

  //////////////// Cascade parameters ////////////////

  public static int MaxTreeDepth = 6;
  public static int MaxTreesPerStage = 16;

  public static float StageTpr = 0.98f;
  public static float StageFpr = 0.4f;

  public static float StageThreshold = 5.0f;
  public static float StageThresholdDelta = 0.001f;

  public static float FprThreshold = 0.01f;

  //////////////// Train data search parameters ////////////////

  public static int Kb0 = 4096, Kb1 = 1024, NRands = 128;

  public static int TrainMaxObjects = 1_048_576;
  public static int TrainDataSearchIterations = 5;

  public static float TrainTpAssignThreshold = 0.4f;
  public static float TrainFpAssignThreshold = 0.01f;

  //////////////// Detection parameters ////////////////

  public static int DetectMaxConn = 4096;

}
