package dev.zprestige.ruby.util;

public class Timer {
    private long time = -1L;
    long startTime;
    boolean paused;
    long delay;

    public Timer() {
        this.startTime = System.currentTimeMillis();
        this.paused = false;
        this.delay = 0L;
    }

    public boolean getTime(long ms) {
        return getMs(System.nanoTime() - time) >= ms;
    }

    public long getCurrentTime(){
        return System.nanoTime() - time;
    }
    public boolean getTimeSub(long ms) {
        return getMs(System.nanoTime() - time) <= ms;
    }

    public void setTime(int time) {
        this.time = System.nanoTime();
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public boolean isPassed() {
        return !this.paused && System.currentTimeMillis() - this.startTime >= this.delay;
    }

    public void setDelay(final long delay) {
        this.delay = delay;
    }

    public void setPaused(final boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }
}