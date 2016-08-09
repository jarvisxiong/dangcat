package org.dangcat.boot.config;

/**
 * 统计配置。
 *
 * @author dangcat
 */
public class StatisticsConfig extends ServiceConfig {
    public static final String LogInterval = "LogInterval";
    public static final String StatisticsInterval = "StatisticsInterval";
    private static final String CONFIG_NAME = "Statistics";
    private static StatisticsConfig instance = new StatisticsConfig();
    /**
     * 输出统计信息周期（秒）。
     */
    private long logInterval = 60;
    /**
     * 统计周期（秒）。
     */
    private long statisticsInterval = 24 * 60 * 60;

    public StatisticsConfig() {
        super(CONFIG_NAME);

        this.addConfigValue(LogInterval, long.class, this.logInterval);
        this.addConfigValue(StatisticsInterval, long.class, this.statisticsInterval);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static StatisticsConfig getInstance() {
        return instance;
    }

    public long getLogInterval() {
        // 最小30秒。
        return Math.max(this.getLongValue(LogInterval), 30);
    }

    public long getStatisticsInterval() {
        // 最小一小时。
        return Math.max(this.getLongValue(StatisticsInterval), 60 * 60);
    }
}
