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
    Assert.assertEquals(500, logger.getMessageSize());
    // wait for timer to start and check again
    Thread.sleep(3000);
    Assert.assertEquals(501, logger.getMessageSize());
  }

}
