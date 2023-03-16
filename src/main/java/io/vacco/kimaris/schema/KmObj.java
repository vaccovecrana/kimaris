package io.vacco.kimaris.schema;

import java.util.*;

public class KmObj {

  public transient KmImage image;

  public String imageId;
  public List<KmPoint> points = new ArrayList<>();
  public KmBounds bounds;

  public KmObj add(KmPoint p) {
    this.points.add(p);
    return this;
  }

  public KmObj withBounds(KmBounds b) {
    this.bounds = Objects.requireNonNull(b);
    return this;
  }

  public KmObj withImage(KmImage img) {
    this.imageId = Objects.requireNonNull(img.imageId);
    this.image = img;
    return this;
  }

  public String id() {
    return String.format("%s:%s",
      imageId == null ? "?" : imageId,
      bounds == null ? "?" : bounds.id()
    );
  }

  @Override public String toString() {
    return String.format("[%s (%dpt) %s]", id(), points.size(), bounds);
  }
}
