package io.vacco.kimaris.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KmImageList extends ArrayList<KmImage> {

  private static final long serialVersionUID = KmConfig.PicoVersion;

  public int sizeMin, sizeMax;

  public KmImageList updateSizeRange() {
    stream()
      .flatMap(img -> img.objects.stream())
      .mapToInt(obj -> obj.bounds.s)
      .min().ifPresent(sm -> this.sizeMin = sm);
    stream()
      .flatMap(img -> img.objects.stream())
      .mapToInt(obj -> obj.bounds.s)
      .max().ifPresent(sm -> this.sizeMax = sm);
    if (sizeMin == 0 || sizeMax == 0) {
      throw new IllegalStateException("Region min size cannot be zero");
    }
    return this;
  }

  public KmImageList withAll(Collection<KmImage> images) {
    this.addAll(images);
    return this;
  }

  public static KmImageList from(List<KmImage> images) {
    var l = new KmImageList();
    l.addAll(images);
    return l;
  }

}
