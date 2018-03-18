/* Licensed under Apache-2.0 */
package org.mymess.weblogger.appender;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.mymess.weblogger.dummy.TimedDummyLogging;

/**
 * * This implementation acts as a controller for logging messages using websocket technology
 *
 * @author wilfridutz
 */
@ApplicationScoped
@ServerEndpoint("/weblog")
public class WebLogAppender extends AppenderSkeleton {
  private static Logger log = Logger.getLogger(WebLogAppender.class);
  private static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());

  /* Dummy logger that issues a random message every second */
  @SuppressWarnings("unused")
  private static TimedDummyLogging dummy = TimedDummyLogging.getInstance();

  /**
   * register new session
   *
   * @param session
   * @throws IOException
   */
  @OnOpen
  public void registerSessions(Session session) throws IOException {
    session.getBasicRemote().sendText("Connection available");
  }

  @OnMessage
  public void pushProcessLog(String loggingMessage, Session session) {
    try {
      if (loggingMessage.equals("start")) {
        if (!allSessions.contains(session)) {
          allSessions.add(session);
          log.info("New client joined: " + session);
        } else {
          // throw exception, if client is already connected
          session.getBasicRemote().sendText("Client already connected");
          throw new IOException();
        }
      } else if (loggingMessage.equals("stop")) {
        if (allSessions.contains(session)) {
          allSessions.remove(session);
          log.info("Client disconnected: " + session);

          // respond to requester
          session.getBasicRemote().sendText("Client disconnected");
          session.close();
        }

      } else {
        if (allSessions.size() != 0) {
          Iterator<Session> iterator = allSessions.iterator();
          while (iterator.hasNext()) {
            Session registeredSession = iterator.next();
            registeredSession.getBasicRemote().sendText(loggingMessage);
          }
        }
      }
    } catch (IOException e) {
      onError(e);
    }
  }

  @OnError
  public void onError(Throwable t) {
    log.error(t.fillInStackTrace());
  }

  @OnClose
  public void onClose(Session session) throws IOException {
    allSessions.remove(session);
    session.close();
  }

  @Override
  public void close() {
    this.closed = true;
  }

  @Override
  public boolean requiresLayout() {
    return true;
  }

  public boolean isClosed() {
    return this.closed;
  }

  @Override
  protected void append(LoggingEvent event) {
    pushProcessLog(this.getLayout().format(event), null);
  }
}
