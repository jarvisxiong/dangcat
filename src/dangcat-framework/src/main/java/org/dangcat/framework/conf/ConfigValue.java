package org.dangcat.framework.conf;

import org.dangcat.commons.utils.ValueUtils;

public class ConfigValue
{
    private Class<?> classType = null;
    private String configValue = null;
    private Object currentValue = null;
    private Object defaultValue = null;
    private String name = null;

    public ConfigValue(String name, Class<?> classType)
    {
        this(name, classType, null);
    }

    public ConfigValue(String name, Class<?> classType, Object defaultValue)
    {
        this.name = name;
        this.classType = classType;
        this.defaultValue = defaultValue;
    }

    public Class<?> getClassType()
    {
        return classType;
    }

    public String getConfigValue()
    {
        return configValue;
    }

    public Object getCurrentValue()
    {
        if (this.currentValue == null && !ValueUtils.isEmpty(this.configValue))
            this.currentValue = ValueUtils.parseValue(this.classType, this.configValue);
        return this.currentValue == null ? this.defaultValue : this.currentValue;
    }

    public Object getDefaultValue()
    {
        return defaultValue;
    }

    public String getName()
    {
        return name;
    }

    public boolean isDefaultValue()
    {
        return ValueUtils.compare(this.getDefaultValue(), this.getCurrentValue()) == 0;
    }

    public void setConfigValue(String configValue)
    {
        this.configValue = configValue;
        this.currentValue = null;
    }

    @Override
    public String toString()
    {
        return this.name + " = " + this.getCurrentValue();
    }
}
