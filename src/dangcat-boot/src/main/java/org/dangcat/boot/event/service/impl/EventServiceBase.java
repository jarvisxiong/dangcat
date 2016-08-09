package org.dangcat.boot.event.service.impl;

import org.dangcat.boot.event.statistics.EventStatistics;
import org.dangcat.boot.service.impl.QueueThreadService;
import org.dangcat.boot.statistics.StatisticsService;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;

/**
 * 消息侦听器。
 *
 * @author dangcat
 */
public abstract class EventServiceBase extends QueueThreadService<Event> {
    private EventStatistics eventStatistics = null;

    public EventServiceBase(ServiceProvider parent, String name) {
        super(parent, name);
    }

    protected EventStatistics getEventStatistics() {
        if (this.eventStatistics == null)
            this.eventStatistics = new EventStatistics(this.getServiceName());
        return this.eventStatistics;
    }

    @Override
    public void initialize() {
        super.initialize();

        StatisticsService statisticsService = this.getService(StatisticsService.class);
        if (statisticsService != null)
            statisticsService.addStatistics(this.getEventStatistics());
    }
}
