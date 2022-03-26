package io.vacco.fastbitmap;

import org.w3c.dom.Element;
import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import java.io.File;
import java.io.IOException;

public class KfBitmapIo {

  private static void save(KfBitmap i, String path, String formatName) {
    try {
      ImageIO.write(i.bufferedImage, formatName, new File(path));
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static void saveAsBMP(KfBitmap i, String pathname) { save(i, pathname, "bmp"); }
  public static void saveAsPNG(KfBitmap i, String pathname) { save(i, pathname, "png"); }
  public static void saveAsGIF(KfBitmap i, String pathname) { save(i, pathname, "gif"); }
  public static void saveAsJPG(KfBitmap i, String pathname) { save(i, pathname, "jpg"); }

  public static void saveAsJPG(KfBitmap i, String pathname, float quality) {
    try {
      JPEGImageWriteParam params = new JPEGImageWriteParam(null);
      params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      params.setCompressionQuality(quality);
      ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
      writer.setOutput(new FileImageOutputStream(new File(pathname)));
      writer.write(null, new IIOImage(i.bufferedImage, null, null), params);
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

  public static void saveAsJPG(KfBitmap i, String pathname, float quality, int xDpi, int yDpi) {
    try {
      ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
      IIOMetadata imageMetaData = writer.getDefaultImageMetadata(new ImageTypeSpecifier(i.bufferedImage), null);
      Element tree = (Element) imageMetaData.getAsTree("javax_imageio_jpeg_image_1.0");
      Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
      jfif.setAttribute("Xdensity", Integer.toString(xDpi));
      jfif.setAttribute("Ydensity", Integer.toString(yDpi));
      jfif.setAttribute("resUnits", "1");
      imageMetaData.setFromTree("javax_imageio_jpeg_image_1.0", tree);

      JPEGImageWriteParam params = new JPEGImageWriteParam(null);
      params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
      params.setCompressionQuality(quality);
      writer.setOutput(new FileImageOutputStream(new File(pathname)));
      writer.write(imageMetaData, new IIOImage(i.bufferedImage, null, imageMetaData), params);
      writer.dispose();
    } catch (IOException ex) {
      throw new IllegalStateException(ex);
    }
  }

}
