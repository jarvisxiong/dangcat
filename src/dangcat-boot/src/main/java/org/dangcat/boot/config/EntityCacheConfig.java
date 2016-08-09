package org.dangcat.boot.config;

import org.dangcat.boot.ApplicationContext;

/**
 * 缓存配置。
 *
 * @author dangcat
 */
public class EntityCacheConfig extends ServiceConfig {
    public static final String ConfigFile = "ConfigFile";
    public static final String CronExpression = "CronExpression";
    public static final String MessageName = "MessageName";
    private static final String CONFIG_NAME = "EntityCache";
    private static EntityCacheConfig instance = new EntityCacheConfig();
    /**
     * 缓存配置文件。
     */
    private String configFile = ApplicationContext.getInstance().getName() + ".cache.xml";
    /**
     * 监控周期。
     */
    private String cronExpression = "0 0 0/2 * * ?";
    /**
     * 同步消息主题。
     */
    private String messageName = null;

    private EntityCacheConfig() {
        super(CONFIG_NAME);

        this.addConfigValue(ConfigFile, String.class, this.configFile);
        this.addConfigValue(CronExpression, String.class, this.cronExpression);
        this.addConfigValue(MessageName, String.class, this.messageName);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static EntityCacheConfig getInstance() {
        return instance;
    }

    public String getConfigFile() {
        return super.getStringValue(ConfigFile);
    }

    public String getCronExpression() {
        return super.getStringValue(CronExpression);
    }

    public String getMessageName() {
        return super.getStringValue(MessageName);
    }
}
