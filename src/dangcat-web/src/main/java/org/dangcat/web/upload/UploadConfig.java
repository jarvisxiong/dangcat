package org.dangcat.web.upload;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.dangcat.boot.config.ServiceConfig;

/**
 * 上传配置。
 * @author dangcat
 * 
 */
public class UploadConfig extends ServiceConfig
{
    public static final String MaxMemorySize = "MaxMemorySize";
    public static final String MaxRequestSize = "MaxRequestSize";
    public static final String Repository = "Repository";
    private static final String CONFIG_NAME = "Upload";
    private static UploadConfig instance = new UploadConfig();
    /** 最大内存。 */
    private Integer maxMemorySize = DiskFileItemFactory.DEFAULT_SIZE_THRESHOLD;
    /** 最大文件上传。 */
    private Integer maxRequestSize = null;
    /** 临时目录路径。 */
    private String repository = System.getProperty("java.io.tmpdir");
    public UploadConfig()
    {
        super(CONFIG_NAME);

        this.addConfigValue(Repository, Integer.class, this.repository);
        this.addConfigValue(MaxMemorySize, Integer.class, this.maxMemorySize);
        this.addConfigValue(MaxRequestSize, Integer.class, this.maxRequestSize);

        this.print();
    }

    /**
     * 获取配置实例
     */
    public static UploadConfig getInstance() {
        return instance;
    }

    public Integer getMaxMemorySize()
    {
        return this.getIntValue(MaxMemorySize);
    }

    public Integer getMaxRequestSize()
    {
        return this.getIntValue(MaxRequestSize);
    }

    public String getRepository()
    {
        return this.getStringValue(Repository);
    }
}
