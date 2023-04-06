package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;
import static io.vacco.kimaris.util.KmBytes.*;

public class KmImages {

  /*
   * Get ARGB luminance value in range 0 to 255
   * using sRGB luminance constants.
   */
  public static short intensityAt(BufferedImage i, int x, int y) {
    int argb = i.getRGB(x, y);
    int r = int2(argb) & 0xFF;
    int g = int1(argb) & 0xFF;
    int b = int0(argb) & 0xFF;
    return (short) (r * 0.2126f + g * 0.7152f + b * 0.0722f);
  }

  public static short[] grayPixelsOf(BufferedImage i) {
    int idx, w = i.getWidth(), h = i.getHeight();
    short lum;
    var buff = new short[w * h];
    for (int y = 0; y < h; y++) {
      for (int x = 0; x < w; x++) {
        lum = intensityAt(i, x, y);
        idx = (w * y) + x;
        buff[idx] = lum;
      }
    }
    return buff;
  }

  public static KmImage setMeta(KmImage ki, BufferedImage i, boolean withPixels) {
    return ki
      .withSize(i.getWidth(), i.getHeight())
      .withPixels(withPixels ? grayPixelsOf(i) : new short[] {});
  }

  public static void restore(KmImageList l) {
    var imgIdx = l.stream().collect(toMap(img -> img.imageId, Function.identity()));
    l.stream().flatMap(img -> img.objects.stream()).forEach(obj -> {
      var img = imgIdx.get(obj.imageId);
      obj.withImage(img);
    });
  }

}
