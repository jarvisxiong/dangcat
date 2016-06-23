package org.dangcat.boot.config;

import org.dangcat.boot.ApplicationContext;

/**
 * 缓存配置。
 * @author dangcat
 * 
 */
public class EntityCacheConfig extends ServiceConfig
{
    private static final String CONFIG_NAME = "EntityCache";
    public static final String ConfigFile = "ConfigFile";
    public static final String CronExpression = "CronExpression";
    private static EntityCacheConfig instance = new EntityCacheConfig();
    public static final String MessageName = "MessageName";

    /**
     * 获取配置实例
     */
    public static EntityCacheConfig getInstance()
    {
        return instance;
    }

    /** 缓存配置文件。 */
    private String configFile = ApplicationContext.getInstance().getName() + ".cache.xml";
    /** 监控周期。 */
    private String cronExpression = "0 0 0/2 * * ?";
    /** 同步消息主题。 */
    private String messageName = null;

    private EntityCacheConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(ConfigFile, String.class, this.configFile);
        this.addConfigValue(CronExpression, String.class, this.cronExpression);
        this.addConfigValue(MessageName, String.class, this.messageName);

        this.print();
    }

    public String getConfigFile()
    {
        return super.getStringValue(ConfigFile);
    }

    public String getCronExpression()
    {
        return super.getStringValue(CronExpression);
    }

    public String getMessageName()
    {
        return super.getStringValue(MessageName);
    }
}
