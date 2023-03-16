package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.io.*;
import java.net.URL;
import java.nio.*;
import java.util.Arrays;

import static java.lang.System.arraycopy;
import static io.vacco.kimaris.schema.KmConfig.*;
import static io.vacco.kimaris.schema.KmBoundBox.bBox;

public class KmCascades {

  private static byte[] getOccupiedArray(ByteBuffer bb) {
    int position = bb.position();
    return Arrays.copyOfRange(bb.array(), 0, position);
  }

  public static void savePico(OutputStream out, KmBuffer kc) {
    try {
      var capacity = (Kb0 * Kb1 * 4) + (Kb0 * Kb1 * 4) + (Kb0 * 4) + (8 * 4);
      var bb = ByteBuffer.allocate(capacity).order(ByteOrder.LITTLE_ENDIAN);

      bb.putInt((int) KmConfig.PicoVersion);
      bb.put(new byte[] {
        kc.boundBox.rMin, kc.boundBox.rMax,
        kc.boundBox.cMin, kc.boundBox.cMax
      });
      bb.putInt(kc.treeDepth);
      bb.putInt(kc.nTrees);

      for (int i = 0; i < kc.nTrees; i++) {
        int tcLen = (1 << kc.treeDepth) - 1;
        for (int k = 0; k < tcLen; k++) {
          bb.putInt(kc.tCodes[i][k]);
        }
        int lutLen = 1 << kc.treeDepth;
        for (int k = 0; k < lutLen; k++) {
          bb.putFloat(kc.luts[i][k]);
        }
        bb.putFloat(kc.thresholds[i]);
      }

      var bytes = getOccupiedArray(bb);
      try (out) {
        out.write(bytes);
      }
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static KmBuffer loadPico(InputStream in) {
    try {
      var bb = ByteBuffer.wrap(in.readAllBytes()).order(ByteOrder.LITTLE_ENDIAN);
      var kc = new KmBuffer();
      var ver = bb.getInt();

      if (ver != KmConfig.PicoVersion) {
        throw new IllegalArgumentException(String.format(
          "Pico version mismatch: [%d]", ver
        ));
      }

      var bounds = new byte[4];
      bb.get(bounds);
      var tDepth = bb.getInt();
      var nTrees = bb.getInt();

      kc.initForDetection(tDepth, bBox(bounds[0], bounds[1], bounds[2], bounds[3]));
      kc.nTrees = nTrees;

      for (int i = 0; i < nTrees; i++) {
        int tcLen = (1 << kc.treeDepth) - 1;
        for (int k = 0; k < tcLen; k++) {
          kc.tCodes[i][k] = bb.getInt();
        }
        int lutLen = 1 << kc.treeDepth;
        for (int k = 0; k < lutLen; k++) {
          kc.luts[i][k] = bb.getFloat();
        }
        kc.thresholds[i] = bb.getFloat();
      }

      return kc;
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static KmBuffer loadPico(URL url) {
    try {
      return loadPico(url.openStream());
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static KmBufferDto loadFrom(KmBuffer kc) {
    var kb = new KmBufferDto();

    kb.version = KmConfig.PicoVersion;
    kb.boundBox = kc.boundBox;
    kb.treeDepth = kc.treeDepth;
    kb.nTrees = kc.nTrees;
    kb.nObjs = kc.nObjs;

    int tcLen = (1 << kc.treeDepth) - 1;
    int lutLen = 1 << kc.treeDepth;

    kb.tCodes = new int[kc.nTrees][tcLen];
    kb.luts = new float[kc.nTrees][lutLen];

    for (int i = 0; i < kc.nTrees; i++) {
      arraycopy(kc.tCodes[i], 0, kb.tCodes[i], 0, tcLen);
      arraycopy(kc.luts[i], 0, kb.luts[i], 0, lutLen);
    }

    kb.thresholds = new float[kc.nTrees];
    arraycopy(kc.thresholds, 0, kb.thresholds, 0, kc.nTrees);

    return kb;
  }

  public static KmBuffer loadFrom(KmBufferDto kb) {
    var kc = new KmBuffer().initForDetection(kb.treeDepth, kb.boundBox);
    kc.treeDepth = kb.treeDepth;
    kc.nTrees = kb.nTrees;

    for (int i = 0; i < kb.tCodes.length; i++) {
      arraycopy(kb.tCodes[i], 0, kc.tCodes[i], 0, kb.tCodes[i].length);
    }
    for (int i = 0; i < kb.luts.length; i++) {
      arraycopy(kb.luts[i], 0, kc.luts[i], 0, kb.luts[i].length);
    }
    arraycopy(kb.thresholds, 0, kc.thresholds, 0, kb.thresholds.length);

    return kc;
  }

}
