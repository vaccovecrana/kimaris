package io.vacco.kimaris.io;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Arrays.*;

public class KmArrays {

  public static byte[] bytesFrom(URL u) {
    try {
      var baos = new ByteArrayOutputStream();
      u.openStream().transferTo(baos);
      return baos.toByteArray();
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public static byte[] concat(byte[] first, byte[] second) {
    byte[] result = copyOf(first, first.length + second.length);
    System.arraycopy(second, 0, result, first.length, second.length);
    return result;
  }

  public static Optional<byte[]> concat(byte[][] values) {
    return stream(values)
        .filter(Objects::nonNull)
        .reduce(KmArrays::concat);
  }

  public static float[] toArrayF(List<Float> vals) {
    var out = new float[vals.size()];
    for (int i = 0; i < out.length; i++) {
      out[i] = vals.get(i);
    }
    return out;
  }

  public static byte[] toArrayB(List<byte[]> vals) {
    byte[][] mat = vals.toArray(byte[][]::new);
    return concat(mat).get();
  }
}
