package org.dangcat.boot.service;

import org.apache.log4j.Logger;

/**
 * 监控线程执行接口。
 */
public interface WatchRunnable extends Runnable {
    /**
     * 最后响应时间，单位毫秒。
     */
    long getLastResponseTime();

    /**
     * 执行日志。
     */
    Logger getLogger();

    /**
     * 超时时长，单位毫秒。
     */
    long getTimeOutLength();

    /**
     * 超时终止接口。
     */
    void terminate();
}
