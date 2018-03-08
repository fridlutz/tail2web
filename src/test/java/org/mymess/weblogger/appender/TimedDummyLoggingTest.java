package org.mymess.weblogger.appender;

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
    Assert.assertEquals(2, logger.getMessageSize());
  }
}
