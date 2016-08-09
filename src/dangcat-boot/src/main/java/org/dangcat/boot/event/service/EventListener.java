package org.dangcat.boot.event.service;

/**
 * 消息侦听器。
 *
 * @author dangcat
 */
public interface EventListener {
    /**
     * 启动侦听器。
     */
    void start();

    /**
     * 停止侦听。
     */
    void stop();
}
