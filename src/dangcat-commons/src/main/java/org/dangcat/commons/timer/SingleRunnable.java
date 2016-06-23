package org.dangcat.commons.timer;

/**
 * 单线程运行。
 *
 * @author dangcat
 */
public class SingleRunnable implements Runnable {
    private boolean isRunning = false;
    private Runnable runnable = null;

    public SingleRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }

    public void setRunnable(Runnable runnable) {
        this.runnable = runnable;
    }

    public boolean isRunning() {
        return isRunning;
    }

    @Override
    public void run() {
        if (!this.isRunning()) {
            try {
                this.isRunning = true;
                this.runnable.run();
            } finally {
                this.isRunning = false;
            }
        }
    }
}
