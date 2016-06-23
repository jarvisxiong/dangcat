package org.dangcat.commons.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * �̹߳����࣬���������̵߳�����
 */
public class NameThreadFactory implements ThreadFactory
{
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    /**
     * �߳�����ǰ׺
     */
    private String namePrefix;

    /**
     * ���캯��
     * @param namePrefix ����ǰ׺��
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