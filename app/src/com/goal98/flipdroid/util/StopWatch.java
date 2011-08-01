package com.goal98.flipdroid.util;

public class StopWatch {
    long start = 0;
    long end = 0;
    String event;

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
        System.out.println(event == null ? "No name event" : event + " cost " + (end - start) + "ms");
    }
}