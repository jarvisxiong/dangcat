package org.dangcat.boot.service.impl;

import org.dangcat.commons.utils.NameThreadFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

class ThreadPoolExecutor extends java.util.concurrent.ThreadPoolExecutor
{
    private AtomicInteger executingCount = new AtomicInteger();

    ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, String name, BlockingQueue<Runnable> workQueue)
    {
        this(corePoolSize, maximumPoolSize, name, workQueue, new AbortPolicy());
    }

    ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, String name, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler rejectedExecutionHandler)
    {
        super(
        // the number of threads to keep in the pool, even if they are idle.
                corePoolSize,
                // maximumPoolSize the maximum number of threads to allow in the
                // pool.
                maximumPoolSize,
                // keepAliveTime when the number of threads is greater than the
                // core, this is the maximum time that excess idle threads will
                // wait for new tasks before terminating.
                0,
                // the time unit for the keepAliveTime argument.
                TimeUnit.MILLISECONDS,
                // the queue to use for holding tasks before they are executed.
                // This queue will hold only the <tt>Runnable</tt> tasks
                // submitted by the <tt>execute</tt> method.
                workQueue,
                // the factory to use when the executor creates a new thread.
                new NameThreadFactory(name + "-"),
                // the handler to use when execution is blocked because the
                // thread bounds and queue capacities are reached.
                rejectedExecutionHandler);
    }

    @Override
    protected void afterExecute(Runnable runnable, Throwable throwable)
    {
        this.executingCount.decrementAndGet();
        super.afterExecute(runnable, throwable);
    }

    @Override
    protected void beforeExecute(Thread thread, Runnable runnable)
    {
        this.executingCount.incrementAndGet();
        super.beforeExecute(thread, runnable);
    }

    /**
     * 正在执行的任务数。
     */
    protected int getExecutingCount()
    {
        return this.executingCount.get();
    }
}
