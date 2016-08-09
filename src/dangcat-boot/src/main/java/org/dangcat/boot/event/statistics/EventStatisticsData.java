package org.dangcat.boot.event.statistics;

import org.dangcat.boot.statistics.StatisticsData;

public class EventStatisticsData extends StatisticsData {
    /**
     * 处理总数。
     */
    public static final String HandleCount = "HandleCount";
    /**
     * 处理耗时。
     */
    public static final String HandleTimeCost = "HandleTimeCost";
    /**
     * 处理速率。
     */
    public static final String HandleVelocity = "HandleVelocity";

    public EventStatisticsData(String name) {
        super(name);
        this.addCount(HandleCount);
        this.addTime(HandleTimeCost);
        this.addVelocity(HandleVelocity, HandleCount, HandleTimeCost);
    }
}
