package io.vacco.kimaris.schema;

import io.vacco.kimaris.impl.KmDet;
import java.util.function.Function;
import java.util.*;

import static io.vacco.kimaris.schema.KmBounds.bounds;

public class KmEns {

  public KmDet det;
  public Function<KmBounds, KmBounds> bFn;
  public String id;

  private final List<KmEns> children = new ArrayList<>();

  private void runTail(KmImage img, Map<String, KmBounds> detections) {
    if (det.kr.detectCount > 0) {
      for (var det : det.kr.detections) {
        if (det != null && det.isValid()) {
          detections.computeIfAbsent(id, k -> bounds()).copyFrom(det).withTag(id);
          for (var en : children) {
            en.det.processImage(img, en.bFn.apply(bounds().copyFrom(det)));
            en.runTail(img, detections);
          }
        }
      }
    }
  }

  public void run(KmImage img, Map<String, KmBounds> detections) {
    det.processImage(img, null);
    detections.clear();
    runTail(img, detections);
  }

  public KmEns then(KmEns next) {
    children.add(Objects.requireNonNull(next));
    return this;
  }

  public KmEns withId(String id) {
    this.id = Objects.requireNonNull(id);
    return this;
  }

  public static KmEns ens(KmDet det, Function<KmBounds, KmBounds> bFn) {
    var dn = new KmEns();
    dn.det = Objects.requireNonNull(det);
    dn.bFn = Objects.requireNonNull(bFn);
    return dn;
  }

  public static KmEns ens(KmDet root) {
    return ens(root, Function.identity());
  }

}
