package impl;

import io.vacco.kimaris.impl.KmLogging;

public class KmTestLog implements KmLogging.KmLog {
  @Override public void info(String msg) { System.out.println(msg); }
  @Override public void debug(String msg) { System.out.println(msg); }
  @Override public boolean isDebugEnabled() { return false; }
  @Override public void trace(String msg) { System.out.println(msg); }
  @Override public boolean isTraceEnabled() { return false; }
}
