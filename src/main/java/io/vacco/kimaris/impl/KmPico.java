package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.util.Arrays;
import java.util.stream.Stream;

public class KmPico {

  public static KmFace[] detectFaces(KmCascadeSet cs, KmScanParams cp, KmImageParams ip) {
    return KmDetFace.clusterDetections(KmDetFace.runCascade(cs.face, cp, ip), cp.iouThreshold)
        .stream()
        .filter(det -> det.q > cp.qThreshold)
        .map(det -> {
          var face = new KmFace().withLoc(KmCoord.from(
              (int) (det.coord.row - det.coord.scale / 2),
              (int) (det.coord.col - det.coord.scale / 2),
              det.coord.scale
          ));
          var leftTest = KmCoord.from(
              det.coord.row - (int) (cp.leftEyeOffsetRow * det.coord.scale),
              det.coord.col - (int) (cp.leftEyeOffsetCol * det.coord.scale),
              det.coord.scale * cp.leftEyeOffsetScale
          );
          var rightTest = KmCoord.from(
              det.coord.row - (int) (cp.rightEyeOffsetRow * det.coord.scale),
              det.coord.col + (int) (cp.rightEyeOffsetCol * det.coord.scale),
              det.coord.scale * cp.rightEyeOffsetScale
          );
          var leftLoc = KmDetPup.runCascade(cp.perturbations, leftTest, ip, false, cs.pupil);
          var rightLoc = KmDetPup.runCascade(cp.perturbations, rightTest, ip, false, cs.pupil);

          if (leftLoc.valid()) { face.leftEye = leftLoc; }
          if (rightLoc.valid()) { face.rightEye = rightLoc; }
          if (leftLoc.valid() && rightLoc.valid()) {
            face.eyeMarks = Arrays.stream(cs.eyeCascades)
                .flatMap(ec -> Stream.of(
                    KmDetMark.runCascade(leftLoc, rightLoc, ip, cp.perturbations, false, ec),
                    KmDetMark.runCascade(leftLoc, rightLoc, ip, cp.perturbations, true, ec)
                ))
                .filter(KmCoord::valid)
                .toArray(KmCoord[]::new); // TODO optimize this
            face.mouthMarks = Stream.concat(
                Arrays.stream(cs.mouthCascades).map(mc -> KmDetMark.runCascade(leftLoc, rightLoc, ip, cp.perturbations, false, mc)),
                Stream.of(KmDetMark.runCascade(leftLoc, rightLoc, ip, cp.perturbations, true, cs.mouthLp84))
            ).filter(KmCoord::valid).toArray(KmCoord[]::new);
          }
          return face;
        }).toArray(KmFace[]::new); // TODO optimize this
  }

}
