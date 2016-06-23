package org.dangcat.framework.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.util.*;

/**
 * 属性工具。
 * @author dangcat
 * 
 */
public class PropertiesManager
{
    private static final Logger logger = Logger.getLogger(PropertiesManager.class);
    private static PropertiesManager instance = new PropertiesManager();
    private Map<String, String> propertiesMap = new HashMap<String, String>();

    private PropertiesManager()
    {
    }

    public static PropertiesManager getInstance()
    {
        return instance;
    }

    private List<String> analyzePropertyName(String value)
    {
        List<String> propertyNameList = null;
        if (value != null)
        {
            int beginIndex = 0;
            for (int index = 0; index < value.length(); index++)
            {
                char charValue = value.charAt(index);
                if (charValue == '$' && index + 1 < value.length() && value.charAt(index + 1) == '{')
                {
                    index++;
                    beginIndex = index + 1;
                }
                if (charValue == '}' && beginIndex > 0 && index - beginIndex > 0)
                {
                    if (propertyNameList == null)
                        propertyNameList = new ArrayList<String>();
                    String propertyName = value.substring(beginIndex, index);
                    if (!propertyNameList.contains(propertyName))
                        propertyNameList.add(propertyName);
                    beginIndex = 0;
                }
            }
        }
        return propertyNameList;
    }

    private String getProperty(String name)
    {
        if (this.propertiesMap.containsKey(name))
            return this.propertiesMap.get(name);
        String value = System.getProperty(name);
        if (!ValueUtils.isEmpty(value))
            return value;
        return System.getenv(name);
    }

    public String getPropertyValue(String name)
    {
        return this.getValue(this.getProperty(name));
    }

    /**
     * 替换属性值里的系统环境变量。
     * @param value 属性值。
     * @return
     */
    public String getValue(String value)
    {
        String propertyValue = value;
        List<String> propertyNameList = this.analyzePropertyName(value);
        if (propertyNameList != null)
        {
            for (String key : propertyNameList)
            {
                String transValue = this.getProperty(key);
                if (transValue != null)
                    transValue = this.getValue(transValue);
                else
                    transValue = "";
                propertyValue = propertyValue.replace("${" + key + "}", transValue);
            }
        }
        return propertyValue;
    }

    public void load(File file)
    {
        if (file == null || !file.exists())
            return;
        try
        {
            this.load(new FileInputStream(file));
        }
        catch (FileNotFoundException e)
        {
            logger.error(this, e);
        }
    }

    public void load(InputStream inputStream)
    {
        try
        {
            Properties properties = new Properties();
            properties.load(inputStream);
            this.load(properties);
        }
        catch (IOException e)
        {
            logger.error(this, e);
        }
    }

    public void load(Properties properties)
    {
        if (properties != null)
        {
            for (Object key : properties.keySet())
            {
                String propertyValue = (String) properties.get(key);
                this.propertiesMap.put((String) key, propertyValue);
            }
        }
    }

    /**
     * 解析系统属性。
     * @param properties 属性配置。
     */
    public void loadSystemProperties(String value)
    {
        if (ValueUtils.isEmpty(value))
            return;

        ByteArrayInputStream inputStream = null;
        try
        {
            inputStream = new ByteArrayInputStream(value.getBytes());
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Object key : properties.keySet())
            {
                String propertyValue = (String) properties.get(key);
                System.setProperty((String) key, propertyValue);
            }
        }
        catch (IOException e)
        {
        }
        finally
        {
            FileUtils.close(inputStream);
        }
    }

    public void setProperty(String name, String value)
    {
        if (ValueUtils.isEmpty(value))
            this.propertiesMap.remove(name);
        else
            this.propertiesMap.put(name, value);
    }

    public void setSystemProperty(String name, String value)
    {
        if (ValueUtils.isEmpty(value))
            System.getProperties().remove(name);
        else
            System.setProperty(name, value);
    }

    public String toText()
    {
        StringBuilder info = new StringBuilder();
        info.append("Full System Properties Dump: ");
        Set<String> keySet = new TreeSet<String>();
        for (Object key : System.getProperties().keySet())
            keySet.add((String) key);
        for (String key : keySet)
        {
            info.append(Environment.LINETAB_SEPARATOR);
            info.append(key);
            info.append(" = ");
            info.append(this.getPropertyValue(key));
        }
        if (this.propertiesMap.size() > 0)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("Local properties list :");
            for (String key : this.propertiesMap.keySet())
            {
                info.append(Environment.LINETAB_SEPARATOR);
                info.append(key);
                info.append(" = ");
                info.append(this.getPropertyValue(key));
            }
        }
        return info.toString();
    }
}
