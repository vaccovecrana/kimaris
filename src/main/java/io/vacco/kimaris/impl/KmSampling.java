package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.concurrent.*;

import static io.vacco.kimaris.impl.KmMath.*;
import static io.vacco.kimaris.impl.KmTrees.*;
import static io.vacco.kimaris.impl.KmLogging.*;
import static io.vacco.kimaris.schema.KmBounds.bounds;
import static java.util.stream.Collectors.toList;
import static java.lang.Math.max;
import static java.lang.String.format;

public class KmSampling {

  /*
   TODO
     Code below assumes that the random number generator passed in
     has been seeded by a parent RNG:
     var rnd = new KmRand().smwcRand(
       new long[] {0xFFFFL * kr.mwcrand() + 0xFFFF1234FFFF0001L * kr.mwcrand()}
     );
     This should help parallelize sampling later, if needed.
   */

  public static KmSample sampleTrainingData(KmBuffer kc, KmImageList images, KmRand rnd) {
    int n = 0;
    var smp = new KmSample();
    var objects = images.stream().flatMap(img -> img.objects.stream()).collect(toList());

    // object samples
    for (int i = 0; i < images.size(); i++) {
      var img = images.get(i);
      for (var obj : img.objects) {
        var cl = classifyRegion(kc, obj.bounds, img);
        if (cl.label == 1) {
          kc.iinds[n] = i;
          kc.tVals[n] = 1;
          kc.os[n] = cl.o;
          kc.s[n] = obj;
          smp.np = smp.np + 1;
          n = n + 1;
        } // else, no region match. TODO should this be logged?
      }
    }

    // non-object samples
    long nw = 0;
    boolean stop = false;

    while (!stop) {
      var ir = rnd.mwcrand();
      var rr = rnd.mwcrand();
      var cr = rnd.mwcrand();
      var sr = rnd.mwcrand();

      var ix = ir % images.size();
      var sx = sr % objects.size();

      var img = images.get((int) ix);
      var r = rr % img.height;
      var c = cr % img.width;
      var s = objects.get((int) sx).bounds.s;

      var bd = bounds(r, c, s);
      var cl = classifyRegion(kc, bd, img);

      if(cl.label == 1) {
        // check if the region intersects with a true positive
        // this could probably be done more efficiently, but we
        // do not expect a large number of objects per image.
        var ok = true;
        for (var obj : img.objects) {
          if (KmMath.getOverlap(bd, obj.bounds) > 0.5) {
            ok = false;
          }
        }

        if (ok) { // we have a false positive ...
          if (smp.nn < smp.np) {
            kc.s[n] = new KmObj().withBounds(bd).withImage(img);
            kc.iinds[n] = (int) ix;
            kc.tVals[n] = -1;
            kc.os[n] = cl.o;
            smp.nn = smp.nn + 1;
            n++;
          } else {
            stop = true;
          }
        }
      }

      if (!stop) {
        nw++;
      }
    }

    smp.eTpr = smp.np / (float) objects.size();
    smp.eFpr = (float) (smp.nn / (double) nw);

    info("\n* sampling finished");
    info(format("  ** cascade TPR=%.8f", smp.eTpr));
    info(format("  ** cascade FPR=%.8f (%d/%d)", smp.eFpr, smp.nn, nw));

    return smp;
  }

  public static int splitTrainingData(KmBuffer kc, int tCode, int[] inds, int inStart, int inAmt) {
    var stop = false;
    int i = inStart, j = inStart + inAmt - 1, swp;

    while (!stop) {
      while (!binTest(tCode, kc.s[inds[i]].bounds, kc.s[inds[i]].image)) {
        if (i == j) {
          break;
        } else {
          i++;
        }
      }
      while(binTest(tCode, kc.s[inds[j]].bounds, kc.s[inds[j]].image)) {
        if(i == j) {
          break;
        } else {
          --j;
        }
      }
      if(i == j) {
        stop = true;
      } else {
        swp = inds[i]; // swap
        inds[i] = inds[j];
        inds[j] = swp;
      }
    }

    int n0 = 0;
    for(i = inStart; i < inStart + inAmt; i++) {
      if(!binTest(tCode, kc.s[inds[i]].bounds, kc.s[inds[i]].image)) {
        n0++;
      }
    }

    return n0;
  }

  public static float getSplitError(KmBuffer kc, int tCode, double[] ws, int[] inds, int inStart, int inAmt) {
    double wSum, wSum0, wSum1;
    double wtValSum0, wtValSumSqr0, wtValSum1, wtValSumSqr1;
    double wmse0, wmse1;

    wSum = wSum0 = wSum1 = wtValSum0 = wtValSum1 = wtValSumSqr0 = wtValSumSqr1 = 0.0;

    for (int i = inStart; i < inStart + inAmt; i++) {
      int ix = inds[i];
      if (binTest(tCode, kc.s[ix].bounds, kc.s[ix].image)) {
        wSum1 += ws[ix];
        wtValSum1 += ws[ix] * kc.tVals[ix];
        wtValSumSqr1 += ws[ix] * sqr(kc.tVals[ix]);
      } else {
        wSum0 += ws[ix];
        wtValSum0 += ws[ix] * kc.tVals[ix];
        wtValSumSqr0 += ws[ix] * sqr(kc.tVals[ix]);
      }
      wSum += ws[ix];
    }

    wmse0 = wtValSumSqr0 - sqr(wtValSum0) / wSum0;
    wmse1 = wtValSumSqr1 - sqr(wtValSum1) / wSum1;

    double out = (wmse0 + wmse1) / wSum;

    return (float) out;
  }

  public static void findObjects(KmBuffer kc, KmImage ki, KmRegion kr, KmBounds kb,
                                 int y0, int x0, int y1, int x1, float s) {
    float r, c, dr, dc;
    dr = dc = max(kr.stride * s, 1.0f);
    for (r = y0 + (s / 2 + 1); r <= y1 - s / 2 - 1; r += dr) {
      for (c = x0 + (s / 2 + 1); c <= x1 - s / 2 - 1; c += dc) {
        var cl = classifyRegion(kc, kb.withD(r, c, s), ki);
        if (cl.label == 1) {
          if (kr.detectCount < kr.detectMax) {
            kr.rcsq[4 * kr.detectCount] = r;
            kr.rcsq[4 * kr.detectCount + 1] = c;
            kr.rcsq[4 * kr.detectCount + 2] = s;
            kr.rcsq[4 * kr.detectCount + 3] = cl.o;
            kr.detectCount = kr.detectCount + 1;
          }
        }
      }
    }
  }

  public static void findObjects(KmBuffer kc, KmImage ki, KmRegion kr,
                                 int y0, int x0, int y1, int x1, boolean thread) {
    var s = kr.sizeMin;
    var bnd = bounds(0, 0, 0);
    if (thread) {
      int tasks = 0;
      float s0 = s, s1 = s;
      while (s0 <= kr.sizeMax) {
        tasks = tasks + 1;
        s0 = kr.scale * s0;
      }
      var cl = new CountDownLatch(tasks);
      while (s1 <= kr.sizeMax) {
        var fs1 = s1;
        ForkJoinPool.commonPool().submit(() -> {
          findObjects(kc, ki, kr, bounds(0, 0, 0), y0, x0, y1, x1, fs1);
          cl.countDown();
        });
        s1 = kr.scale * s1;
      }
      try {
        cl.await();
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    } else {
      while (s <= kr.sizeMax) {
        findObjects(kc, ki, kr, bnd, y0, x0, y1, x1, s);
        s = kr.scale * s;
      }
    }
  }

  public static KmSample searchForTrainingData(KmBuffer kc, KmImageList images, KmRand smpRnd, KmRegion kr, float subsf, boolean thread) {
    int n = 0;
    var smp = new KmSample();

    for (int i = 0; i < images.size(); i++) {
      var img = images.get(i);

      kr.clearDetections();
      findObjects(kc, img, kr, 0, 0, img.height, img.width, thread);

      if (isDebugEnabled()) {
        debug(format("%s -> %d %d %d", img.imageId, kr.detectCount, smp.np, smp.nn));
      }

      for (int j = 0; j < kr.detectCount; j++) {
        boolean assigned = false;
        for (var obj : img.objects) {
          float overlap = getOverlap(
            kr.rcsq[4 * j], kr.rcsq[4 * j + 1], kr.rcsq[4 * j + 2],
            obj.bounds.r, obj.bounds.c, obj.bounds.s
          );
          if (overlap > kr.overlapThreshold && smp.np < kc.nObjs / 2 && n < kc.nObjs) { // true positive
            kc.s[n] = new KmObj()
              .withImage(img)
              .withBounds(bounds(
                kr.rcsq[4 * j], kr.rcsq[4 * j + 1], kr.rcsq[4 * j + 2]
              ));
            kc.os[n] = kr.rcsq[4*j+3];
            kc.iinds[n] = i;
            kc.tVals[n] = 1;
            n = n + 1;
            smp.np = smp.np + 1;
          }
          if (overlap > KmConfig.TrainTpAssignThreshold) {
            assigned = true;
          }
        }
        if (!assigned) {
          var r0 = smpRnd.mwcrand();
          r0 = r0 % 1000;
          var t0 = (float) r0 / 999.0f;
          var t1 = smp.nn < 3 * (kc.nObjs + 1) / 4;
          if (t0 < subsf && t1 && smp.nn < smp.np && n < kc.nObjs) { // false positive
            kc.s[n] = new KmObj()
              .withImage(img)
              .withBounds(bounds(
                kr.rcsq[4 * j], kr.rcsq[4 * j + 1], kr.rcsq[4 * j + 2]
              ));
            kc.os[n] = kr.rcsq[4*j+3];
            kc.iinds[n] = i;
            kc.tVals[n] = -1;
            n = n + 1;
            smp.nn = smp.nn + 1;
          }
        }
      }
    }

    return smp;
  }

}
