package org.dangcat.boot.statistics;

/**
 * 数据处理统计。
 * @author dangcat
 * 
 */
public class ProcessStatistics<T extends StatisticsData> extends StatisticsBasic
{
    public ProcessStatistics(String name)
    {
        super(name);
    }

    @Override
    protected StatisticsData creatStatisticsData(String name)
    {
        return new StatisticsData(name);
    }

    public long increaseError()
    {
        return this.increase(StatisticsData.Error);
    }

    public long increaseError(long error)
    {
        return this.increase(StatisticsData.Error, error);
    }

    public long increaseSuccess()
    {
        return this.increase(StatisticsData.Success);
    }

    public long increaseSuccess(long success)
    {
        return this.increase(StatisticsData.Success, success);
    }
}
