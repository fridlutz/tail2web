package org.mymess.weblogger.appender;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;

public class WebLogAppenderStaticTest {
  private WebLogAppender appender = new WebLogAppender();


  @Test
  public void staticWebLogger() {

    Assert.assertNotNull(appender);
    Assert.assertTrue(!appender.isClosed());

    appender.close();
    Assert.assertTrue(appender.isClosed());


  }

  @Test(expected = NullPointerException.class)
  public void testNullPointerException() {
    appender.append(null);
    appender.pushProcessLog(null, null);
  }

  @Test(expected = IOException.class)
  public void testIOException() {

  }
}
