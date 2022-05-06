package io.vacco.kimaris.impl;

import io.vacco.kimaris.schema.KmImageParams;
import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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

  public static void writePng(int w, int h, short[] pixels, File out) {
    var data = new byte[w * h];
    for (int i = 0; i < data.length; i++) {
      data[i] = (byte) pixels[i];
    }
    try {
      var image = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
      image.getRaster().setDataElements(0, 0, w, h, data);
      ImageIO.write(image, "png", out);
    } catch (IOException ex) {
      throw new IllegalStateException(ex.getMessage(), ex);
    }
  }
}
