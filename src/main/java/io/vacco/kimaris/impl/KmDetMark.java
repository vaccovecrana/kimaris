package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;

public class KmDetMark {

  public static KmCoord runCascade(KmCoord leftEye, KmCoord rightEye,
                                   KmImageParams img, int perturb, boolean flipV,
                                   KmCascade plc) {
    int dx = (leftEye.row - rightEye.row) * (leftEye.row - rightEye.row);
    int dy = (leftEye.col - rightEye.col) * (leftEye.col - rightEye.col);
    double dist = Math.sqrt(dx + dy);

    double row = leftEye.row + rightEye.row / 2.0 + 0.25 * dist;
    double col = leftEye.col + rightEye.col / 2.0 + 0.15 * dist;
    double scale = 3.0 * dist;

    var coord = KmCoord.from((int) row, (int) col, (int) scale);

    return KmDetPup.runCascade(perturb, coord, img, flipV, plc);
  }

}
