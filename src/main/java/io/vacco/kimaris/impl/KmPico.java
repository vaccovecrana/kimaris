package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.*;

public class KmPico {

  private static int binTest(int px1, int px2) {
    if (px1 <= px2) { return 1; }
    return 0;
  }

  public static double classifyRegion(int r, int c, int s, int treeDepth, byte[] pixels, int dim, KmFaceCascade pg) {
    double out = 0;
    int root = 0, px1, px2;
    r = r * 256;
    c = c * 256;
    if (pg.treeNum > 0) {
      for (int i = 0; i < pg.treeNum; i++) {
        int idx = 1, x1, x2;
        for (int j = 0; j < pg.treeDepth; j++) {
          x1 = ((r + pg.treeCodes[root + 4 * idx + 0]) * s) >> 8 * dim + ((c + pg.treeCodes[root + 4 * idx + 1]) * s >> 8);
          x2 = ((r + pg.treeCodes[root + 4 * idx + 2]) * s) >> 8 * dim + ((c + pg.treeCodes[root + 4 * idx + 3]) * s >> 8);
          px1 = pixels[x1] & 0xff;
          px2 = pixels[x2] & 0xff;
          idx = 2 * idx + binTest(px1, px2);
        }
        out += pg.treePred[treeDepth * i + idx - treeDepth];
        if (out <= pg.treeThreshold[i]) {
          return -1.0;
        }
        root += 4 * treeDepth;
      }
      return out - pg.treeThreshold[(int) pg.treeNum - 1];
    }
    return 0.0;
  }

  public static double calcIoU(KDetection det1, KDetection det2) {
    double r1 = det1.row, c1 = det1.col, s1 = det1.scale;
    double r2 = det2.row, c2 = det2.col, s2 = det2.scale;
    double overRow = Math.max(0, Math.min(r1 + s1 / 2, r2 + s2 / 2) - Math.max(r1 - s1 / 2, r2 - s2 / 2));
    double overCol = Math.max(0, Math.min(c1 + s1 / 2, c2 + s2 / 2) - Math.max(c1 - s1 / 2, c2 - s2 / 2));
    return overRow * overCol / (s1 * s1 + s2 * s2 - overRow * overCol);
  }

  public static List<KDetection> clusterDetections(List<KDetection> detections, double iouThreshold) {
    detections.sort(Comparator.comparingDouble(d -> d.q)); // TODO must be sort ascending.
    boolean[] assignments = new boolean[detections.size()];
    List<KDetection> clusters = new ArrayList<>();

    for (int i = 0; i < detections.size(); i++) {
      if (!assignments[i]) {
        int r = 0, c = 0, s = 0, n = 0;
        double q = 0;
        for (int j = 0; j < detections.size(); j++) {
          if (calcIoU(detections.get(i), detections.get(j)) > iouThreshold) {
            assignments[j] = true;
            r += detections.get(j).row;
            c += detections.get(j).col;
            s += detections.get(j).scale;
            q += detections.get(j).q;
            n++;
          }
        }
        if (n > 0) {
          clusters.add(KDetection.from(r / n, c / n, s / n, q));
        }
      }
    }
    return clusters;
  }

  public static List<KDetection> runCascade(KmFaceCascade pg, KmCascadeParams cp, KmImageParams ip) {
    List<KDetection> detections = new ArrayList<>(); // TODO need to reduce heap usage.
    byte[] pixels = ip.pixels;
    int treeDepth = (int) Math.pow(2, pg.treeDepth);
    double q;
    int scale = cp.minSize;

    while (scale <= cp.maxSize) {
      double step = Math.max(cp.shiftFactor * scale, 1);
      int offset = (int) ((double) scale / 2 + 1);
      for (int row = offset; row <= ip.rows - offset; row += step) {
        for (int col = offset; col <= ip.cols - offset; col += step) {
          q = classifyRegion(row, col, scale, treeDepth, pixels, ip.dim, pg);
          if (q > 0.0) {
            var det = KDetection.from(row, col, scale, q);
            detections.add(det);
          }
        }
      }
      scale = (int) (scale + Math.max(2, scale * cp.scaleFactor) - scale);
    }
    return detections;
  }

}
