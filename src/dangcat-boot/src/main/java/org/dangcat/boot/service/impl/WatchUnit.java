package org.dangcat.boot.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.boot.service.WatchRunnable;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class WatchUnit implements Runnable
{
    protected static final int STATE_FINISHED = 2;
    protected static final int STATE_RUNNING = 1;
    protected static final int STATE_SUBMIT = 0;
    private Future<?> future = null;
    private long lastResponseTime = System.currentTimeMillis();
    private Object result = null;
    private int state = STATE_SUBMIT;
    private WatchRunnable watchRunnable = null;

    WatchUnit(WatchRunnable watchRunnable)
    {
        this.watchRunnable = watchRunnable;
    }

    protected void done()
    {
        Logger logger = this.getLogger();
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Begin to get result from the " + this.watchRunnable);
            this.result = this.future.get();
            if (logger.isDebugEnabled())
                logger.debug("End get result from the " + this.watchRunnable);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.error("Get result from the " + this.watchRunnable + " error", e);
            else
                logger.error("Get result from the " + this.watchRunnable + " error: " + e);
        }
        finally
        {
            synchronized (this)
            {
                this.notifyAll();
            }
        }
    }

    protected Future<?> getFuture()
    {
        return this.future;
    }

    protected void setFuture(Future<?> future) {
        this.future = future;
    }

    protected Logger getLogger()
    {
        Logger logger = this.watchRunnable.getLogger();
        if (logger == null)
            logger = WatchThreadExecutor.logger;
        return logger;
    }

    protected Object getResult()
    {
        return this.result;
    }

    protected WatchRunnable getWatchRunnable()
    {
        return this.watchRunnable;
    }

    protected boolean isDone()
    {
        return this.isFinished() || (this.future != null && (this.future.isDone() || this.future.isCancelled()));
    }

    protected boolean isFinished()
    {
        return this.state == STATE_FINISHED;
    }

    protected boolean isTimeOut()
    {
        if (this.state == STATE_SUBMIT)
            return false;

        long currentTimeMillis = System.currentTimeMillis();
        long lastResponseTime = Math.max(this.watchRunnable.getLastResponseTime(), this.lastResponseTime);
        return currentTimeMillis - lastResponseTime >= this.watchRunnable.getTimeOutLength();
    }

    @Override
    public void run()
    {
        Logger logger = this.getLogger();
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Begin to run in ThreadPool " + this.watchRunnable.getClass().getSimpleName());
            this.lastResponseTime = System.currentTimeMillis();
            this.state = STATE_RUNNING;

            this.watchRunnable.run();

            if (logger.isDebugEnabled())
                logger.debug("End run in ThreadPool " + this.watchRunnable.getClass().getSimpleName());
        }
        finally
        {
            this.state = STATE_FINISHED;
            synchronized (this)
            {
                this.notifyAll();
            }
        }
    }

    protected void terminate()
    {
        Logger logger = this.getLogger();
        try
        {
            if (logger.isDebugEnabled())
                logger.debug("Begin to terminate the " + this.watchRunnable);
            this.watchRunnable.terminate();
            this.result = this.future.get(10, TimeUnit.MICROSECONDS);
            if (logger.isDebugEnabled())
                logger.debug("End terminate the " + this.watchRunnable);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.error("Terminate the " + this.watchRunnable + " error", e);
            else
                logger.error("Terminate the " + this.watchRunnable + " error: " + e);
        }
        finally
        {
            synchronized (this)
            {
                this.notifyAll();
            }
        }
    }
}
