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

    for (int i = 0; i < 500; i++) {
      logger.doLogging();
    }
    Assert.assertTrue(logger.getMessageSize() >= 500);

    Thread.sleep(5000);
    Assert.assertTrue(logger.getMessageSize() > 500);
  }

}
