package org.dangcat.boot.config;

import org.dangcat.commons.utils.Environment;

public class WebServiceConfig extends ServiceConfig
{
    public static final String ConfigFile = "ConfigFile";
    public static final String ContextRoot = "ContextRoot";
    public static final String KeyManagerPassword = "KeyManagerPassword";
    public static final String KeyStore = "KeyStore";
    public static final String KeyStorePassword = "KeyStorePassword";
    public static final String Port = "Port";
    public static final String Resources = "Resources";
    public static final String SslPort = "SslPort";
    public static final String ThreadPoolSize = "ThreadPoolSize";
    public static final String WebApp = "WebApp";
    private static final String CONFIG_NAME = "WebService";
    private static WebServiceConfig instance = new WebServiceConfig();
    /** 配置文件。 */
    private String configFile = "./webapp";
    /** 根路径 */
    private String contextRoot = "";
    /** 秘码管理口令。 */
    private String keyManagerPassword = null;
    /** 密码库。 */
    private String keyStore = null;
    /** 密码库口令。 */
    private String keyStorePassword = null;
    /** 开启端口。 */
    private Integer port = 8080;
    /** 资源集合。 */
    private String resources = null;
    /** SSL端口。 */
    private Integer sslPort = 8443;
    /** 线程池大小。 */
    private Integer threadPoolSize = 20;
    /** 密码库。 */
    private String webApp = "./webapp";
    public WebServiceConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(ConfigFile, String.class, this.configFile);
        this.addConfigValue(WebApp, String.class, this.webApp);
        this.addConfigValue(Resources, String.class, this.resources);
        this.addConfigValue(Port, Integer.class, this.port);
        this.addConfigValue(ThreadPoolSize, Integer.class, this.threadPoolSize);
        this.addConfigValue(SslPort, Integer.class, this.sslPort);
        this.addConfigValue(KeyStore, String.class, this.keyStore);
        this.addConfigValue(KeyStorePassword, String.class, this.keyStorePassword);
        this.addConfigValue(KeyManagerPassword, String.class, this.keyManagerPassword);
        this.addConfigValue(ContextRoot, String.class, this.contextRoot);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static WebServiceConfig getInstance() {
        return instance;
    }

    public String getConfigFile()
    {
        return this.getStringValue(ConfigFile);
    }

    public String getContextRoot()
    {
        return this.getStringValue(ContextRoot);
    }

    @Override
    protected boolean getDefaultEnabled()
    {
        return false;
    }

    public String getKeyManagerPassword()
    {
        return this.getStringValue(KeyManagerPassword);
    }

    public String getKeyStore()
    {
        return this.getStringValue(KeyStore);
    }

    public String getKeyStorePassword()
    {
        return this.getStringValue(KeyStorePassword);
    }

    public Integer getPort()
    {
        return this.getIntValue(Port);
    }

    public String getResources()
    {
        return this.getStringValue(Resources);
    }

    public Integer getSslPort()
    {
        return this.getIntValue(SslPort);
    }

    public Integer getThreadPoolSize()
    {
        return this.getIntValue(ThreadPoolSize);
    }

    public String getWebApp()
    {
        return this.getStringValue(WebApp);
    }

    @Override
    public boolean isEnabled()
    {
        return !Environment.isTestEnabled() && super.isEnabled();
    }
}