package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.*;

import static io.vacco.kimaris.impl.KmSampling.findObjects;
import static io.vacco.kimaris.schema.KmBounds.bounds;

public class KmDet {

  private final KmBuffer kb;
  public  final KmRegion kr;

  public KmDet(KmBuffer kb, KmRegion kr) {
    this.kb = Objects.requireNonNull(kb);
    this.kr = Objects.requireNonNull(kr);
  }

  public void ccDfs(KmRegion reg, int i) {
    int j;
    for (j = 0; j < reg.detectCount; j++) {
      float ol = KmMath.getOverlap(
        reg.rcsq[4 * i], reg.rcsq[4 * i + 1], reg.rcsq[4 * i + 2],
        reg.rcsq[4 * j], reg.rcsq[4 * j + 1], reg.rcsq[4 * j + 2]
      );
      if(reg.a[j] == 0 && ol > reg.overlapThreshold) {
        reg.a[j] = reg.a[i];
        ccDfs(reg, j);
      }
    }
  }

  public int findConnectedComponents(KmRegion reg) {
    if (reg.detectCount == 0) {
      return 0;
    }
    int i, cc = 1;
    for (i = 0; i < reg.detectCount; i++) {
      reg.a[i] = 0;
    }
    for (i = 0; i < reg.detectCount; ++i)
      if(reg.a[i] == 0) {
        reg.a[i] = cc;
        ccDfs(reg, i);
        cc = cc + 1;
      }
    return cc - 1; // number of connected components
  }

  public void clusterDetections(KmRegion reg) {
    int idx = 0, ncc, cc;

    ncc = findConnectedComponents(reg);

    if(ncc == 0) {
      reg.clearDetections();
      return;
    }

    for (cc = 1; cc <= ncc; cc++) {
      int i, k;
      float sumqs = 0.0f, sumrs = 0.0f, sumcs = 0.0f, sumss = 0.0f;
      k = 0;

      for (i = 0; i < reg.detectCount; ++i) {
        if (reg.a[i] == cc) {
          sumrs += reg.rcsq[4 * i    ];
          sumcs += reg.rcsq[4 * i + 1];
          sumss += reg.rcsq[4 * i + 2];
          sumqs += reg.rcsq[4 * i + 3];
          k = k + 1;
        }
      }

      reg.rcsq[4 * idx    ] = sumrs / k;
      reg.rcsq[4 * idx + 1] = sumcs / k;
      reg.rcsq[4 * idx + 2] = sumss / k;
      reg.rcsq[4 * idx + 3] = sumqs; // accumulated confidence measure

      idx++;
    }

    reg.clearDetections();

    for (int i = 0; i < idx; i++) {
      if (reg.detections[i] == null) {
        reg.detections[i] = bounds(0, 0, 0);
      }
      if (reg.rcsq[4 * i + 3] >= reg.detectThreshold) {
        reg.detections[i].with(
          (int) reg.rcsq[4 * i    ],
          (int) reg.rcsq[4 * i + 1],
          (int) reg.rcsq[4 * i + 2]
        );
        reg.detectCount = reg.detectCount + 1;
      } else {
        reg.detections[i].invalidate();
      }
    }

    Arrays.fill(reg.rcsq, 0);
  }

  public void processImage(KmImage img, KmBounds sub) {
    if (sub == null) { // full image scan
      kr.fitTo(img);
      findObjects(kb, img, kr, 0, 0, img.height, img.width, true);
    } else { // sub-region scan
      int y0 = sub.r - (sub.s / 2);
      int x0 = sub.c - (sub.s / 2);
      findObjects(kb, img, kr, y0, x0, y0 + sub.s, x0 + sub.s, true);
    }
    clusterDetections(kr);
  }

}
