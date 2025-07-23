package io.vacco.kimaris;

import io.vacco.jtinn.JtTrain;

import java.io.File;
import java.util.*;

public class MbLbpSampler implements JtTrain.JtSampler {

  private final List<File> faceFiles; // Positive samples (faces)
  private final List<File> nonFaceFiles; // Negative samples
  private final int blockRows, blockCols; // MB-LBP block size (e.g., 3, 3)
  private final KmSchema.KmImageParams ip = new KmSchema.KmImageParams(); // Reusable buffer
  private final Random rnd = new Random();

  public MbLbpSampler(String faceDir, String nonFaceDir, int blockRows, int blockCols) {
    this.faceFiles = Arrays.asList(new File(faceDir).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png")));
    this.nonFaceFiles = Arrays.asList(new File(nonFaceDir).listFiles((dir, name) -> name.endsWith(".jpg") || name.endsWith(".png")));
    this.blockRows = blockRows;
    this.blockCols = blockCols;
    if (faceFiles.isEmpty() || nonFaceFiles.isEmpty()) {
      throw new IllegalArgumentException("Empty dataset directories");
    }
  }

  private float[] normalizeHistogram(short[] hist) {
    float[] features = new float[hist.length];
    int total = 0;
    for (short val : hist) { total += val; }
    float denom = total > 0 ? (float) total : 1.0f; // Avoid div-by-zero
    for (int i = 0; i < hist.length; i++) {
      features[i] = hist[i] / denom; // Probability distribution
    }
    return features;
  }

  private JtTrain.JtSample extractSample(File imgFile, float label) {
    try {
      KmImage.grayPixelsOf(imgFile.toURI().toURL(), ip); // Load grayscale
      short[] hist = KmMbLbp.mbLbpHistogramOf(ip, blockRows, blockCols);
      float[] features = normalizeHistogram(hist);
      return JtTrain.JtSample.of(features, new float[]{label});
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public JtTrain.JtSample[] get() {
    // Sample equal number of positives/negatives for balance; adjust as needed
    int batchSize = Math.min(32, Math.min(faceFiles.size(), nonFaceFiles.size())); // Mini-batch size
    List<JtTrain.JtSample> samples = new ArrayList<>(batchSize * 2);

    Collections.shuffle(faceFiles, rnd);
    Collections.shuffle(nonFaceFiles, rnd);

    for (int i = 0; i < batchSize; i++) {
      samples.add(extractSample(faceFiles.get(i), 1.0f)); // Face
      samples.add(extractSample(nonFaceFiles.get(i), 0.0f)); // Non-face
    }

    return samples.toArray(new JtTrain.JtSample[0]);
  }
}