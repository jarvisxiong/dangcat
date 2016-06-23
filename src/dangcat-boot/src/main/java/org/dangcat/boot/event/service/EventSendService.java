package org.dangcat.boot.event.service;

import org.dangcat.framework.event.Event;

/**
 * 消息发送服务。
 *
 * @author dangcat
 */
public interface EventSendService {
    /**
     * 发送消息对象。
     *
     * @param event 消息对象。
     */
    void send(Event event);

    /**
     * 发送消息对象。
     *
     * @param name  消息源名称。
     * @param event 消息对象。
     */
    void send(String name, Event event);
}
