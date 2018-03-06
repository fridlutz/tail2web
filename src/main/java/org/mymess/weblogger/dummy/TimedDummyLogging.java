package org.mymess.weblogger.dummy;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import org.apache.log4j.Logger;

public class TimedDummyLogging {

    Logger log = Logger.getLogger(TimedDummyLogging.class);
    static TimedDummyLogging timedLogger;
    
    
    public TimedDummyLogging() {
	Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                String msg = "Random Message " + UUID.randomUUID();
                Random rand = new Random();
                int  n = rand.nextInt(5) + 1;
                if (n == 1)
                    log.debug(msg);
                else if (n == 2)
                    log.info(msg);
                else if (n == 3)
                    log.warn(msg);
                else if (n == 4)
                    log.error(msg);
                else if (n == 5)
                    log.fatal(msg);
            }
        }, 0, 2000);
    }

    public static TimedDummyLogging getInstance() {
	if (timedLogger == null)
	    timedLogger = new TimedDummyLogging();
	return timedLogger;
    }
}
