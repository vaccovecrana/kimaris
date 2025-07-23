package io.vacco.kimaris;

import j8spec.annotation.DefinedOrder;
import j8spec.junit.J8SpecRunner;
import org.junit.runner.RunWith;

import static j8spec.J8Spec.*;
import static org.junit.Assert.*;

@DefinedOrder
@RunWith(J8SpecRunner.class)
public class KmAreaTest {

  public static void printMat(KmSchema.KmCoord crd, short[][] in) {
    System.out.println("======================================");
    System.out.println(crd);
    for (short[] r : in) {
      for (short v : r) {
        System.out.printf("%d ", v);
      }
      System.out.println();
    }
    System.out.println("======================================");
  }

  static {
    it("Calculates integral values", () -> {
      short[][] in0 = new short[][] {
          {5, 2, 5, 2},
          {3, 6, 3, 6},
          {5, 2, 5, 2}
      };
      short[][] out0 = new short[3][4];
      KmArea.areaSum(in0, out0);
    });

    it("Calculates an arbitrary region at four points", () -> {
      short[][] in = new short[][] {
          {5, 2, 5, 2},
          {3, 6, 3, 6},
          {5, 2, 5, 2},
          {3, 6, 3, 6}
      };
      short[][] out = new short[4][4];

      KmArea.areaSum(in, out);
      assertEquals(64.0, out[3][3], 0.01);

      double sum = KmArea.areaOf(out, 1, 1, 1, 3, 3, 3, 3, 1);
      assertEquals(16.0, sum, 0.01);
      sum = KmArea.areaOf(out, 2, 0, 2, 3);
      assertEquals(24.0, sum, 0.01);
      sum = KmArea.areaOf(out, 1, 1, 3, 3);
      assertEquals(39.0, sum, 0.01);
      sum = KmArea.areaOf(out, 0, 0, 3, 3);
      assertEquals(36.0, sum, 0.01);
      sum = KmArea.areaOf(out, 1, 1, 2, 3);
      assertEquals(24.0, sum, 0.01);
      sum = KmArea.areaOf(out, 1, 1, 1, 1);
      assertEquals(6.0, sum, 0.01);
      sum = KmArea.areaOf(out, 1, 1, 1, 2);
      assertEquals(9.0, sum, 0.01);
    });

    it("Performs convolution traversal on matrix data", () -> {
      short[][] mat = {
        { 0,  1,  2,  3,  4,  5,  6,  7,  8},
        { 9, 10, 11, 12, 13, 14, 15, 16, 17},
        {18, 19, 20, 21, 22, 23, 24, 25, 26},
        {27, 28, 29, 30, 31, 32, 33, 34, 35},
        {36, 37, 38, 39, 40, 41, 42, 43, 44},
        {45, 46, 47, 48, 49, 50, 51, 52, 53},
        {54, 55, 56, 57, 58, 59, 60, 61, 62},
        {63, 64, 65, 66, 67, 68, 69, 70, 71},
        {72, 73, 74, 75, 76, 77, 78, 79, 80}
      };
      printMat(null, mat);
      System.out.println("4x5;1,1");
      KmArea.convolve(4, 5, 1, 1, mat, KmAreaTest::printMat);
      System.out.println("4x4;2,2");
      KmArea.convolve(4, 4, 2, 2, mat, KmAreaTest::printMat);
      System.out.println("3x3;2,2");
      KmArea.convolve(3, 3, 2, 2, mat, KmAreaTest::printMat);
      System.out.println("4x3;6,3");
      KmArea.convolve(4, 3, 6, 3, mat, KmAreaTest::printMat);
      System.out.println("2x2;1,1");
      KmArea.convolve(2, 2, 1, 1, mat, KmAreaTest::printMat);
      System.out.println("1x1;2,2");
      KmArea.convolve(1, 1, 2, 2, mat, KmAreaTest::printMat);
    });
  }

}
