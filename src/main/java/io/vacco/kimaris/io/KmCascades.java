package io.vacco.kimaris.io;

import io.vacco.kimaris.impl.KmPico;
import io.vacco.kimaris.schema.*;
import java.net.URL;
import java.nio.*;
import java.nio.file.Paths;
import java.util.ArrayList;

import static io.vacco.kimaris.io.KmArrays.*;

public class KmCascades {

  public static KmCascade unpackCascade(URL url) {
    var bytes = bytesFrom(url);
    var bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    var pup = new KmCascade();

    pup.id = Paths.get(url.getFile()).getFileName().toString();
    pup.stages = bb.getInt();
    pup.scales = bb.getFloat();
    pup.trees = bb.getInt();
    pup.treeDepth = bb.getInt();

    var tCodes = new ArrayList<byte[]>();
    var tPreds = new ArrayList<Float>();
    var pos = 16;

    for (int s = 0; s < pup.stages; s++) {
      for (int t = 0; t < pup.trees; t++) {
        int depth = (int) Math.pow(2, pup.treeDepth);
        int idx1 = pos + 4 * depth - 4;
        byte[] dCode = new byte[idx1 - pos];

        bb.get(dCode, 0, dCode.length);
        tCodes.add(dCode);
        pos = idx1;

        for (int i = 0; i < depth; i++) {
          for (int l = 0; l < 2; l++) {
            tPreds.add(bb.getFloat());
          }
        }
      }
    }

    pup.treeCodes = toArrayB(tCodes);
    pup.treePreds = toArrayF(tPreds);

    return pup;
  }

  public static KmFaceCascade unpackFaceCascade(URL url) {
    var bytes = bytesFrom(url);
    var bb = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
    var fc = new KmFaceCascade();

    bb.position(8);

    fc.treeDepth = bb.getInt();
    fc.treeNum = bb.getInt();
    fc.treeThreshold = new float[(int) fc.treeNum];

    var tCodes = new ArrayList<byte[]>();
    var tPreds = new ArrayList<Float>();
    var pos = 16;

    for (int t = 0; t < fc.treeNum; t++) {
      tCodes.add(new byte[] {0, 0, 0, 0});

      int idx1 = pos + (int) ((4 * Math.pow(2, fc.treeDepth)) - 4);
      byte[] dCode = new byte[idx1 - pos];

      bb.get(dCode, 0, dCode.length);
      tCodes.add(dCode);
      pos = idx1;

      for (int i = 0; i < Math.pow(2, fc.treeDepth); i++) {
        tPreds.add(bb.getFloat());
        pos = pos + 4;
      }

      fc.treeThreshold[t] = bb.getFloat();
      pos = pos + 4;
    }

    fc.treeCodes = toArrayB(tCodes);
    fc.treePred = toArrayF(tPreds);

    return fc;
  }

  public static KmCascadeSet loadCascades() {
    var cs = new KmCascadeSet();

    cs.face = KmCascades.unpackFaceCascade(KmPico.class.getResource("/io/vacco/kimaris/facefinder"));
    cs.pupil = KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/puploc"));

    cs.eyeCascades = new KmCascade[] {
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp46")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp44")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp42")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp38")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp312"))
    };
    cs.mouthCascades = new KmCascade[] {
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp93")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp84")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp82")),
        KmCascades.unpackCascade(KmPico.class.getResource("/io/vacco/kimaris/lp81"))
    };
    cs.mouthLp84 = cs.mouthCascades[1];

    return cs;
  }

}
