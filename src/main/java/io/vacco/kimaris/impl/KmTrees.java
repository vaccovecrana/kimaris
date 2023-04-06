
package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import io.vacco.kimaris.util.KmMath;

import static io.vacco.kimaris.util.KmBytes.*;
import static io.vacco.kimaris.schema.KmClass.classify;
import static io.vacco.kimaris.impl.KmSampling.*;
import static io.vacco.kimaris.util.KmLogging.*;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.String.format;

public class KmTrees {

  /*
   * A tree code packs 2 (x,y) values which, when combined with
   * a set of bounds, point to specific pixel gray intensity values
   * in an image.
   *
   * These two pixel values produce an intensity comparison result.
   */
  public static boolean binTest(int tCode, KmBounds b, KmImage i) {

    byte p0 = int0(tCode), p1 = int1(tCode), p2 = int2(tCode), p3 = int3(tCode);
    int r1, c1, r2, c2;

    r1 = (256 * b.r + p0 * b.s) / 256;
    c1 = (256 * b.c + p1 * b.s) / 256;
    r2 = (256 * b.r + p2 * b.s) / 256;
    c2 = (256 * b.c + p3 * b.s) / 256;

    r1 = min(max(0, r1), i.height - 1);
    c1 = min(max(0, c1), i.width - 1);
    r2 = min(max(0, r2), i.height - 1);
    c2 = min(max(0, c2), i.width - 1);

    int ix0 = r1 * i.width + c1;
    int ix1 = r2 * i.width + c2;

    short v0 = i.pixels[ix0];
    short v1 = i.pixels[ix1];

    return v0 <= v1;
  }

  public static float getTreeOutput(KmBuffer kc, int i, KmBounds b, KmImage img) {
    int idx = 1, j;
    for(j = 0; j < kc.treeDepth; ++j) {
      var t = binTest(kc.tCodes[i][idx - 1], b, img);
      idx = 2 * idx + (t ? 1 : 0);
    }
    int lx = idx - (1 << kc.treeDepth);
    float out = kc.luts[i][lx];
    if (isTraceEnabled()) {
      trace(format("[%d, %d] (%s), %d -> %.4f", i, idx, b, lx, out));
    }
    return out;
  }

  public static KmClass classifyRegion(KmBuffer kc, KmBounds b, KmImage img) {
    int i;
    float o = 0.0f;
    if (kc.nTrees == 0) {
      return classify(o, 1);
    }
    for (i = 0; i < kc.nTrees; i++) {
      o += getTreeOutput(kc, i, b, img);
      if(o <= kc.thresholds[i]) {
        return classify(o, -1);
      }
    }
    return classify(o, 1);
  }

  public static void growSubtree(KmBuffer kc, KmRand rnd,
                                 int[] tCodes, float[] lut,
                                 int nodeIdx, int depth, int maxDepth,
                                 double[] ws, int[] inds, int inStart, int inAmt) {
    int[] tmpTCodes = new int[2048];
    float[] es = new float[2048];
    float e;

    if (depth == maxDepth) {
      int maxDx = (1 << maxDepth) - 1;
      int lutIdx = nodeIdx - maxDx;
      double tValAcc = 0.0, tDiv, wSum = 0.0;
      for (int i = inStart; i < inStart + inAmt; i++) { // compute output: a simple average
        int ix = inds[i];
        tValAcc += ws[ix] * kc.tVals[ix];
        wSum += ws[ix];
      }
      if(wSum == 0.0) {
        lut[lutIdx] = 0.0f;
      } else {
        tDiv = tValAcc / wSum;
        lut[lutIdx] = (float) tDiv;
      }
      return;
    }
    else if (inAmt <= 1) {
      tCodes[nodeIdx] = 0;
      growSubtree(kc, rnd, tCodes, lut, 2 * nodeIdx + 1, depth + 1, maxDepth, ws, inds, inStart, inAmt);
      growSubtree(kc, rnd, tCodes, lut, 2 * nodeIdx + 2, depth + 1, maxDepth, ws, inds, inStart, inAmt);
      return;
    }

    for(int i = 0; i < KmConfig.NRands; i++) { // generate binary test codes
      tmpTCodes[i] = KmMath.getRandomTCode(kc.boundBox, rnd);
    }

    for(int i = 0; i < KmConfig.NRands; i++) {
      es[i] = getSplitError(kc, tmpTCodes[i], ws, inds, inStart, inAmt);
    }

    e = es[0];
    tCodes[nodeIdx] = tmpTCodes[0];

    for(int i = 1; i < KmConfig.NRands; i++) {
      if(e > es[i]) {
        e = es[i];
        tCodes[nodeIdx] = tmpTCodes[i];
      }
    }

    int n0 = splitTrainingData(kc, tCodes[nodeIdx], inds, inStart, inAmt);

    growSubtree(kc, rnd, tCodes, lut, 2 * nodeIdx + 1, depth + 1, maxDepth, ws, inds, inStart, n0);
    growSubtree(kc, rnd, tCodes, lut, 2 * nodeIdx + 2, depth + 1, maxDepth, ws, inds, inStart + n0, inAmt - n0);
  }

  public static void growRtree(KmBuffer kc, KmRand rnd, int it, double[] ws, int n) {
    int i;
    int[] inds = new int[n];
    for(i = 0; i < n; i++) {
      inds[i] = i;
    }
    growSubtree(
      kc, rnd, kc.tCodes[it], kc.luts[it],
      0, 0, kc.treeDepth, ws, inds, 0, n
    );
  }

}