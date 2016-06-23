package org.dangcat.boot;

import java.io.File;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

public class ContextPath
{
    private static final String BIN_DIRNAME = "bin";
    private static final String CLASSES_DIRNAME = "classes";
    private static final String CONF_DIRNAME = "conf";
    public static final String DANGCAT_BASE = "dangcat.base";
    public static final String DANGCAT_BIN = "dangcat.bin";
    public static final String DANGCAT_CLASSPATH = "dangcat.classpath";
    public static final String DANGCAT_CONF = "dangcat.conf";
    public static final String DANGCAT_DATA = "dangcat.data";
    public static final String DANGCAT_HOME = "dangcat.home";
    public static final String DANGCAT_LOG = "dangcat.log";
    public static final String DANGCAT_WEBAPPS = "dangcat.webapps";
    private static final String DATA_DIRNAME = "data";
    private static final String LOG_DIRNAME = "log";
    private static final String TARGET_DIRNAME = "target";
    private static final String WEBAPPS_DIRNAME = "webapp";

    private String bin = null;
    private String classPath = null;
    private String conf = null;
    private String data = null;
    private String log = null;
    private String webApps = null;

    public String getBaseDir()
    {
        if (this.isDebug())
            return this.getHomePath("..");
        return this.getHome();
    }

    private String getBasePath(String path)
    {
        return this.getBaseDir() + File.separator + path;
    }

    public String getBin()
    {
        if (this.bin == null)
        {
            if (this.isDebug())
                this.bin = this.getHomePath(CLASSES_DIRNAME);
            else
                this.bin = this.getHomePath(BIN_DIRNAME);
        }
        return this.bin;
    }

    public String getClassPath()
    {
        return this.classPath;
    }

    public String getConf()
    {
        if (this.conf == null)
            this.conf = FileUtils.getCanonicalPath(this.getBasePath(CONF_DIRNAME));
        return this.conf;
    }

    public String getData()
    {
        if (this.data == null)
            this.data = FileUtils.getCanonicalPath(this.getBasePath(DATA_DIRNAME));
        return this.data;
    }

    public String getHome()
    {
        return Environment.getHomePath();
    }

    private String getHomePath(String path)
    {
        return this.getHome() + File.separator + path;
    }

    public String getLog()
    {
        if (this.log == null)
            this.log = FileUtils.getCanonicalPath(this.getBasePath(LOG_DIRNAME));
        return this.log;
    }

    public String getWebApps()
    {
        if (this.webApps == null)
        {
            if (this.isDebug())
                this.webApps = this.getHomePath(".." + File.separator + "src" + File.separator + "main" + File.separator + WEBAPPS_DIRNAME);
            else
                this.webApps = FileUtils.getCanonicalPath(this.getBasePath(WEBAPPS_DIRNAME));
        }
        return this.webApps;
    }

    protected void initSystemProperties()
    {
        System.setProperty(DANGCAT_BASE, this.getBaseDir());
        System.setProperty(DANGCAT_HOME, this.getHome());
        System.setProperty(DANGCAT_BIN, this.getBin());
        System.setProperty(DANGCAT_CONF, this.getConf());
        System.setProperty(DANGCAT_DATA, this.getData());
        System.setProperty(DANGCAT_LOG, this.getLog());
        System.setProperty(DANGCAT_WEBAPPS, this.getWebApps());
    }

    private boolean isDebug()
    {
        return Environment.getHomePath().endsWith(TARGET_DIRNAME);
    }

    public void setClassPath(String classPath)
    {
        this.classPath = classPath;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(Environment.LINETAB_SEPARATOR);
        info.append("home: ");
        info.append(this.getHome());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(BIN_DIRNAME);
        info.append(": ");
        info.append(this.getBin());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(CONF_DIRNAME);
        info.append(": ");
        info.append(this.getConf());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(DATA_DIRNAME);
        info.append(": ");
        info.append(this.getData());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(LOG_DIRNAME);
        info.append(": ");
        info.append(this.getLog());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(WEBAPPS_DIRNAME);
        info.append(": ");
        info.append(this.getWebApps());
        if (!ValueUtils.isEmpty(this.getClassPath()))
        {
            info.append(Environment.LINETAB_SEPARATOR);
            info.append("classpath: " + this.getClassPath());
        }
        return info.toString();
    }
}
