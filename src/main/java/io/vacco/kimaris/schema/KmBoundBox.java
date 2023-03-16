package io.vacco.kimaris.schema;

public class KmBoundBox {

  public byte rMin, rMax;
  public byte cMin, cMax;

  public static KmBoundBox bBox(byte rMin, byte rMax, byte cMin, byte cMax) {
    var bb = new KmBoundBox();
    bb.rMin = rMin; // 0
    bb.rMax = rMax; // 1
    bb.cMin = cMin; // 2
    bb.cMax = cMax; // 3
    return bb;
  }

  public static KmBoundBox getDefault() {
    return bBox(
      (byte) -127, (byte) +127,
      (byte) -127, (byte) +127
    );
  }

}
