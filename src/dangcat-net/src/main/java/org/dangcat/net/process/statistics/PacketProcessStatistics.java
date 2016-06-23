package org.dangcat.net.process.statistics;

import org.dangcat.boot.statistics.ProcessStatistics;

/**
 * 数据处理统计。
 * @author dangcat
 * 
 */
public class PacketProcessStatistics extends ProcessStatistics<PacketProcessStatisticsData>
{
    public PacketProcessStatistics(String name)
    {
        super(name);
    }

    @Override
    protected PacketProcessStatisticsData creatStatisticsData(String name)
    {
        return new PacketProcessStatisticsData(name);
    }

    public long increaseIgnore()
    {
        return this.increase(PacketProcessStatisticsData.Ignore);
    }

    /**
     * 解析错误。
     */
    public long increaseParseError()
    {
        return this.increase(PacketProcessStatisticsData.ParseError);
    }

    /**
     * 接收数据大小。
     */
    public long increaseReceive()
    {
        return this.increase(PacketProcessStatisticsData.Receive);
    }

    /**
     * 校验错误。
     */
    public long increaseValidError()
    {
        return this.increase(PacketProcessStatisticsData.ValidError);
    }
}
