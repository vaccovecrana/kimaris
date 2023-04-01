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
      .filter(i -> i >= 8)
      .min().ifPresent(sm -> this.sizeMin = sm);
    stream()
      .flatMap(img -> img.objects.stream())
      .mapToInt(obj -> obj.bounds.s)
      .max().ifPresent(sm -> this.sizeMax = sm);
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
