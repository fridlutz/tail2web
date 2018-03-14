package org.mymess.weblogger.appender;

import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.websocket.ClientEndpointConfig;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.ServerContainer;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.websocket.server.Constants;
import org.apache.tomcat.websocket.server.WsContextListener;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


public class WebLogAppenderTest {

  private static Tomcat tomcat;
  private static final int TOMCAT_PORT = 9999;

  private String onMessageReponse;
  private CountDownLatch messageLatch;
  private int messageCount = 0;
  private static final String SERVER_URL = "ws://localhost:9999/weblog";
  private static ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();



  public static class WebLogAppenderTestConfig extends WsContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
      super.contextInitialized(sce);
      ServerContainer sc = (ServerContainer) sce.getServletContext()
          .getAttribute(Constants.SERVER_CONTAINER_SERVLET_CONTEXT_ATTRIBUTE);
      try {
        sc.addEndpoint(WebLogAppender.class);
      } catch (DeploymentException e) {
        throw new IllegalStateException(e);
      }
    }
  }


  @BeforeClass
  public static void setUp()
      throws LifecycleException, ServletException, InterruptedException, DeploymentException {

    tomcat = new Tomcat();
    tomcat.setPort(TOMCAT_PORT);
    Context context = tomcat.addContext("", null);
    context.setSessionTimeout(10080);

    context.addApplicationListener(WebLogAppenderTestConfig.class.getName());
    Tomcat.addServlet(context, "default", new DefaultServlet());
    context.addServletMapping("/", "default");
    tomcat.init();
    tomcat.start();
  }

  @AfterClass
  public static void shutDownTomcat() throws LifecycleException {
    tomcat.stop();
  }


  @Test
  public void testOpenSocket() throws InterruptedException {
    WebSocketContainer container = ContainerProvider.getWebSocketContainer();
    Session session = null;
    // create a new concurrent latch

    try {
      session = container.connectToServer(new ClientTestEndpoint(), cec, new URI(SERVER_URL));
      // a valid session (not null) is an indication that the server could connect
      Assert.assertNotNull(session);


      messageLatch = new CountDownLatch(1);
      boolean openMessageReceivedByClient = messageLatch.await(30, TimeUnit.SECONDS);

      // check whether a message was received by the client
      Assert.assertTrue("Time lapsed before message was received by client.",
          openMessageReceivedByClient);
      // check message received when opening
      Assert.assertEquals("Connection available", onMessageReponse);


      // perform a test to check what happens when triggering a "start"
      messageLatch = new CountDownLatch(1);
      session.getBasicRemote().sendText("start");
      boolean startMessageReceivedByClient = messageLatch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Time lapsed before message was received by client.",
          startMessageReceivedByClient);
      // message should contain "New client joined", JSON parsing is not performed in test, but
      // basic string comparison
      Assert.assertTrue(onMessageReponse.contains("New client joined"));

      // reset counter and wait for incoming messages from timer (2secs ramp up, every 2 secs a
      // message
      // 10 secs = 5 messages -> ramp up is covered during init, it seems
      messageCount = 0;
      Thread.sleep(10000);
      Assert.assertTrue(messageCount > 3);


      // perform a test to check what happens when triggering a "stop"
      messageLatch = new CountDownLatch(1);
      session.getBasicRemote().sendText("stop");
      boolean stopMessageReceivedByClient = messageLatch.await(30, TimeUnit.SECONDS);
      Assert.assertTrue("Time lapsed before message was received by client.",
          stopMessageReceivedByClient);
      // message should contain "Client disconnected"
      Assert.assertTrue(onMessageReponse.contains("Client disconnected"));


    } catch (Exception e) {
      e.printStackTrace();
    }



  }

  class ClientTestEndpoint extends Endpoint {

    @Override
    public void onOpen(Session session, EndpointConfig config) {

      session.addMessageHandler(new MessageHandler.Whole<String>() {
        @Override
        public void onMessage(String message) {
          onMessageReponse = message;
          messageCount++;
          // trigger that response was received
          messageLatch.countDown();
        }
      });
    }
  }
}

