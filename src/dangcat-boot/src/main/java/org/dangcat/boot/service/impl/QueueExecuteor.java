package org.dangcat.boot.service.impl;

/**
 * 队列执行接口。
 * @author dangcat
 * 
 * @param <T>
 */
public interface QueueExecuteor<T>
{
    /**
     * 队列线程执行接口。
     */
    public void execute(T data);

}
