package org.dangcat.boot.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.config.ThreadPoolConfig;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.boot.service.ThreadPoolService;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.ServiceStatus;
import org.dangcat.framework.service.impl.ServiceControlBase;

/**
 * 线程池服务。
 * @author dangcat
 * 
 */
public class ThreadPoolFactory extends ServiceControlBase implements ThreadPoolService, RejectedExecutionHandler
{
    private static ThreadPoolFactory instance = null;
    private static final String SERVICE_NAME = "ThreadPoolWork";

    /**
     * 建立服务实例。
     * @param parent 所属父服务。
     * @return
     */
    public static synchronized ThreadPoolService createInstance(ServiceProvider parent)
    {
        if (instance == null)
        {
            instance = new ThreadPoolFactory(parent);
            instance.initialize();
        }
        return instance;
    }

    /**
     * 读取服务实例。
     * @return
     */
    public static ThreadPoolService getInstance()
    {
        return instance;
    }

    /** 拒绝事件列表。 */
    private List<RejectedExecutionHandler> rejectedExecutionHandlerList = new ArrayList<RejectedExecutionHandler>();
    /** 线程池 */
    private ThreadPoolExecutor threadPoolExecutor = null;
    /** 处于阻塞的任务队列。 */
    private BlockingQueue<Runnable> workQueue = null;

    /**
     * 构建服务
     * @param parent 所属父服务。
     */
    private ThreadPoolFactory(ServiceProvider parent)
    {
        super(parent);

        ThreadPoolConfig.getInstance().addChangeEventAdaptor(new ChangeEventAdaptor()
        {
            @Override
            public void afterChanged(Object sender, Event event)
            {
                if (ThreadPoolFactory.this.threadPoolExecutor != null)
                    ThreadPoolFactory.this.restart();
            }
        });
    }

    /**
     * 添加拒绝处理侦听器。
     * @param rejectedExecutionHandler 处理器。
     */
    public void addRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler)
    {
        if (rejectedExecutionHandler != null && !this.rejectedExecutionHandlerList.contains(rejectedExecutionHandler))
            this.rejectedExecutionHandlerList.add(rejectedExecutionHandler);
    }

    /**
     * 执行多线程任务。
     */
    @Override
    public void execute(Runnable runnable)
    {
        if (Environment.isTestEnabled())
            runnable.run();
        else
        {
            ThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutor;
            if (threadPoolExecutor != null)
                threadPoolExecutor.execute(runnable);
        }
    }

    /**
     * 当前正在执行的任务数。
     */
    @Override
    public int getActiveCount()
    {
        ThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutor;
        if (threadPoolExecutor != null)
            return threadPoolExecutor.getExecutingCount();
        return 0;
    }

    /**
     * 当前处于阻塞的任务数。
     */
    @Override
    public int getBlockCount()
    {
        BlockingQueue<Runnable> workQueue = this.workQueue;
        if (workQueue != null)
            return this.workQueue.size();
        return 0;
    }

    /**
     * 添加线程执行对象。
     */
    @Override
    public boolean isShutdown()
    {
        ThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutor;
        if (threadPoolExecutor != null)
            return threadPoolExecutor.isShutdown();
        return true;
    }

    /**
     * 触发拒绝处理侦听器。
     * @param runnable 处理器。
     * @param executor 线程池对象。
     */
    @Override
    public void rejectedExecution(Runnable runnable, java.util.concurrent.ThreadPoolExecutor threadPoolExecutor)
    {
        for (RejectedExecutionHandler rejectedExecutionHandler : this.rejectedExecutionHandlerList)
            rejectedExecutionHandler.rejectedExecution(runnable, threadPoolExecutor);
    }

    /**
     * 删除拒绝处理侦听器。
     * @param rejectedExecutionHandler 处理器。
     */
    @Override
    public void removeRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler)
    {
        if (rejectedExecutionHandler != null && this.rejectedExecutionHandlerList.contains(rejectedExecutionHandler))
            this.rejectedExecutionHandlerList.remove(rejectedExecutionHandler);
    }

    /**
     * 启动服务。
     */
    @Override
    public synchronized void start()
    {
        if (this.threadPoolExecutor == null && !Environment.isTestEnabled())
        {
            this.setServiceStatus(ServiceStatus.Starting);
            if (this.workQueue == null)
                this.workQueue = new ArrayBlockingQueue<Runnable>(ThreadPoolConfig.getInstance().getQueueCapacity());
            int corePoolSize = ThreadPoolConfig.getInstance().getMaximumPoolSize();
            int maximumPoolSize = ThreadPoolConfig.getInstance().getMaximumPoolSize();
            this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, SERVICE_NAME, this.workQueue, this);

            super.start();
        }
    }

    /**
     * 停止服务。
     */
    @Override
    public void stop()
    {
        ThreadPoolExecutor threadPoolExecutor = this.threadPoolExecutor;
        if (threadPoolExecutor != null)
        {
            this.setServiceStatus(ServiceStatus.Stopping);
            synchronized (this.threadPoolExecutor)
            {
                this.threadPoolExecutor = null;
            }
            threadPoolExecutor.shutdown();
            super.stop();
        }
    }

    /**
     * 向系统提交全局事件。
     * @param event 事件对象。
     */
    public void submitEvent(final Event event)
    {
        this.execute(new Runnable()
        {
            @Override
            public void run()
            {
                ApplicationContext.getInstance().handle(event);
            }
        });
    }
}
