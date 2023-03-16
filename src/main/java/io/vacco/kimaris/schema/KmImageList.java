package io.vacco.kimaris.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KmImageList extends ArrayList<KmImage> {

  private static final long serialVersionUID = KmConfig.PicoVersion;

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
