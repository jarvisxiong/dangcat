package org.dangcat.boot.event.service;

import org.dangcat.framework.event.Event;

/**
 * 消息侦听服务。
 *
 * @author dangcat
 */
public interface EventListenService extends EventReceiveListener {
    /**
     * 添加消息侦听器。
     *
     * @param eventListener 消息侦听器。
     */
    void addEventListener(EventListener eventListener);

    /**
     * 处理事件消息。
     *
     * @param event 消息对象。
     */
    void handleEvent(Event event);

    /**
     * 删除消息侦听器。
     *
     * @param eventListener 消息侦听器。
     */
    void removeEventListener(EventListener eventListener);
}
