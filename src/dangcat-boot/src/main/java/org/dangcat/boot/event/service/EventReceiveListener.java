package org.dangcat.boot.event.service;

import org.dangcat.framework.event.Event;

/**
 * 消息接收器。
 * @author dangcat
 * 
 */
public interface EventReceiveListener
{
    /**
     * 接收数据。
     * @param event 接收事件。
     */
    void onReceive(Event event);
}
