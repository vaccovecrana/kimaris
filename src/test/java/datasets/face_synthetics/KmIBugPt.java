package datasets.face_synthetics;

import io.vacco.kimaris.schema.*;

import static io.vacco.kimaris.util.KmMath.eclDist;
import static io.vacco.kimaris.schema.KmBounds.bounds;
import static io.vacco.kimaris.schema.KmPoint.pt;
import static java.lang.Float.parseFloat;

public enum KmIBugPt {

  IB18(18, 20, 1.2f),
  IB22(22, 20, 1.2f),
  IB23(23, 25, 1.2f),
  IB27(27, 25, 1.2f),
  IB20(20, 22, 1.2f),
  IB25(25, 27, 1.2f),

  IB40(40, 39, 1.3f),
  IB43(43, 44, 1.3f),

  IB37(37, 38, 1.5f),
  IB46(46, 45, 1.5f),

  IB49(49, 51, 1.5f),
  IB55(55, 53, 1.5f),

  IB51(51, 53, 1.2f),
  IB53(53, 51, 1.2f),

  IB59(59, 57, 1.1f),
  IB57(57, 59, 1.1f),

  IB69(69, 28, 1.4f), // face synthetics: right eye
  IB70(70, 28, 1.4f), // face synthetics: left eye
  ;

  private final int ix0, ix1;
  private final float mult;

  KmIBugPt(int ix0, int ix1, float mult) {
    this.ix0 = ix0;
    this.ix1 = ix1;
    this.mult = mult;
  }

  private KmPoint getPoint(String[][] xya, int ptIdx) {
    return pt(parseFloat(xya[ptIdx - 1][0]), parseFloat(xya[ptIdx - 1][1]));
  }

  public KmObj map(String[][] xya) {
    var pt0 = getPoint(xya, ix0);
    var pt1 = getPoint(xya, ix1);
    return new KmObj().add(pt0)
      .withBounds(bounds(pt0.y, pt0.x, eclDist(pt0, pt1) * mult));
  }

}
