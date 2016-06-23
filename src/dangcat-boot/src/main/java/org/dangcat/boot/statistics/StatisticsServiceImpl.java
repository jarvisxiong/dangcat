package org.dangcat.boot.statistics;

import org.dangcat.boot.config.StatisticsConfig;
import org.dangcat.boot.service.impl.ThreadService;
import org.dangcat.commons.timer.IntervalAlarmClock;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * 统计服务。
 * @author dangcat
 * 
 */
public class StatisticsServiceImpl extends ThreadService implements StatisticsService
{
    private static final String SERVICE_NAME = "STATISTICS";
    /** 统计起始时间。 */
    private long beginTime = DateUtils.currentTimeMillis();
    /** 统计对象列表。 */
    private List<StatisticsAble> statisticsList = new ArrayList<StatisticsAble>();

    /**
     * 构造服务。
     * @param parent 所属服务。
     */
    public StatisticsServiceImpl(ServiceProvider parent)
    {
        super(parent, SERVICE_NAME);
    }

    /**
     * 添加统计对象。
     * @param statisticsAble 统计对象。
     */
    public void addStatistics(StatisticsAble statisticsAble)
    {
        if (statisticsAble != null && !this.statisticsList.contains(statisticsAble))
            this.statisticsList.add(statisticsAble);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        this.setAlarmClock(new IntervalAlarmClock(this)
        {
            @Override
            public long getInterval()
            {
                return StatisticsConfig.getInstance().getLogInterval();
            }

            @Override
            public boolean isEnabled()
            {
                return StatisticsConfig.getInstance().isEnabled();
            }
        });
    }

    /**
     * 定时输出统计信息。
     */
    @Override
    protected void innerExecute()
    {
        if (this.statisticsList.size() > 0)
        {
            StringBuilder info = new StringBuilder();
            for (StatisticsAble statisticsAble : this.statisticsList)
            {
                if (statisticsAble.isValid())
                {
                    String message = statisticsAble.readRealInfo();
                    if (!ValueUtils.isEmpty(message))
                    {
                        info.append(Environment.LINE_SEPARATOR);
                        info.append(message);
                    }
                }
            }
            if (info.length() > 0)
                logger.info(info);
        }
        this.reset();
    }

    /**
     * 删除统计对象。
     * @param statisticsAble 统计对象。
     */
    public void removeStatistics(StatisticsAble statisticsAble)
    {
        if (statisticsAble != null && this.statisticsList.contains(statisticsAble))
            this.statisticsList.remove(statisticsAble);
    }

    /**
     * 重置统计值。
     */
    private void reset()
    {
        if (DateUtils.currentTimeMillis() - this.beginTime > StatisticsConfig.getInstance().getStatisticsInterval() * 1000)
        {
            for (StatisticsAble statisticsAble : this.statisticsList)
                statisticsAble.reset();
            this.beginTime = DateUtils.currentTimeMillis();
        }
    }
}
