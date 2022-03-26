package io.vacco.kimaris.schema;

public class KmCascadeSet {

  public KmFaceCascade face;
  public KmCascade pupil;
  public KmCascade lp38, lp42, lp44, lp46, lp81, lp82, lp84, lp93, lp312;

  public KmCascade[] landmarks() {
    return new KmCascade[] {
        lp38, lp42, lp44, lp46,
        lp81, lp82, lp84, lp93, lp312
    };
  }
}
