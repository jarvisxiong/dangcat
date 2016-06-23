package org.dangcat.framework.event;

/**
 * 事件处理接口。
 * @author dangcat
 * 
 */
public interface EventHandler
{
    Object handle(Event event);
}
