package org.dangcat.boot.event.service;

import org.dangcat.framework.event.Event;

/**
 * 消息发送接口。
 * @author dangcat
 * 
 */
public interface EventSender
{
    /**
     * 消息源名称。
     */
    public String getName();

    /**
     * 发送消息。
     * @param event 消息对象。
     */
    public void send(Event event);

    /**
     * 启动发送。
     */
    public void start();

    /**
     * 停止发送。
     */
    public void stop();
}
