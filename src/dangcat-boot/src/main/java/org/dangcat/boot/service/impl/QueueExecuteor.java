package org.dangcat.boot.service.impl;

/**
 * 队列执行接口。
 *
 * @param <T>
 * @author dangcat
 */
public interface QueueExecuteor<T> {
    /**
     * 队列线程执行接口。
     */
    void execute(T data);

}
