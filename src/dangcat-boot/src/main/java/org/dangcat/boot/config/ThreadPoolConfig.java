package org.dangcat.boot.config;

/**
 * 线程池配置。
 * @author dangcat
 * 
 */
public class ThreadPoolConfig extends ServiceConfig
{
    public static final String MaximumPoolSize = "MaximumPoolSize";
    public static final String QueueCapacity = "QueueCapacity";
    private static final String CONFIG_NAME = "ThreadPool";
    private static ThreadPoolConfig instance = new ThreadPoolConfig();
    /** Maximum pool size */
    private int maximumPoolSize = 30;
    /** Queuw capcacity */
    private int queueCapacity = 10000;
    private ThreadPoolConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(MaximumPoolSize, int.class, this.maximumPoolSize);
        this.addConfigValue(QueueCapacity, int.class, this.queueCapacity);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static ThreadPoolConfig getInstance() {
        return instance;
    }

    public int getMaximumPoolSize()
    {
        return this.getIntValue(MaximumPoolSize);
    }

    public int getQueueCapacity()
    {
        return this.getIntValue(QueueCapacity);
    }
}
