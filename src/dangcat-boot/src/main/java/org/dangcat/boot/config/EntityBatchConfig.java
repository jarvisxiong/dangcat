package org.dangcat.boot.config;

import org.dangcat.commons.utils.Environment;

/**
 * 实体批量操作配置。
 * @author dangcat
 * 
 */
public class EntityBatchConfig extends ServiceConfig
{
    public static final String BatchSize = "BatchSize";
    public static final String CronExpression = "CronExpression";
    private static final String CONFIG_NAME = "EntityBatch";
    private static final String MaxInterval = "MaxInterval";
    private static EntityBatchConfig instance = new EntityBatchConfig();
    /** 批量操作的大小。 */
    private int batchSize = 5000;
    /** 监控周期。 */
    private String cronExpression = "0/10 * * * * ?";
    /** 最大执行周期。 */
    private int maxInterval = 30;
    private EntityBatchConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(BatchSize, int.class, this.batchSize);
        this.addConfigValue(MaxInterval, int.class, this.maxInterval);
        this.addConfigValue(CronExpression, String.class, this.cronExpression);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static EntityBatchConfig getInstance() {
        return instance;
    }

    public int getBatchSize()
    {
        return Environment.isTestEnabled() ? 0 : this.getIntValue(BatchSize);
    }

    public String getCronExpression()
    {
        return this.getStringValue(CronExpression);
    }

    public int getMaxInterval()
    {
        return Environment.isTestEnabled() ? 0 : this.getIntValue(MaxInterval);
    }
}
