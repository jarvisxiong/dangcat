package org.dangcat.business.config;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;

@Table
public class BusinessSetup extends EntityBase implements DataStatus
{
    public static final String ConfigName = "ConfigName";
    public static final String Name = "Name";
    public static final String Value = "Value";
    private static final long serialVersionUID = 1L;
    @Column(isPrimaryKey = true, displaySize = 20, index = 1)
    private String configName = null;

    @Column(isPrimaryKey = true, displaySize = 20, index = 0)
    private String name = null;

    @Column(displaySize = 60, index = 2)
    private String value = null;

    public String getConfigName()
    {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    @Override
    public DataState getDataState()
    {
        return DataState.Insert;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }
}
