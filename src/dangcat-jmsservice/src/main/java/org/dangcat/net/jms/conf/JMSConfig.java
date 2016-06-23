package org.dangcat.net.jms.conf;

import org.dangcat.boot.config.ServiceConfig;

/**
 * 统计配置。
 */
public class JMSConfig extends ServiceConfig {
    public static final String ConfigFile = "ConfigFile";
    public static final String ListenProcessSize = "ListenProcessSize";
    private static final String CONFIG_NAME = "JMS";
    private static JMSConfig instance = new JMSConfig();
    /**
     * ActiveMQ的配置文件。
     */
    private String configFile = "conf/jmsservice.activemq.xml";
    /**
     * 侦听并发处理线程数。
     */
    private int listenProcessSize = 20;

    public JMSConfig() {
        super(CONFIG_NAME);

        this.addConfigValue(ConfigFile, int.class, this.configFile);
        this.addConfigValue(ListenProcessSize, int.class, this.listenProcessSize);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static JMSConfig getInstance() {
        return instance;
    }

    public String getConfigFile() {
        return this.getStringValue(ConfigFile);
    }

    public int getListenProcessSize() {
        return Math.max(this.getIntValue(ListenProcessSize), 5);
    }
}
