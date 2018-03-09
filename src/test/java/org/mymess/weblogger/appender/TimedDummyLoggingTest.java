package org.mymess.weblogger.appender;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.Test;
import org.mymess.weblogger.dummy.TimedDummyLogging;
import junit.framework.Assert;

public class TimedDummyLoggingTest {

  @Test
  public void checkInstanceCreation() {
    TimedDummyLogging logger = TimedDummyLogging.getInstance();
    Assert.assertNotNull(logger);
  }

  @Test
  public void isTimerRunning() throws InterruptedException {
    TimedDummyLogging logger = TimedDummyLogging.getInstance();
    Assert.assertNotNull(logger.getMessageSize());
    Thread.sleep(5000);
    assertTrue(logger.getMessageSize() > 0);
  }
}
