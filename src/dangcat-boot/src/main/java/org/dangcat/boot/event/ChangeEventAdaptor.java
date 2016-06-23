package org.dangcat.boot.event;

import org.dangcat.framework.event.Event;

/**
 * 属性改变侦听接口。
 * @author dangcat
 * 
 */
public abstract class ChangeEventAdaptor
{
    /**
     * 变化后事件。
     * @param event 事件对象。
     */
    public void afterChanged(Object sender, Event event)
    {

    }

    /**
     * 变化前事件。
     * @param event 事件对象。
     */
    public void beforeChange(Object sender, Event event)
    {

    }
}
