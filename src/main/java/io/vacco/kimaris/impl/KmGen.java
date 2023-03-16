package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;

import static io.vacco.kimaris.impl.KmTrees.*;
import static io.vacco.kimaris.impl.KmLogging.*;
import static java.lang.Math.exp;
import static java.lang.String.format;

public class KmGen {

  public static void learnNewStage(KmBuffer kc, KmSample smp, KmRand rnd,
                                   float minTpr, float maxFpr, int maxTreesPerStage) {
    int i;
    double[] ws = new double[smp.np + smp.nn];
    double wsum;
    float threshold = 0, tpr, fpr = 1.0f;

    info("* learning a new stage");

    maxTreesPerStage = kc.nTrees + maxTreesPerStage;

    while (kc.nTrees < maxTreesPerStage && fpr > maxFpr) {
      wsum = 0.0;
      for (i = 0; i < smp.np + smp.nn; i++) { // compute weights
        if (kc.tVals[i] > 0) {
          ws[i] = exp(-1.0 * kc.os[i]) / smp.np;
        } else {
          ws[i] = exp( 1.0 * kc.os[i]) / smp.nn;
        }
        wsum += ws[i];
      }
      for(i = 0; i < smp.np + smp.nn; i++) {
        ws[i] /= wsum;
      }

      growRtree(kc, rnd, kc.nTrees, ws, smp.np + smp.nn);

      kc.thresholds[kc.nTrees] = -1337.0f;
      kc.nTrees = kc.nTrees + 1;

      for (i = 0; i < smp.np + smp.nn; i++) { // update outputs
        float o = getTreeOutput(
          kc, kc.nTrees - 1, kc.s[i].bounds,
          kc.s[i].image
        );
        kc.os[i] += o;
      }

      // get threshold
      threshold = KmConfig.StageThreshold;
      int numTps, numFps;

      do {
        threshold -= KmConfig.StageThresholdDelta;
        numTps = 0;
        numFps = 0;
        for (i = 0; i < smp.np + smp.nn; i++) {
          if (kc.tVals[i] > 0 && kc.os[i] > threshold) {
            numTps = numTps + 1;
          }
          if(kc.tVals[i] < 0 && kc.os[i] > threshold) {
            numFps = numFps + 1;
          }
        }
        tpr = numTps / (float) smp.np;
        fpr = numFps / (float) smp.nn;
      } while (tpr < minTpr);

      info(format("  ** tree %d ... stage tpr=%f, stage fpr=%f", kc.nTrees, tpr, fpr));
    }

    kc.thresholds[kc.nTrees - 1] = threshold;

    info(format("  ** threshold set to %f", threshold));
  }

  public static KmBuffer learnCascade(KmBoundBox bb, KmImageList trainData, KmRand rootRnd, KmRegion reg) {

    if (log == null) {
      System.out.println("=============================================================");
      System.out.println("==== No logger output is available for Cascade training. ====");
      System.out.println("=============================================================");
    }

    KmImages.restore(trainData);

    var kc = new KmBuffer()
      .initForDetection(KmConfig.MaxTreeDepth, bb)
      .initForTraining();
    var stageRnd = new KmRand().smwcRand(new long[] {rootRnd.mwcrand()});

    KmSample smp;

    while(true) {
      smp = KmSampling.sampleTrainingData(kc, trainData, stageRnd);
      if(smp.eFpr < KmConfig.FprThreshold) {
        break;
      }
      learnNewStage(kc, smp, rootRnd, KmConfig.StageTpr, KmConfig.StageFpr, KmConfig.MaxTreesPerStage);
    }

    float subsf = KmConfig.TrainFpAssignThreshold;
    var trainRnd = new KmRand().smwcRand(new long[] { rootRnd.mwcrand() });

    for(int i = 0; i < KmConfig.TrainDataSearchIterations; ++i) {
      info("* scanning in progress");
      smp = KmSampling.searchForTrainingData(kc, trainData, trainRnd, reg, subsf);
      info(format("* starting training with np=%d, nn=%d ...", smp.np, smp.nn));
      learnNewStage(
        kc, smp, rootRnd,
        KmConfig.StageTpr * KmConfig.StageTpr,
        KmConfig.StageFpr * KmConfig.StageFpr,
        KmConfig.MaxTreesPerStage
      );
      KmSampling.sampleTrainingData(kc, trainData, stageRnd); // estimating FPR for random sampling
      subsf *= 3;
    }

    info("* learning process finished");

    return kc;
  }

}
