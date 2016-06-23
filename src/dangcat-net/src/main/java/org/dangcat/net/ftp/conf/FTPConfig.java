package org.dangcat.net.ftp.conf;

import org.dangcat.boot.config.ServiceConfig;

public class FTPConfig extends ServiceConfig
{
    public static final String Interval = "Interval";
    private static final String CONFIG_NAME = "FTPConfig";
    private static FTPConfig instance = new FTPConfig();
    /**
     * 读取周期（秒）。
     */
    private long interval = 30;

    public FTPConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(Interval, long.class, this.interval);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static FTPConfig getInstance() {
        return instance;
    }

    public long getInterval()
    {
        return Math.max(this.getLongValue(Interval), 30);
    }
}
