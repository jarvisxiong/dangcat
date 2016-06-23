package org.dangcat.boot.service;

import java.util.concurrent.RejectedExecutionHandler;

/**
 * 线程池服务。
 * @author dangcat
 * 
 */
public interface ThreadPoolService
{
    /**
     * 添加拒绝处理侦听器。
     * @param rejectedExecutionHandler 处理器。
     */
    void addRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);

    /**
     * 执行多线程任务。
     */
    void execute(Runnable runnable);

    /**
     * 当前正在执行的任务数。
     */
    int getActiveCount();

    /**
     * 当前处于阻塞的任务数。
     */
    int getBlockCount();

    /**
     * 是否已经停止。
     */
    boolean isShutdown();

    /**
     * 删除拒绝处理侦听器。
     * @param rejectedExecutionHandler 处理器。
     */
    void removeRejectedExecutionHandler(RejectedExecutionHandler rejectedExecutionHandler);
}
