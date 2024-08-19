package datasets.face_synthetics;

import examples.KmCamTest;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.util.KmAvg3F;
import io.vacco.kimaris.schema.*;

import static io.vacco.kimaris.impl.KmEns.ens;

public class KmFsEnsemble {

  private final KmDet face = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/facefinder-pico")),
    KmRegion.detectDefault().withDetectMax(24)
  );

  private final KmDet eyePup = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.EyePup.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(128)
      .withDetectThreshold(4)
  );

  private final KmDet eyeCornerIn = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.EyeCornerIn.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(16)
      .withSizeMin(16)
      .withSizeMax(96)
      .withDetectThreshold(3)
  );

  private final KmDet eyeCornerOut = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.EyeCornerOut.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(16)
      .withSizeMin(16)
      .withSizeMax(96)
      .withDetectThreshold(3)
  );

  private final KmDet mouthCornerOut = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.MouthCornerOut.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  private final KmDet mouthLipUp = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.MouthLipUp.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  private final KmDet mouthLipLow = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.MouthLipLow.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  private final KmDet eyebrowCorner = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.EyebrowCorner.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  private final KmDet eyebrowCenter = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource(KmIBugMark.EyebrowCenter.getClassPath())),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  private final KmAvg3F faceAvg = new KmAvg3F().init(24);

  private KmBounds applySg(KmBounds b, KmAvg3F avg) {
    avg.update(b);
    avg.copyTo(b);
    return b;
  }

  public final KmEns ens = ens(face, b -> applySg(b, faceAvg)).withId("face")
    .then(ens(eyebrowCorner, kb -> kb.shift(.25f, .45f).resize(.4f)).withId("r-ebr-cor-in"))
    .then(ens(eyebrowCenter, kb -> kb.shift(.25f, .35f).resize(.4f)).withId("r-ebr-cnt"))
    .then(ens(eyebrowCorner, kb -> kb.shift(.25f, .25f).resize(.4f)).withId("r-ebr-cor-out"))
    .then(ens(eyePup, kb -> kb.shift(.4f, .3f).resize(.5f)).withId("r-ey"))
    .then(ens(eyeCornerIn, kb -> kb.shift(.4f, .4f).resize(.2f)).withId("r-ec-in"))
    .then(ens(eyeCornerOut, kb -> kb.shift(.4f, .2f).resize(.2f)).withId("r-ec-ou"))

    .then(ens(eyebrowCorner, kb -> kb.shift(.25f, .55f).resize(.4f)).withId("l-ebr-cor-in"))
    .then(ens(eyebrowCenter, kb -> kb.shift(.25f, .65f).resize(.4f)).withId("l-ebr-cnt"))
    .then(ens(eyebrowCorner, kb -> kb.shift(.25f, .75f).resize(.4f)).withId("l-ebr-cor-out"))
    .then(ens(eyePup, kb -> kb.shift(.4f, .7f).resize(.5f)).withId("l-ey"))
    .then(ens(eyeCornerIn, kb -> kb.shift(.4f, .6f).resize(.2f)).withId("l-ec-in"))
    .then(ens(eyeCornerOut, kb -> kb.shift(.4f, .8f).resize(.2f)).withId("l-ec-ou"))

    .then(ens(mouthCornerOut, kb -> kb.shift(.85f, .3f).resize(.4f)).withId("r-mc-out"))
    .then(ens(mouthLipUp, kb -> kb.shift(.75f, .3f).resize(.4f)).withId("r-ml-up"))
    .then(ens(mouthLipLow, kb -> kb.shift(.95f, .3f).resize(.6f)).withId("r-ml-low"))

    .then(ens(mouthCornerOut, kb -> kb.shift(.85f, .7f).resize(.4f)).withId("l-mc-out"))
    .then(ens(mouthLipUp, kb -> kb.shift(.75f, .7f).resize(.4f)).withId("l-ml-up"))
    .then(ens(mouthLipLow, kb -> kb.shift(.95f, .7f).resize(.6f)).withId("l-ml-low"))
    ;

}
