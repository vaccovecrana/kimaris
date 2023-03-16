package io.vacco.kimaris.impl;

public class KmBytes {

  public static byte int3(int x) {
    return (byte) (x >> 24);
  }

  public static byte int2(int x) {
    return (byte) (x >> 16);
  }

  public static byte int1(int x) {
    return (byte) (x >> 8);
  }

  public static byte int0(int x) {
    return (byte) (x /*>> 0*/);
  }

  public static int pack(byte b0, byte b1, byte b2, byte b3) {
    return
      ((b0 & 255))       + ((b1 & 255) <<  8) +
      ((b2 & 255) << 16) + ((b3 & 255) << 24);
  }

}
