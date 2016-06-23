package org.dangcat.framework.conf;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.impl.PropertiesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 资源配置管理器。
 * @author dangcat
 * 
 */
public class ConfigureManager
{
    protected static final Logger logger = Logger.getLogger(ConfigureManager.class);
    private static ConfigureManager instance = new ConfigureManager();
    private File configFile = null;
    private Map<String, ConfigureCollection> configureCollectionMap = new HashMap<String, ConfigureCollection>();
    private ConfigureManager()
    {
    }

    public static ConfigureManager getInstance() {
        return instance;
    }

    /**
     * 通过文件配置数据源。
     * @param configFile 配置文件。
     */
    public boolean configure(File configFile)
    {
        if (configFile == null || !configFile.exists())
        {
            if (configFile != null)
                System.err.println("**The config file " + configFile.getAbsolutePath() + " is not exists!");
            return true;
        }

        this.configFile = configFile;
        this.log();

        FileInputStream inputStream = null;
        boolean result = true;
        try
        {
            Properties properties = new Properties();
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
            this.configure(properties);
        }
        catch (Exception e)
        {
            result = false;
            logger.error("Load the resource file error : " + configFile.getAbsolutePath(), e);
        }
        finally
        {
            FileUtils.close(inputStream);
        }
        return result;
    }

    /**
     * 分析配置设定。
     * @param properties 配置内容。
     */
    public void configure(Properties properties)
    {
        for (Object keyObject : properties.keySet())
        {
            String propertyName = (String) keyObject;
            String[] propertyNames = propertyName.split("\\.");
            if (propertyNames != null && propertyNames.length > 0)
            {
                String type = propertyNames[0].toLowerCase();
                if (!this.configureCollectionMap.containsKey(type))
                {
                    ConfigureCollection configureCollection = new ConfigureCollection(type);
                    configureCollection.configure(properties);
                    this.configureCollectionMap.put(type, configureCollection);
                }
            }
        }
    }

    /**
     * 根据资源类型得到资源配置集合。
     * @param type 资源类型名。
     * @return 配置集合。
     */
    public ConfigureCollection getConfigureCollection(String type)
    {
        return this.configureCollectionMap.get(type.toLowerCase());
    }

    private void log()
    {
        FileReader fileReader = null;
        try
        {
            StringBuilder info = new StringBuilder();
            info.append("The resource config " + this.configFile.getAbsolutePath() + ":");
            fileReader = new FileReader(this.configFile);
            BufferedReader reader = new BufferedReader(fileReader);
            String line = null;
            while ((line = reader.readLine()) != null)
            {
                if (line.trim().startsWith("#") || ValueUtils.isEmpty(line))
                    continue;

                info.append(Environment.LINETAB_SEPARATOR);
                info.append(PropertiesManager.getInstance().getValue(line));
            }
            if (info.length() > 0)
                logger.info(info);
        }
        catch (Exception e)
        {
            logger.error(this, e);
        }
        finally
        {
            FileUtils.close(fileReader);
        }
    }
}