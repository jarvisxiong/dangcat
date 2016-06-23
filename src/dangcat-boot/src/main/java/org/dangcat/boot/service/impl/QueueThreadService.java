package org.dangcat.boot.service.impl;

import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.impl.ServiceControlBase;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 队列多线程服务基础类。
 * @author dangcat
 * 
 */
public class QueueThreadService<T> extends ServiceControlBase implements Runnable
{
    private static final int DEFAULT_CONCURRENT_SIZE = 1;
    private static final int DEFAULT_MAX_QUEUECAPACITY = 10000;
    /** 正在执行的任务队列。 */
    private Queue<T> executingCache = new LinkedList<T>();
    /** 最大并发数量。 */
    private int maxConcurrentSize = DEFAULT_CONCURRENT_SIZE;
    /** 最大队列容量。 */
    private int maxQueueCapacity = DEFAULT_MAX_QUEUECAPACITY;
    /** 队列执行接口。 */
    private QueueExecuteor<T> queueExecuteor = null;
    /** 等待执行的任务队列。 */
    private Queue<T> waitingCache = new LinkedList<T>();

    /**
     * 构造服务。
     * @param parent 所属服务。
     */
    public QueueThreadService(ServiceProvider parent)
    {
        super(parent);
    }

    /**
     * 构造服务。
     * @param parent 所属服务。
     */
    public QueueThreadService(ServiceProvider parent, String serviceName)
    {
        super(parent, serviceName);
    }

    /**
     * 构造服务。
     * @param parent 所属服务。
     * @param name 服务名称。
     * @param queueExecuteor 队列执行接口。
     */
    public QueueThreadService(ServiceProvider parent, String serviceName, QueueExecuteor<T> queueExecuteor)
    {
        this(parent, serviceName);
        this.queueExecuteor = queueExecuteor;
    }

    /**
     * 缓冲池。
     */
    public void addTask(T... tasks)
    {
        if (tasks != null)
        {
            for (T task : tasks)
            {
                if (this.isWaitingQueueFull())
                    this.onIgnoreProcess(task);
                else
                {
                    synchronized (this.waitingCache)
                    {
                        this.waitingCache.add(task);
                    }
                }
            }
            this.innerExecute();
        }
    }

    protected void execute(T data)
    {
        if (this.queueExecuteor != null)
            this.queueExecuteor.execute(data);
    }

    public int getExecuteSize()
    {
        return this.executingCache.size();
    }

    public int getMaxConcurrentSize()
    {
        return maxConcurrentSize;
    }

    public void setMaxConcurrentSize(int maxConcurrentSize) {
        this.maxConcurrentSize = maxConcurrentSize;
    }

    public int getMaxQueueCapacity()
    {
        return maxQueueCapacity;
    }

    public void setMaxQueueCapacity(int maxQueueCapacity) {
        this.maxQueueCapacity = maxQueueCapacity;
    }

    public QueueExecuteor<T> getQueueExecuteor()
    {
        return queueExecuteor;
    }

    public void setQueueExecuteor(QueueExecuteor<T> queueExecuteor) {
        this.queueExecuteor = queueExecuteor;
    }

    public int getWaitingSize()
    {
        return this.waitingCache.size();
    }

    private void innerExecute()
    {
        while (!this.isExecuteQueueFull() && this.getWaitingSize() > 0)
        {
            T task = null;
            synchronized (this.waitingCache)
            {
                task = this.waitingCache.poll();
            }
            if (task != null)
            {
                synchronized (this.executingCache)
                {
                    this.executingCache.add(task);
                }
                ThreadPoolFactory.getInstance().execute(this);
            }
        }
    }

    /**
     * 执行队列是否已经满负荷。
     */
    public boolean isExecuteQueueFull()
    {
        return this.getExecuteSize() >= this.getMaxConcurrentSize();
    }

    /**
     * 等待队列是否已经满负荷。
     */
    public boolean isWaitingQueueFull()
    {
        return this.getWaitingSize() >= this.getMaxQueueCapacity();
    }

    /**
     * 正在因为处理能力不足忽略的数据。
     */
    protected void onIgnoreProcess(T data)
    {
    }

    /**
     * 独立线程执行接口。
     */
    @Override
    public void run()
    {
        T task = null;
        synchronized (this.executingCache)
        {
            task = this.executingCache.poll();
        }
        if (task != null)
        {
            try
            {
                this.execute(task);
            }
            finally
            {
                this.innerExecute();
            }
        }
    }
}
