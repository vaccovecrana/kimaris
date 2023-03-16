package io.vacco.kimaris.schema;

import io.vacco.kimaris.impl.KmImages;
import java.awt.image.BufferedImage;
import java.util.*;

/**
 * @see KmImages#grayPixelsOf(BufferedImage)
 */
public class KmImage {

  public String imageId;
  public String imagePath;
  public short[] pixels;
  public int width, height;

  public List<KmObj> objects = new ArrayList<>();

  public KmImage withImagePath(String imagePath) {
    this.imagePath = Objects.requireNonNull(imagePath);
    this.imageId = String.format("%x", imagePath.hashCode());
    return this;
  }

  public KmImage withPixels(short[] pixels) {
    this.pixels = Objects.requireNonNull(pixels);
    return this;
  }

  public KmImage withSize(int width, int height) {
    this.width = width;
    this.height = height;
    return this;
  }

  public KmImage add(KmObj obj) {
    this.objects.add(obj.withImage(this));
    return this;
  }

  @Override public String toString() {
    return String.format("[%s, %s]", imageId, imagePath);
  }
}
