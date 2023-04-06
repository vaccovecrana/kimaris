package io.vacco.kimaris.util;

import io.vacco.kimaris.schema.*;

import static io.vacco.kimaris.util.KmBytes.pack;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class KmMath {

  public static double sqrt(double d) {
    return Double.longBitsToDouble(
      ((Double.doubleToRawLongBits(d) >> 32) + 1072632448) << 31
    );
  }

  public static double eclDist(double x0, double y0,
                               double x1, double y1) {
    return sqrt(
      ((x1 - x0) * (x1 - x0)) +
        ((y1 - y0) * (y1 - y0))
    );
  }

  public static double eclDist(KmPoint p0, KmPoint p1) {
    return eclDist(p0.x, p0.y, p1.x, p1.y);
  }

  public static KmBounds boundsFrom(double xMin, double yMin,
                                    double xMax, double yMax) {
    double r = yMin + (yMax - yMin) / 2;
    double c = xMin + (xMax - xMin) / 2;
    double s = KmMath.eclDist(xMin, yMin, xMax, yMax);
    return KmBounds.bounds(r, c, s);
  }

  public static float getOverlap(float r1, float c1, float s1,
                                 float r2, float c2, float s2) {
    float overR = max(0, min(r1 + s1 / 2, r2 + s2 / 2) - max(r1 - s1 / 2, r2 - s2 / 2));
    float overC = max(0, min(c1 + s1 / 2, c2 + s2 / 2) - max(c1 - s1 / 2, c2 - s2 / 2));
    return overR * overC / (s1 * s1 + s2 * s2 - overR * overC);
  }

  public static float getOverlap(KmBounds b0, KmBounds b1) {
    return getOverlap(
      b0.r, b0.c, b0.s,
      b1.r, b1.c, b1.s
    );
  }

  public static int getRandomTCode(KmBoundBox bb, KmRand r) {
    long
      p0 = r.mwcrand(), p1 = r.mwcrand(),
      p2 = r.mwcrand(), p3 = r.mwcrand();

    p0 = p0 % (bb.rMax - bb.rMin + 1);
    p1 = p1 % (bb.cMax - bb.cMin + 1);
    p2 = p2 % (bb.rMax - bb.rMin + 1);
    p3 = p3 % (bb.cMax - bb.cMin + 1);

    p0 = bb.rMin + p0;
    p1 = bb.cMin + p1;
    p2 = bb.rMin + p2;
    p3 = bb.cMin + p3;

    return pack((byte) p0, (byte) p1, (byte) p2, (byte) p3);
  }

  public static float sqr(float f) {
    return f * f;
  }

  public static double sqr(double d) {
    return d * d;
  }

}
