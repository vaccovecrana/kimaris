package io.vacco.fastbitmap;

/**
 * Coordinate system.
 */
public enum KfCoordinateSystem {
  /**
   * Represents X and Y.
   * <p>Example:
   * <pre>
   * {@code
   * for(int y = 0; y < height; y++){
   *    for(int x = 0; x < width; x++){
   *       int g = fastBitmap.getGray(x,y);
   *       ...
   *    }
   * }
   * }
   * </pre>
   */
  Cartesian,

  /**
   * Represents I and J.
   * <p>Example:
   * <pre>
   * {@code
   * for(int i = 0; i < height; i++){
   *    for(int j = 0; j < width; j++){
   *       int g = fastBitmap.getGray(i,j);
   *       ...
   *    }
   * }
   * }
   * </pre>
   */
  Matrix
}
