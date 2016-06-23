package org.dangcat.boot.event.service.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.event.service.EventSendService;
import org.dangcat.boot.event.service.EventSender;
import org.dangcat.boot.event.statistics.EventStatistics;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.pool.ConnectionFactory;
import org.dangcat.framework.service.ServiceProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息侦发送服务。
 *
 * @author dangcat
 */
public class EventSendServiceImpl extends EventServiceBase implements EventSendService {
    private static final String RESOURCE_NAME = "ResourceName";
    private static final String SERVICE_NAME = "EventSend";
    private Map<String, EventSender> messageSenderMap = new HashMap<String, EventSender>();

    public EventSendServiceImpl(ServiceProvider parent) {
        super(parent, SERVICE_NAME);
    }

    public void addEventSender(EventSender eventSender) {
        if (eventSender != null) {
            synchronized (this.messageSenderMap) {
                this.messageSenderMap.put(eventSender.getName(), eventSender);
            }
        }
    }

    @Override
    protected void execute(Event event) {
        EventStatistics eventStatistics = this.getEventStatistics();
        long beginTime = DateUtils.currentTimeMillis();
        eventStatistics.increaseHandleCount();
        EventSender eventSender = this.getEventSender(event);
        if (eventSender != null)
            eventSender.send(event);
        else
            ApplicationContext.getInstance().handle(event);
        eventStatistics.increaseHandleTimeCost(beginTime);
    }

    private EventSender getEventSender(Event event) {
        EventSender eventSender = null;
        if (!Environment.isTestEnabled() && !this.messageSenderMap.isEmpty())
            eventSender = this.messageSenderMap.get(event.getParams().get(RESOURCE_NAME));
        return eventSender;
    }

    public void removeEventSender(EventSender eventListener) {
        if (eventListener != null) {
            synchronized (this.messageSenderMap) {
                this.messageSenderMap.put(eventListener.getName(), eventListener);
            }
        }
    }

    @Override
    public void send(Event event) {
        this.send(ConnectionFactory.DEFAULT, event);
    }

    @Override
    public void send(String name, Event event) {
        if (event != null) {
            event.getParams().put(RESOURCE_NAME, name);
            this.addTask(event);
        }
    }

    @Override
    public void start() {
        synchronized (this.messageSenderMap) {
            for (EventSender eventSender : this.messageSenderMap.values())
                eventSender.start();
        }
        super.start();
    }

    @Override
    public void stop() {
        synchronized (this.messageSenderMap) {
            for (EventSender eventSender : this.messageSenderMap.values())
                eventSender.stop();
        }
        super.stop();
    }
}
