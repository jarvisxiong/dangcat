package org.dangcat.boot.statistics;

/**
 * 统计服务。
 *
 * @author dangcat
 */
public interface StatisticsService {
    /**
     * 添加统计对象。
     *
     * @param statisticsAble 统计对象。
     */
    void addStatistics(StatisticsAble statisticsAble);

    /**
     * 删除统计对象。
     *
     * @param statisticsAble 统计对象。
     */
    void removeStatistics(StatisticsAble statisticsAble);
}
