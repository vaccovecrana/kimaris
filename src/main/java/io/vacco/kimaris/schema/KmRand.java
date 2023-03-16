package io.vacco.kimaris.schema;

import java.util.Objects;

/* Not thread safe */
public class KmRand {

  public long[] prng = new long[1];
  public int[]  m    = new int[2];

  public KmRand smwcRand(long seed) {
    prng[0] = 0x12345678000fffffL * seed;
    return this;
  }

  public KmRand smwcRand(long[] state) {
    this.prng = Objects.requireNonNull(state);
    return this;
  }

  public long mwcrand() {
    m[1] = (int) (prng[0] >> 32);
    m[0] = (int) prng[0];

    // System.out.printf("[st: %x, m0: %x, m1: %x]%n", prng[0], m[0], m[1]);

    // bad state?
    if(m[0] == 0) {
      m[0] = 0xAAAA;
    }
    if(m[1] == 0) {
      m[1] = 0xBBBB;
    }
    // mutate state
    m[0] = 36969 * (m[0] & 65535) + ((m[0] >> 16) & 0xffff);
    m[1] = 18000 * (m[1] & 65535) + ((m[1] >> 16) & 0xffff);
    prng[0] = (long) m[1] << 32 | m[0] & 0xFFFFFFFFL;

    int out = (m[0] << 16) + m[1];

    // System.out.printf("[st: %x, m0: %x, m1: %x, out: %x]%n", prng[0], m[0], m[1], out);

    return Integer.toUnsignedLong(out);
  }

  @Override public String toString() {
    return String.format("[st: %x, m0: %x, m1: %x]", prng[0], m[0], m[1]);
  }
}
