package org.dangcat.business.settle;

import org.dangcat.boot.config.ServiceConfig;

/**
 * 统计配置。
 * @author dangcat
 * 
 */
public class SettleConfig extends ServiceConfig
{
    private static final String CONFIG_NAME = "Settle";
    public static final String CronExpression = "CronExpression";
    private static SettleConfig instance = new SettleConfig();

    /**
     * 获取配置实例
     */
    public static SettleConfig getInstance()
    {
        return instance;
    }

    /** 监控周期（秒）。 */
    private String cronExpression = "0 * * * * ?";

    public SettleConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(CronExpression, String.class, this.cronExpression);

        this.print();
    }

    public String getCronExpression()
    {
        return super.getStringValue(CronExpression);
    }
}
