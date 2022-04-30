package io.vacco.kimaris.io;

import io.vacco.kimaris.schema.KmImageParams;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.net.URL;

public class KmImage {

  public static KmImageParams grayPixelsOf(BufferedImage img, KmImageParams ip) {
    ip = ip == null ? new KmImageParams() : ip;
    ip.cols = img.getWidth();
    ip.rows = img.getHeight();
    ip.grayMat = ip.grayMat == null ? new short[ip.rows][ip.cols] : ip.grayMat;

    int r, g, b;
    short gV;
    for (int y = 0; y < ip.rows; y++) {
      for (int x = 0; x < ip.cols; x++) {
        int p = img.getRGB(x, y); // TODO is there a way to extract raw values when the image type is grayscale?
        r = p >> 16 & 0xFF;
        g = p >> 8 & 0xFF;
        b = p & 0xFF;
        gV = (short) ((byte) ((r + g + b) / 3) & 0xff);
        ip.grayMat[y][x] = gV;
      }
    }
    return ip;
  }

  public static KmImageParams grayPixelsOf(URL imgUrl, KmImageParams ip) {
    try {
      var img = ImageIO.read(imgUrl);
      return grayPixelsOf(img, ip);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static KmImageParams grayPixelsOf(byte[] raw, KmImageParams ip) {
    try (var is = new ByteArrayInputStream(raw)) {
      var img = ImageIO.read(is);
      return grayPixelsOf(img, ip);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

}
