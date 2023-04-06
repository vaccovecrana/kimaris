package impl;

import io.vacco.kimaris.util.KmLogging;

public class KmTestLog implements KmLogging.KmLog {

  public boolean logInfo = true, logDebug = false, logTrace = false;

  @Override public void info(String msg) { System.out.printf("INFO: %s%n", msg); }
  @Override public boolean isInfoEnabled() { return logInfo; }
  public KmTestLog withLogInfo(boolean logInfo) {
    this.logInfo = logInfo;
    return this;
  }

  @Override public void debug(String msg) { System.out.printf("DEBUG: %s%n", msg); }
  @Override public boolean isDebugEnabled() { return false; }
  public KmTestLog withLogDebug(boolean logDebug) {
    this.logDebug = logDebug;
    return this;
  }

  @Override public void trace(String msg) { System.out.printf("TRACE: %s%n", msg); }
  @Override public boolean isTraceEnabled() { return false; }
  public KmTestLog withLogTrace(boolean logTrace) {
    this.logTrace = logTrace;
    return this;
  }

}
