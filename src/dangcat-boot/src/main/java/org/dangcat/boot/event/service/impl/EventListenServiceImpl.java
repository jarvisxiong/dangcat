package org.dangcat.boot.event.service.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.event.service.EventListenService;
import org.dangcat.boot.event.service.EventListener;
import org.dangcat.boot.event.statistics.EventStatistics;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;

import java.util.Collection;
import java.util.HashSet;

/**
 * 消息侦听器。
 * @author dangcat
 * 
 */
public class EventListenServiceImpl extends EventServiceBase implements EventListenService
{
    private static final String SERVICE_NAME = "EventRecv";
    private Collection<EventListener> eventListeners = new HashSet<EventListener>();

    public EventListenServiceImpl(ServiceProvider parent)
    {
        super(parent, SERVICE_NAME);
    }

    @Override
    public void addEventListener(EventListener eventListener)
    {
        synchronized (this.eventListeners)
        {
            if (eventListener != null && !this.eventListeners.contains(eventListener))
                this.eventListeners.add(eventListener);
        }
    }

    @Override
    protected void execute(Event event)
    {
        EventStatistics eventStatistics = this.getEventStatistics();
        long beginTime = DateUtils.currentTimeMillis();
        eventStatistics.increaseHandleCount();

        ApplicationContext.getInstance().handle(event);

        eventStatistics.increaseHandleTimeCost(beginTime);
    }

    /**
     * 处理事件消息。
     * @param event 消息对象。
     */
    public void handleEvent(Event event)
    {
        this.addTask(event);
    }

    @Override
    public void onReceive(Event event)
    {
        this.addTask(event);
    }

    @Override
    public void removeEventListener(EventListener eventListener)
    {
        synchronized (this.eventListeners)
        {
            if (eventListener != null && this.eventListeners.contains(eventListener))
                this.eventListeners.remove(eventListener);
        }
    }

    @Override
    public void start()
    {
        synchronized (this.eventListeners)
        {
            for (EventListener eventListener : this.eventListeners)
                eventListener.start();
        }
        super.start();
    }

    @Override
    public void stop()
    {
        synchronized (this.eventListeners)
        {
            for (EventListener eventListener : this.eventListeners)
                eventListener.stop();
        }
        super.stop();
    }
}
