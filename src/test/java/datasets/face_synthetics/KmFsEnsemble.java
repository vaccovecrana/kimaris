package datasets.face_synthetics;

import examples.KmCamTest;
import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.KmRegion;

import static io.vacco.kimaris.impl.KmEns.ens;

public class KmFsEnsemble {

  private final KmDet face = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/facefinder-pico")),
    KmRegion.detectDefault().withDetectMax(24)
  );

  private final KmDet eyePup = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/eye-pup")),
    KmRegion.detectDefault()
      .withDetectMax(8)
      .withSizeMin(16)
      .withSizeMax(196)
      .withDetectThreshold(4)
  );

  private final KmDet eyeCornerIn = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/eye-corner-in")),
    KmRegion.detectDefault()
      .withDetectMax(16)
      .withSizeMin(16)
      .withSizeMax(64)
      .withDetectThreshold(3)
  );

  private final KmDet eyeCornerOut = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/eye-corner-out")),
    KmRegion.detectDefault()
      .withDetectMax(16)
      .withSizeMin(16)
      .withSizeMax(64)
      .withDetectThreshold(3)
  );

  private final KmDet mouthCornerOut = new KmDet(
    KmCascades.loadPico(KmCamTest.class.getResource("/mouth-corner-out")),
    KmRegion.detectDefault()
      .withDetectMax(16)
      .withSizeMin(16)
      .withSizeMax(96)
  );

  public final KmEns ens = ens(face).withId("face")
    .then(ens(eyePup, kb -> kb.shift(.4f, .3f).resize(.5f)).withId("r-ey"))
    .then(ens(eyeCornerIn, kb -> kb.shift(.4f, .4f).resize(.2f)).withId("r-ec-in"))
    .then(ens(eyeCornerOut, kb -> kb.shift(.4f, .2f).resize(.2f)).withId("r-ec-ou"))
    .then(ens(eyePup, kb -> kb.shift(.4f, .7f).resize(.5f)).withId("l-ey"))
    .then(ens(eyeCornerIn, kb -> kb.shift(.4f, .6f).resize(.2f)).withId("l-ec-in"))
    .then(ens(eyeCornerOut, kb -> kb.shift(.4f, .8f).resize(.2f)).withId("l-ec-ou"))
    .then(
      ens(mouthCornerOut, kb -> kb.shift(.95f, .3f).resize(.4f)).withId("l-mc-out")
    ).then(
      ens(mouthCornerOut, kb -> kb.shift(.95f, .7f).resize(.4f)).withId("r-mc-out")
    );

}
