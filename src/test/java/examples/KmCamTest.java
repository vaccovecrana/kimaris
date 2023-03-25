package examples;

import io.vacco.kimaris.impl.*;
import io.vacco.kimaris.schema.*;
import io.vacco.oruzka.core.OFnBlock;
import io.vacco.uvc.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

import static io.vacco.uvc.Uvc.*;
import static javax.swing.UIManager.*;

public class KmCamTest {

  public static int w = 640, h = 480;

  public static class CamView extends JFrame {

    private static final long serialVersionUID = -1;

    private UvcCameraIO cio;

    private final BasicStroke bs = new BasicStroke(4);
    private final BufferedImage bgi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    private final Ellipse2D circle = new Ellipse2D.Float();
    private final Font font = new Font(Font.MONOSPACED, Font.ITALIC, 16);

    private final KmImage ki = new KmImage();

    private final KmRegion fr = KmRegion.detectDefault().withDetectMax(24);

    private final KmRegion er = KmRegion.detectDefault()
      .withDetectMax(24)
      .withSizeMin(16)
      .withSizeMax(196)
      .withDetectThreshold(4);

    private final KmRegion mr = KmRegion.detectDefault()
      .withDetectMax(128)
      .withSizeMin(16)
      .withSizeMax(64)
      .withDetectThreshold(2);

    private final KmDet face = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/facefinder-pico")), fr
    );

    private final KmDet eyes = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/puploc-java")), er
    );

    private final KmDet k0 = face.then(eyes);

    public CamView() {
      setTitle(CamView.class.getCanonicalName());
      setPreferredSize(new Dimension(w, h));
      setLocationRelativeTo(null);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      addWindowListener(new WindowAdapter() {
        @Override public void windowClosing(WindowEvent e) {
          if (cio != null) {
            cio.close();
          }
        }
        @Override public void windowOpened(WindowEvent e) {
          var g = bgi.createGraphics();
          g.setPaint(Color.BLUE);
          g.fillRect(0, 0, bgi.getWidth(), bgi.getHeight());
          g.dispose();
          var label = new JLabel("", new ImageIcon(bgi), SwingConstants.CENTER);
          add(label);
          initCam();
        }
      });
    }

    private void drawDetections(KmRegion kr, Graphics2D g) {
      g.setColor(Color.RED);
      g.setFont(font);
      g.setStroke(bs);
      for (int i = 0; i < kr.detections.length; i++) {
        var det = kr.detections[i];
        if (det != null && det.isValid()) {
          var rX = det.c + (det.s / 2);
          var rY = det.r + (det.s / 2);
          circle.setFrameFromCenter(det.c, det.r, rX, rY);
          g.draw(circle);
          g.drawOval(det.c, det.r, 2, 2);
          g.drawString(String.format("[%d]", i), rX, rY);
        }
      }
    }

    public void updateCam(BufferedImage bi) {
      KmImages.setMeta(ki, bi, true);
      k0.processImage(ki, null);
      SwingUtilities.invokeLater(() -> {
        var g = bgi.createGraphics();
        g.setBackground(Color.BLUE);
        g.clearRect(0, 0, w, h);
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, w, h);
        g.drawImage(bi, 0, 0, null);
        drawDetections(fr, g);
        drawDetections(er, g);
        drawDetections(mr, g);
        g.dispose();
      });
      repaint();
    }

    public void initCam() {
      this.cio = new UvcCameraIO()
        .initFirst(w, h, 30, Uvc.UVCFrameFormat.UVC_FRAME_FORMAT_MJPEG)
        .start(frame -> {
          var f = uvc_allocate_frame(w * h);
          cio.checkStatus(Uvc.uvc_mjpeg2rgb(frame, f), "uvc_mjpeg2rgb");
          updateCam(UvcImageIO.imageFrom(f));
          uvc_free_frame(f);
        }, Throwable::printStackTrace);
    }
  }

  public static void main(String[] args) {
    EventQueue.invokeLater(() -> {
      OFnBlock.tryRun(() -> setLookAndFeel(getSystemLookAndFeelClassName()));
      var cv = new CamView();
      cv.pack();
      cv.setVisible(true);
    });
  }

}
