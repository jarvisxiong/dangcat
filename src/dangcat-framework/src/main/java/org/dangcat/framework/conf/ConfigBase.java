package org.dangcat.framework.conf;

import org.apache.log4j.Logger;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.commons.utils.Environment;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigBase implements ConfigProvider
{
    protected final Logger logger = Logger.getLogger(this.getClass());
    private Map<String, ConfigValue> configValueMap = new HashMap<String, ConfigValue>();
    private String name;

    public ConfigBase(String name)
    {
        this.name = name;
    }

    protected ConfigValue addConfigValue(String configName, Class<?> classType, Object defaultValue)
    {
        ConfigValue configValue = new ConfigValue(configName, classType, defaultValue);
        this.configValueMap.put(configName, configValue);
        return configValue;
    }

    protected boolean getBooleanValue(String configName)
    {
        return (Boolean) this.getCurrentValue(configName);
    }

    protected ConfigValue getConfigValue(String configName)
    {
        return this.configValueMap.get(configName);
    }

    @Serialize(ignore = true)
    public Map<String, ConfigValue> getConfigValueMap()
    {
        return configValueMap;
    }

    protected Object getCurrentValue(String configName)
    {
        return this.getConfigValue(configName).getCurrentValue();
    }

    protected Integer getIntValue(String configName)
    {
        return (Integer) this.getCurrentValue(configName);
    }

    protected long getLongValue(String configName)
    {
        return (Long) this.getCurrentValue(configName);
    }

    public String getName()
    {
        return name;
    }

    protected String getStringValue(String configName)
    {
        return (String) this.getCurrentValue(configName);
    }

    public void print()
    {
        logger.info(this);
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(this.getName());
        info.append(" Config:");
        if (this.configValueMap != null)
        {
            for (ConfigValue configValue : this.configValueMap.values())
            {
                info.append(Environment.LINETAB_SEPARATOR);
                info.append(configValue.getName());
                info.append(" = ");
                info.append(this.getCurrentValue(configValue.getName()));
            }
        }
        return info.toString();
    }
}
