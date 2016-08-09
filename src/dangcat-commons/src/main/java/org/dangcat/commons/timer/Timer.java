package org.dangcat.commons.timer;

/**
 * 定时器。
 *
 * @author dangcat
 */
public class Timer {
    private long delay = 0;
    private boolean isDaemon = false;
    private boolean isRunning = false;
    private String name = null;
    private long period = 0;
    private Runnable runnable = null;
    private Thread thread = null;

    /**
     * 构建定时器。
     *
     * @param name 定时器名称。
     */
    public Timer(String name) {
        this(name, null, 0, 0);
    }

    /**
     * 构建定时器。
     *
     * @param name     定时器名称。
     * @param runnable 任务接口。
     */
    public Timer(String name, Runnable runnable) {
        this(name, runnable, 0, 0);
    }

    /**
     * 构建定时器。
     *
     * @param name     定时器名称。
     * @param runnable 任务接口。
     * @param period   运行周期，单位（毫秒）。
     */
    public Timer(String name, Runnable runnable, long period) {
        this(name, runnable, 0, period);
    }

    /**
     * 构建定时器。
     *
     * @param name     定时器名称。
     * @param runnable 任务接口。
     * @param delay    延时长度，单位（毫秒）。
     * @param period   运行周期，单位（毫秒）。
     */
    public Timer(String name, Runnable runnable, long delay, long period) {
        this.name = name;
        this.runnable = runnable;
        this.delay = delay;
        this.period = period;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    private void intervalExec() {
        if (this.getDelay() > 0)
            this.waiting(this.getDelay());

        while (this.isRunning) {
            this.waiting(this.getPeriod());
            this.getRunnable().run();
        }
    }

    public boolean isDaemon() {
        return isDaemon;
    }

    public void setDaemon(boolean isDaemon) {
        this.isDaemon = isDaemon;
    }

    /**
     * 启动定时器。
     */
    public synchronized void start() {
        if (this.getRunnable() != null && this.getPeriod() > 0) {
            this.isRunning = true;
            this.thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    intervalExec();
                }
            }, this.name);
            this.thread.setDaemon(this.isDaemon());
            this.thread.start();
        }
    }

    /**
     * 停止定时器。
     */
    public synchronized void stop() {
        if (this.isRunning) {
            this.isRunning = false;
            this.thread.interrupt();
            this.thread = null;
        }
    }

    /**
     * 等待延时。
     *
     * @param milliSecond 延时长度，单位毫秒。
     */
    private synchronized void waiting(long milliSecond) {
        try {
            if (milliSecond > 0)
                this.wait(milliSecond);
        } catch (InterruptedException e) {
        }
    }
}
