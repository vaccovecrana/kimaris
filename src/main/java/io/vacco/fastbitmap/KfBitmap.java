package io.vacco.fastbitmap;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Class to handle image.
 *
 * @author Diego Catalano
 */
public class KfBitmap {

  public BufferedImage bufferedImage;
  private WritableRaster raster;
  private int[] pixels;
  private byte[] pixelsGRAY;
  private KfCoordinateSystem cSystem = KfCoordinateSystem.Matrix;
  private int strideX, strideY;
  private int size;

  public KfBitmap(KfBitmap fastBitmap) {
    this.bufferedImage = fastBitmap.toBufferedImage();
    if (getType() == BufferedImage.TYPE_3BYTE_BGR) {
      toRGB();
    }
    setCoordinateSystem(fastBitmap.getCoordinateSystem());
    refresh();
  }

  public KfBitmap(BufferedImage bufferedImage) {
    this.bufferedImage = bufferedImage;
    prepare();
    refresh();
  }

  public KfBitmap(Image image) {
    bufferedImage = (BufferedImage) image;
    prepare();
    refresh();
  }

  public KfBitmap(ImageIcon ico) {
    bufferedImage = (BufferedImage) ico.getImage();
    prepare();
    refresh();
  }

  public KfBitmap(URL url) {
    try {
      this.bufferedImage = ImageIO.read(url);
      prepare();
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public KfBitmap(File f) {
    try {
      this.bufferedImage = ImageIO.read(f);
      prepare();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public KfBitmap(byte[] raw) {
    try {
      InputStream is = new ByteArrayInputStream(raw);
      this.bufferedImage = ImageIO.read(is);
      prepare();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public KfBitmap(int width, int height) {
    this.bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    this.setCoordinateSystem(KfCoordinateSystem.Matrix);
    refresh();
  }

  public KfBitmap(int width, int height, KfColorSpace colorSpace) {
    if (colorSpace == KfColorSpace.RGB) {
      bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    } else if (colorSpace == KfColorSpace.Grayscale) {
      bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
    } else if (colorSpace == KfColorSpace.ARGB) {
      bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    }
    this.setCoordinateSystem(KfCoordinateSystem.Matrix);
    refresh();
  }

  private void prepare() {
    if (getType() == BufferedImage.TYPE_BYTE_GRAY) {
      refresh();
    } else if (getType() == BufferedImage.TYPE_INT_ARGB || getType() == BufferedImage.TYPE_4BYTE_ABGR) {
      toARGB();
    } else {
      toRGB();
    }
    setCoordinateSystem(KfCoordinateSystem.Matrix);
  }

  private void refresh() {
    this.raster = getRaster();
    if (isGrayscale()) {
      pixelsGRAY = ((DataBufferByte) raster.getDataBuffer()).getData();
      this.size = pixelsGRAY.length;
    }
    if (isRGB() || isARGB()) {
      pixels = ((DataBufferInt) raster.getDataBuffer()).getData();
      this.size = pixels.length;
    }
  }

  public KfColorSpace getColorSpace() {
    if (getType() == BufferedImage.TYPE_BYTE_GRAY) {
      return KfColorSpace.Grayscale;
    } else if (getType() == BufferedImage.TYPE_INT_ARGB) {
      return KfColorSpace.ARGB;
    }
    return KfColorSpace.RGB;
  }

  public byte[] getGrayData() {
    return this.pixelsGRAY;
  }

  public void setGrayData(byte[] data) {
    this.pixelsGRAY = data;
  }

  public int[] getRGBData() {
    return this.pixels;
  }

  public void setRGBData(int[] data) {
    this.pixels = data;
  }

  public int getSize() {
    return size;
  }

  public KfCoordinateSystem getCoordinateSystem() {
    return cSystem;
  }

  public void setCoordinateSystem(KfCoordinateSystem coSystem) {
    this.cSystem = coSystem;
    if (coSystem == KfCoordinateSystem.Matrix) {
      this.strideX = getWidth();
      this.strideY = 1;
    } else {
      this.strideX = 1;
      this.strideY = getWidth();
    }
  }

  public BufferedImage toBufferedImage() {
    BufferedImage b = new BufferedImage(getWidth(), getHeight(), getType());
    Graphics g = b.getGraphics();
    g.drawImage(this.bufferedImage, 0, 0, null);
    return b;
  }

  public Image toImage() {
    return Toolkit.getDefaultToolkit().createImage(bufferedImage.getSource());
  }

  public ImageIcon toIcon() {
    BufferedImage b = new BufferedImage(getWidth(), getHeight(), getType());
    Graphics g = b.getGraphics();
    g.drawImage(this.bufferedImage, 0, 0, null);
    return new ImageIcon(b);
  }

  public void toGrayscale() {
    new KfGrayscale().applyInPlace(this);
    pixels = null;
  }

  public void toARGB() {
    BufferedImage b = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = b.getGraphics();
    g.drawImage(this.bufferedImage, 0, 0, null);
    this.bufferedImage = b;
    refresh();
    g.dispose();
  }

  public void toRGB() {
    BufferedImage b = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
    Graphics g = b.getGraphics();
    g.drawImage(this.bufferedImage, 0, 0, null);
    this.bufferedImage = b;
    refresh();
    g.dispose();
  }

  public void clear() {
    if (isGrayscale()) {
      int size = pixelsGRAY.length;
      for (int i = 0; i < size; i++) {
        pixelsGRAY[i] = 0;
      }
    } else {
      int size = pixels.length;
      for (int i = 0; i < size; i++) {
        pixels[i] = 0;
      }
    }
  }

  public Graphics getGraphics() {
    return this.bufferedImage.getGraphics();
  }

  public void createGraphics() {
    this.bufferedImage.createGraphics();
  }

  private WritableRaster getRaster() {
    return this.bufferedImage.getRaster();
  }

  private int getType() {
    return this.bufferedImage.getType();
  }

  public boolean isGrayscale() {
    return bufferedImage.getType() == BufferedImage.TYPE_BYTE_GRAY;
  }

  public boolean isRGB() {
    return bufferedImage.getType() == BufferedImage.TYPE_INT_RGB;
  }

  public boolean isARGB() {
    return bufferedImage.getType() == BufferedImage.TYPE_INT_ARGB;
  }

  public int getWidth() {
    return bufferedImage.getWidth();
  }

  public int getHeight() {
    return bufferedImage.getHeight();
  }

  public int[] getRGB(int offset) {
    int[] rgb = new int[3];
    rgb[0] = pixels[offset] >> 16 & 0xFF;
    rgb[1] = pixels[offset] >> 8 & 0xFF;
    rgb[2] = pixels[offset] & 0xFF;
    return rgb;
  }

  public int[] getRGB(int x, int y) {
    int[] rgb = new int[3];
    rgb[0] = pixels[x * strideX + y * strideY] >> 16 & 0xFF;
    rgb[1] = pixels[x * strideX + y * strideY] >> 8 & 0xFF;
    rgb[2] = pixels[x * strideX + y * strideY] & 0xFF;
    return rgb;
  }

  public int getPackedRGB(int offset) {
    return pixels[offset];
  }

  public int getPackedRGB(int x, int y) {
    return pixels[x * strideX + y * strideY];
  }

  public int[] getARGB(int x, int y) {
    int[] argb = new int[4];
    argb[0] = pixels[x * strideX + y * strideY] >> 24 & 0xFF;
    argb[1] = pixels[x * strideX + y * strideY] >> 16 & 0xFF;
    argb[2] = pixels[x * strideX + y * strideY] >> 8 & 0xFF;
    argb[3] = pixels[x * strideX + y * strideY] & 0xFF;
    return argb;
  }

  public void setRGB(int x, int y, KfColor color) {
    setRGB(x, y, color.r, color.g, color.b);
  }

  public void setRGB(int x, int y, int red, int green, int blue) {
    int a = pixels[x * strideX + y * strideY] >> 24 & 0xFF;
    pixels[x * strideX + y * strideY] = a << 24 | red << 16 | green << 8 | blue;
  }

  public void setRGB(int x, int y, int[] rgb) {
    pixels[x * strideX + y * strideY] = rgb[0] << 16 | rgb[1] << 8 | rgb[2];
  }

  public void setRGB(int offset, int red, int green, int blue) {
    int a = pixels[offset] >> 24 & 0xFF;
    pixels[offset] = a << 24 | red << 16 | green << 8 | blue;
  }

  public void setRGB(int offset, int[] rgb) {
    int a = pixels[offset] >> 24 & 0xFF;
    pixels[offset] = a << 24 | rgb[0] << 16 | rgb[1] << 8 | rgb[2];
  }

  public void setRGB(int offset, KfColor color) {
    int a = pixels[offset] >> 24 & 0xFF;
    pixels[offset] = a << 24 | color.r << 16 | color.g << 8 | color.b;
  }

  public void setRGB(int offset, int color) {
    pixels[offset] = color;
  }

  public void setRGB(int x, int y, int color) {
    pixels[x * strideX + y * strideY] = color;
  }

  public void setARGB(int x, int y, int alpha, int red, int green, int blue) {
    pixels[x * strideX + y * strideY] = alpha << 24 | red << 16 | green << 8 | blue;
  }

  public void setARGB(int x, int y, int[] rgb) {
    pixels[x * strideX + y * strideY] = rgb[0] << 24 | rgb[1] << 16 | rgb[2] << 8 | rgb[3];
  }

  public void setARGB(int offset, int alpha, int red, int green, int blue) {
    pixels[offset] = alpha << 24 | red << 16 | green << 8 | blue;
  }

  public void setARGB(int offset, int[] argb) {
    pixels[offset] = argb[0] << 24 | argb[1] << 16 | argb[2] << 8 | argb[3];
  }

  public int getGray(int x, int y) {
    return pixelsGRAY[x * strideX + y * strideY] & 0xFF;
  }

  public int getGray(int offset) {
    return pixelsGRAY[offset] & 0xFF;
  }

  public void setGray(int offset, int value) {
    pixelsGRAY[offset] = (byte) value;
  }

  public void setGray(int x, int y, int value) {
    pixelsGRAY[x * strideX + y * strideY] = (byte) value;
  }

  public int getAlpha(int x, int y) {
    return pixels[x * strideX + y * strideY] >> 24 & 0xFF;
  }

  public int getAlpha(int offset) {
    return pixels[offset] >> 24 & 0xFF;
  }

  public void setAlpha(int offset, int value) {
    pixels[offset] = pixels[offset] & 0x00ffffff | value << 24;
  }

  public void setAlpha(int x, int y, int value) {
    pixels[x * strideX + y * strideY] = pixels[x * strideX + y * strideY] & 0x00ffffff | value << 24;
  }

  public int getRed(int x, int y) {
    return pixels[x * strideX + y * strideY] >> 16 & 0xFF;
  }

  public int getRed(int offset) {
    return pixels[offset] >> 16 & 0xFF;
  }

  public void setRed(int offset, int value) {
    pixels[offset] = pixels[offset] & 0xff00ffff | value << 16;
  }

  public void setRed(int x, int y, int value) {
    pixels[x * strideX + y * strideY] = pixels[x * strideX + y * strideY] & 0xff00ffff | value << 16;
  }

  public int getGreen(int x, int y) {
    return pixels[x * strideX + y * strideY] >> 8 & 0xFF;
  }

  public int getGreen(int offset) {
    return pixels[offset] >> 8 & 0xFF;
  }

  public void setGreen(int offset, int value) {
    pixels[offset] = pixels[offset] & 0xffff00ff | value << 8;
  }

  public void setGreen(int x, int y, int value) {
    pixels[x * strideX + y * strideY] = pixels[x * strideX + y * strideY] & 0xffff00ff | value << 8;
  }

  public int getBlue(int x, int y) {
    return pixels[x * strideX + y * strideY] & 0xFF;
  }

  public int getBlue(int offset) {
    return pixels[offset] & 0xFF;
  }

  public void setBlue(int offset, int value) {
    pixels[offset] = pixels[offset] & 0xffffff00 | value;
  }

  public void setBlue(int x, int y, int value) {
    pixels[x * strideX + y * strideY] = pixels[x * strideX + y * strideY] & 0xffffff00 | value;
  }

  public void setImage(BufferedImage bufferedImage){
    this.bufferedImage = bufferedImage;
    refresh();
  }

  public void setImage(KfBitmap fastBitmap){
    this.bufferedImage = fastBitmap.toBufferedImage();
    setCoordinateSystem(fastBitmap.getCoordinateSystem());
    refresh();
  }

  public int clampValues(int value) {
    if (value < 0)
      return 0;
    else if (value > 255)
      return 255;
    return value;
  }

  public int clampValues(int value, int min, int max) {
    if (value < min)
      return min;
    else if (value > max)
      return max;
    return value;
  }

}
