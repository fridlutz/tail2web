package org.mymess.weblogger.appender;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/***
 * This implementation acts as a controller for logging messages using websocket
 * technology
 * 
 * @author wilfridutz
 *
 */

@ServerEndpoint("/weblog")

public class WebLogAppender extends AppenderSkeleton {
    
    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(WebLogAppender.class);

    private static Set<Session> allSessions = Collections.synchronizedSet(new HashSet<Session>());

    /***
     * register new session
     * 
     * @param session
     */

    @OnOpen
    public void registerSessions(Session session) {

    }

    @OnMessage
    public void pushProcessLog(String loggingMessage, Session session) throws IOException, InterruptedException {

	if (loggingMessage.equals("start")) {
	    allSessions.add(session);
	    session.getBasicRemote().sendText("Connection successfully established.");
	} else if (loggingMessage.equals("stop")) {
	    if (allSessions.contains(session)) {
		allSessions.remove(session);
		session.getBasicRemote().sendText("Connection successfully stopped.");
	    }
	} else {
	    if (allSessions.size() != 0) {
		for (Iterator<Session> iterator = allSessions.iterator(); iterator.hasNext();) {
		    Session registeredSession = iterator.next();
		    registeredSession.getBasicRemote().sendText(loggingMessage);

		}
	    }
	}

    }

    @OnError
    public void onError(Throwable e) {
	e.printStackTrace();
    }

    @OnClose
    public void onClose(Session session) {
	allSessions.remove(session);

    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
	return true;
    }

    @Override
    protected void append(LoggingEvent event) {
	try {
	    String color = "black";
	    if (event.getLevel().equals(Level.WARN))
		color = "red";
	    pushProcessLog("<span style=\"color:" + color + "\">" + this.getLayout().format(event) + "</span>", null);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (InterruptedException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

}
