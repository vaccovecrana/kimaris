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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.vacco.kimaris.schema.KmEns.ens;
import static io.vacco.uvc.Uvc.*;
import static javax.swing.UIManager.*;

public class KmCamTest {

  public static int w = 640, h = 480;

  public static class CamView extends JFrame {

    private static final long serialVersionUID = -1;

    private UvcCameraIO cio;

    private final BasicStroke bs = new BasicStroke(2);
    private final BufferedImage bgi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    private final Ellipse2D circle = new Ellipse2D.Float();
    private final Font font = new Font(Font.MONOSPACED, Font.ITALIC, 10);

    private final KmImage ki = new KmImage();

    private final KmDet face = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/facefinder-pico")),
      KmRegion.detectDefault().withDetectMax(24)
    );

    private final KmDet eye = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/eye")),
      KmRegion.detectDefault()
        .withDetectMax(8)
        .withSizeMin(16)
        .withSizeMax(196)
        .withDetectThreshold(4)
    );

    private final KmDet eyeCorner = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/eye-corner")),
      KmRegion.detectDefault()
        .withDetectMax(16)
        .withSizeMin(16)
        .withSizeMax(96)
        .withDetectThreshold(4)
    );

    private final KmDet eyePup = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/puploc-java")),
      KmRegion.detectDefault()
        .withDetectMax(8)
        .withSizeMin(16)
        .withSizeMax(196)
        .withDetectThreshold(4)
    );

    private final KmDet mouthCornerOut = new KmDet(
      KmCascades.loadPico(KmCamTest.class.getResource("/mouth-corner-out")),
      KmRegion.detectDefault()
        .withDetectMax(16)
        .withSizeMin(16)
        .withSizeMax(96)
    );

    private final Map<String, KmBounds> detections = new ConcurrentHashMap<>();
    private final Map<String, KmAvg3F> averages = new ConcurrentHashMap<>();

    private final KmEns ens = ens(face).withId("face")
      .then(
        ens(eye, kb -> kb.shift(.5f, .3f).resize(.5f)).withId("r-ey")
          .then(ens(eyePup, kb -> kb.shift(.5f, .5f).resize(1)).withId("r-ey-pup"))
          .then(ens(eyeCorner, kb -> kb.shift(.5f, .2f).resize(.8f)).withId("r-ey-rc"))
          .then(ens(eyeCorner, kb -> kb.shift(.5f, .7f).resize(.8f)).withId("r-ey-lc"))
      ).then(
        ens(eye, kb -> kb.shift(.5f, .7f).resize(.5f)).withId("ls-ey")
          .then(ens(eyePup, kb -> kb.shift(.5f, .5f).resize(1)).withId("l-ey-pup"))
          .then(ens(eyeCorner, kb -> kb.shift(.5f, .2f).resize(.8f)).withId("l-ey-rc"))
          .then(ens(eyeCorner, kb -> kb.shift(.5f, .7f).resize(.8f)).withId("l-ey-lc"))
      ).then(
        ens(mouthCornerOut, kb -> kb.shift(.9f, .3f).resize(.5f)).withId("l-mc-out")
      ).then(
        ens(mouthCornerOut, kb -> kb.shift(.9f, .7f).resize(.5f)).withId("r-mc-out")
      );

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

    private void drawRawDetections(Graphics2D g) {
      for (var e : detections.entrySet()) {
        var r = e.getValue().r;
        var c = e.getValue().c;
        var s = e.getValue().s;
        var rX = c + (s / 2);
        var rY = r + (s / 2);
        circle.setFrameFromCenter(c, r, rX, rY);
        g.draw(circle);
        g.drawOval(c, r, 2, 2);
        g.drawString(String.format("[%s]", e.getKey()), rX, rY);
      }
    }

    private void drawAvgDetections(Graphics2D g) {
      for (var e : averages.entrySet()) {
        var r = e.getValue().av0.val;
        var c = e.getValue().av1.val;
        var s = e.getValue().av2.val;
        var rX = c + (s / 2);
        var rY = r + (s / 2);
        g.drawOval((int) c, (int) r, 2, 2);
        g.drawString(String.format("[%s]", e.getKey()), rX, rY);
      }
    }

    private void drawDetections(Graphics2D g) {
      g.setColor(Color.RED);
      g.setFont(font);
      g.setStroke(bs);
      // drawRawDetections(g);
      drawAvgDetections(g);
    }

    public void updateCam(BufferedImage bi) {
      KmImages.setMeta(ki, bi, true);
      ens.run(ki, detections);
      for (var det : detections.values()) {
        var avg = averages.computeIfAbsent(det.tag, t -> new KmAvg3F().init(4));
        avg.update(det);
      }
      SwingUtilities.invokeLater(() -> {
        var g = bgi.createGraphics();
        g.setBackground(Color.BLUE);
        g.clearRect(0, 0, w, h);
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, w, h);
        g.drawImage(bi, 0, 0, null);
        drawDetections(g);
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
