package org.dangcat.boot.event.statistics;

import org.dangcat.boot.statistics.ProcessStatistics;

public class EventStatistics extends ProcessStatistics<EventStatisticsData> {
    public EventStatistics(String name) {
        super(name);
    }

    @Override
    protected EventStatisticsData creatStatisticsData(String name) {
        return new EventStatisticsData(name);
    }

    public long increaseHandleCount() {
        return this.increase(EventStatisticsData.HandleCount);
    }

    /**
     * 统计处理次数。
     */
    public long increaseHandleCount(long handleCount) {
        return this.increase(EventStatisticsData.HandleCount, handleCount);
    }

    public void increaseHandleTimeCost(long beginTime) {
        this.end(EventStatisticsData.HandleTimeCost, beginTime);
    }
}
