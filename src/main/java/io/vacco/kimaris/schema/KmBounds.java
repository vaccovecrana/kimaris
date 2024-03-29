package io.vacco.kimaris.schema;

import java.util.Objects;

public class KmBounds {

  /** translates to Y from top-left 0,0 */
  public int r;
  /** translates to X from top-left 0,0 */
  public int c;
  /** the diameter of a circle centered at `(c,r)` */
  public int s;

  public String tag;

  public KmBounds with(int r, int c, int s) {
    this.r = r;
    this.c = c;
    this.s = s;
    return this;
  }

  public KmBounds withD(float r, float c, float s) {
    return with((int) r, (int) c, (int) s);
  }

  public KmBounds withTag(String tag) {
    this.tag = Objects.requireNonNull(tag);
    return this;
  }

  public KmBounds invalidate() {
    return with(-1, -1, -1);
  }

  public boolean isValid() {
    return r != -1 && c != -1 && s != -1;
  }

  public KmBounds copyFrom(KmBounds other) {
    this.r = other.r;
    this.c = other.c;
    this.s = other.s;
    return this;
  }

  public static KmBounds bounds(double r, double c, double s) {
    var b = new KmBounds();
    return b.with((int) r, (int) c, (int) s);
  }

  public String id() {
    return String.format("%x%x%x", r, c, s);
  }

  @Override public String toString() {
    return String.format("[r: %d, c: %d, s: %d%s]",
      r, c, s, tag == null ? "" : String.format(", %s", tag)
    );
  }

}
