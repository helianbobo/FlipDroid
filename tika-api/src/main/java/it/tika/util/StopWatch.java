package it.tika.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: cliu3
 * Date: 11-3-23
 * Time: 下午1:22
 * To change this template use File | Settings | File Templates.
 */
public class StopWatch {
    long start = 0;
    long end = 0;
    String event;
    Logger logger = Logger.getLogger(StopWatch.class.getName());

    public void start() {
        start = System.currentTimeMillis();
    }

    public void start(String event) {
        start();
        this.event = event;
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public void stopPrintReset() {
        stop();
        report();
        reset();
    }

    public void reset() {
        start = 0;
        end = 0;
        event = null;
    }

    public void report() {
        logger.log(Level.FINE,event == null ? "No name event" : event + " cost " + (end - start) + "ms");
    }
}
