package org.dangcat.commons.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * 线程工厂类，用于设置线程的名称
 */
public class NameThreadFactory implements ThreadFactory
{
    /**
     * 线程名称前缀
     */
    private String namePrefix;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    /**
     * 构造函数
     * @param namePrefix 命名前缀。
     */
    public NameThreadFactory(String namePrefix)
    {
        this.namePrefix = namePrefix;
    }

    public Thread newThread(Runnable r)
    {
        return new Thread(r, namePrefix + threadNumber.getAndIncrement());
    }
}
