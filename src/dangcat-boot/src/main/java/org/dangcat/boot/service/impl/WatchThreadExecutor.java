package org.dangcat.boot.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.boot.service.WatchRunnable;
import org.dangcat.commons.timer.Timer;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.impl.ServiceControlBase;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;

/**
 * 监控线程服务。
 * 
 */
public class WatchThreadExecutor extends ServiceControlBase implements Runnable
{
    public static final Logger logger = Logger.getLogger(WatchThreadExecutor.class);
    private static final String SERVICE_NAME = "WatchExecutor";
    private static WatchThreadExecutor instance = null;
    /** 自动启动和关闭 */
    private boolean autoRun = true;
    /** core pool size */
    private int corePoolSize = 10;
    /** Maximum pool size */
    private int maximumPoolSize = 10;
    /** Queuw capcacity */
    private int queueCapacity = 10000;
    /** 线程池 */
    private ThreadPoolExecutor threadPoolExecutor = null;
    /** 处于阻塞的任务队列。 */
    private Timer timer = null;
    /** 检查周期 */
    private int watchInterval = 1000;
    /** 观察执行列表。 */
    private Collection<WatchUnit> watchRunnables = new HashSet<WatchUnit>();
    /** 处于阻塞的任务队列。 */
    private BlockingQueue<Runnable> workQueue = null;
    private WatchThreadExecutor()
    {
        this(null);
    }
    private WatchThreadExecutor(ServiceProvider parent)
    {
        super(parent, SERVICE_NAME);
    }

    public static synchronized WatchThreadExecutor createInstance(ServiceProvider parent) {
        if (instance == null) {
            instance = new WatchThreadExecutor(parent);
            instance.initialize();
        }
        return instance;
    }

    public static WatchThreadExecutor getInstance() {
        if (instance == null)
            createInstance(null);
        return instance;
    }

    private void clearTask()
    {
        synchronized (this.watchRunnables)
        {
            for (WatchUnit watchUnit : this.watchRunnables)
            {
                synchronized (watchUnit)
                {
                    watchUnit.notifyAll();
                }
            }
        }
    }

    public int getCorePoolSize()
    {
        return this.corePoolSize;
    }

    public void setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        if (this.threadPoolExecutor != null)
            this.threadPoolExecutor.setCorePoolSize(corePoolSize);
    }

    public int getMaximumPoolSize()
    {
        return this.maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        if (this.threadPoolExecutor != null)
            this.threadPoolExecutor.setMaximumPoolSize(maximumPoolSize);
    }

    public int getQueueCapacity()
    {
        return this.queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        this.queueCapacity = queueCapacity;
    }

    public int getWatchInterval()
    {
        return this.watchInterval;
    }

    public void setWatchInterval(int watchInterval) {
        this.watchInterval = watchInterval;
    }

    public boolean isActive()
    {
        return !this.watchRunnables.isEmpty() || this.threadPoolExecutor != null && this.threadPoolExecutor.getActiveCount() > 0;
    }

    public boolean isAutoRun()
    {
        return this.autoRun;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    @Override
    public void run()
    {
        WatchUnit[] watchUnits = null;
        synchronized (this.watchRunnables)
        {
            if (!this.watchRunnables.isEmpty())
                watchUnits = this.watchRunnables.toArray(new WatchUnit[0]);
        }
        if (watchUnits != null)
        {
            for (WatchUnit watchUnit : watchUnits)
            {
                if (watchUnit.isDone())
                    watchUnit.done();
                else if (watchUnit.isTimeOut())
                    watchUnit.terminate();
            }
        }
        if (this.isAutoRun())
        {
            synchronized (this.watchRunnables)
            {
                if (!this.isActive())
                    this.stopService();
            }
        }
    }

    @Override
    public void start()
    {
        if (!this.isRunning())
        {
            if (logger.isDebugEnabled())
                logger.debug("Begin to start the " + this.getClass().getSimpleName());

            if (this.workQueue == null)
                this.workQueue = new ArrayBlockingQueue<Runnable>(this.getQueueCapacity());
            if (this.threadPoolExecutor == null)
                this.threadPoolExecutor = new ThreadPoolExecutor(this.getCorePoolSize(), this.getMaximumPoolSize(), SERVICE_NAME, this.workQueue);
            // 监控定时器。
            if (this.timer == null)
            {
                this.timer = new Timer(SERVICE_NAME + "-Timer", this, this.getWatchInterval(), this.getWatchInterval());
                this.timer.start();
            }

            if (logger.isDebugEnabled())
                logger.debug("End start the " + this.getClass().getSimpleName());
            super.start();
        }
    }

    @Override
    public void stop()
    {
        this.stopService();
        this.clearTask();
    }

    private void stopService()
    {
        if (this.isRunning())
        {
            if (logger.isDebugEnabled())
                logger.debug("Begin to stop the " + this.getClass().getSimpleName());

            if (this.timer != null)
                this.timer.stop();
            this.timer = null;
            if (this.threadPoolExecutor != null)
                this.threadPoolExecutor.shutdown();
            this.threadPoolExecutor = null;
            if (this.workQueue != null)
                this.workQueue.clear();
            this.workQueue = null;

            if (logger.isDebugEnabled())
                logger.debug("End stop the " + this.getClass().getSimpleName());
            super.stop();
        }
    }

    public Object submit(WatchRunnable watchRunnable)
    {
        if (logger.isDebugEnabled())
            logger.debug("Begin to submit the " + watchRunnable);
        WatchUnit watchUnit = new WatchUnit(watchRunnable);
        try
        {
            if (this.isAutoRun() || this.isRunning())
            {
                synchronized (this.watchRunnables)
                {
                    this.watchRunnables.add(watchUnit);
                    if (this.isAutoRun())
                        this.start();
                }
            }
            if (this.isRunning())
            {
                Future<?> future = this.threadPoolExecutor.submit(watchUnit);
                watchUnit.setFuture(future);
                synchronized (watchUnit)
                {
                    if (!watchUnit.isDone())
                        watchUnit.wait();
                }
            }
        }
        catch (InterruptedException e)
        {
            Logger logger = watchUnit.getLogger();
            if (logger == null)
                logger = WatchThreadExecutor.logger;
            if (logger.isDebugEnabled())
                logger.error(watchRunnable, e);
            else
                logger.error(watchRunnable.toString() + " execute error : " + e);
        }
        finally
        {
            synchronized (this.watchRunnables)
            {
                this.watchRunnables.remove(watchUnit);
            }
            if (logger.isDebugEnabled())
                logger.debug("End submit the " + watchRunnable);
        }
        return watchUnit.getResult();
    }
}
