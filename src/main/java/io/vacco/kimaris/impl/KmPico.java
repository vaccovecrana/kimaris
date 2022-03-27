package io.vacco.kimaris.impl;

import io.vacco.kimaris.io.KmIo;
import io.vacco.kimaris.schema.*;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Stream;

public class KmPico {

  public static KmCoord applyPuplocTo(KmFace f, double or, double oc, double os) {
    return KmCoord.from(
        (int) (f.loc.row - or * f.loc.scale),
        (int) (f.loc.col - oc * f.loc.scale),
        (int) (f.loc.scale * os)
    );
  }

  public static KmFace[] detectFaces(KmCascadeSet cs, KmScanParams cp, URL imageUrl) {
    var imgParams = KmIo.loadImage(imageUrl);
    return KmDetFace.clusterDetections(KmDetFace.runCascade(cs.face, cp, imgParams), cp.iouThreshold)
        .stream()
        .filter(det -> det.q > cp.qThreshold)
        .map(det -> {
          var face = new KmFace().withLoc(KmCoord.from(
              det.coord.col - det.coord.scale / 2,
              det.coord.row - det.coord.scale / 2,
              det.coord.scale
          ));

          var leftTest = applyPuplocTo(face, cp.leftEyeOffsetRow, cp.leftEyeOffsetCol, cp.leftEyeOffsetScale);
          var rightTest = applyPuplocTo(face, cp.rightEyeOffsetRow, cp.rightEyeOffsetCol, cp.rightEyeOffsetScale);
          var leftLoc = KmDetPup.runCascade(cp.perturbations, leftTest, imgParams, false, cs.pupil);
          var rightLoc = KmDetPup.runCascade(cp.perturbations, rightTest, imgParams, false, cs.pupil);

          if (leftLoc.valid()) { face.leftEye = leftLoc; }
          if (rightLoc.valid()) { face.rightEye = rightLoc; }
          if (leftLoc.valid() && rightLoc.valid()) {
            face.eyeMarks = Arrays.stream(cs.eyeCascades)
                .flatMap(ec -> Stream.of(
                    KmDetMark.runCascade(leftLoc, rightLoc, imgParams, cp.perturbations, false, ec),
                    KmDetMark.runCascade(leftLoc, rightLoc, imgParams, cp.perturbations, true, ec)
                ))
                .filter(KmCoord::valid)
                .toArray(KmCoord[]::new); // TODO optimize this
            face.mouthMarks = Stream.concat(
                Arrays.stream(cs.mouthCascades).map(mc -> KmDetMark.runCascade(leftLoc, rightLoc, imgParams, cp.perturbations, false, mc)),
                Stream.of(KmDetMark.runCascade(leftLoc, rightLoc, imgParams, cp.perturbations, true, cs.mouthLp84))
            ).filter(KmCoord::valid).toArray(KmCoord[]::new);
          }
          return face;
        }).toArray(KmFace[]::new); // TODO optimize this
  }

}
