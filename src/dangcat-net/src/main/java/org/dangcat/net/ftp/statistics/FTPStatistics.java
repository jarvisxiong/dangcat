package org.dangcat.net.ftp.statistics;

import org.dangcat.boot.statistics.ProcessStatistics;

public class FTPStatistics extends ProcessStatistics<FTPStatisticsData>
{
    public FTPStatistics(String name)
    {
        super(name);
    }

    @Override
    protected FTPStatisticsData creatStatisticsData(String name)
    {
        return new FTPStatisticsData(name);
    }

    public long increaseSize(long size)
    {
        return this.increase(FTPStatisticsData.Size, size);
    }
}
