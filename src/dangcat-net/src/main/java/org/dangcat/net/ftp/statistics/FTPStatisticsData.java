package org.dangcat.net.ftp.statistics;

import org.dangcat.boot.statistics.StatisticsData;

public class FTPStatisticsData extends StatisticsData
{
    /** 文件大小。 */
    public static final String Size = "Size";
    /** 文件大小速率。 */
    public static final String SizeVelocity = "SizeVelocity";

    public FTPStatisticsData(String name)
    {
        super(name);
        this.addCount(Size);
        this.addVelocity(SizeVelocity, Size, TimeCost);
    }
}
