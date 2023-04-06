package io.vacco.kimaris.util;

import java.util.Objects;

public class KmLogging {

  public interface KmLog {
    void info(String msg);
    boolean isInfoEnabled();
    void debug(String msg);
    boolean isDebugEnabled();
    void trace(String msg);
    boolean isTraceEnabled();
  }

  public static KmLog log;

  public static void withLog(KmLog log) {
    KmLogging.log = Objects.requireNonNull(log);
  }

  public static void info(String msg) {
    if (log != null) {
      log.info(msg);
    }
  }

  public static void debug(String msg) {
    if (log != null) {
      log.debug(msg);
    }
  }

  public static void trace(String msg) {
    if (log != null) {
      log.trace(msg);
    }
  }

  public static boolean isInfoEnabled() {
    return log != null && log.isInfoEnabled();
  }

  public static boolean isDebugEnabled() {
    return log != null && log.isDebugEnabled();
  }

  public static boolean isTraceEnabled() {
    return log != null && log.isTraceEnabled();
  }

}
