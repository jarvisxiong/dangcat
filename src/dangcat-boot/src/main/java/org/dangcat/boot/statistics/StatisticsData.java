package org.dangcat.boot.statistics;

/**
 * 统计基础。
 *
 * @author dangcat
 */
public class StatisticsData extends StatisticsBase {
    public static final String Error = "Error";
    public static final String Success = "Success";

    public StatisticsData(String name) {
        super(name);
        this.addCount(Success);
        this.addCount(Error);
        this.addVelocity(Velocity, Success);
        this.addTime(TimeCost);
    }
}
