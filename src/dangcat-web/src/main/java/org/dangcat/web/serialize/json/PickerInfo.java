package org.dangcat.web.serialize.json;

import org.dangcat.persistence.model.Column;

import java.util.Map;

public abstract class PickerInfo
{
    public void createColumnProperties(Map<String, Object> properties)
    {
    }

    public void createFieldProperties(Map<String, Object> properties)
    {
    }

    public String getDisplayField()
    {
        return this.getValueField();
    }

    public String[] getFilterFields()
    {
        return null;
    }

    public String getJndiName()
    {
        return null;
    }

    public Object getPickData()
    {
        return null;
    }

    public String getPickerIconSrc()
    {
        return "[SKIN]/button/pickInfo.png";
    }

    public abstract Column[] getPickListFields();

    public Map<String, Object> getPickListProperties()
    {
        return null;
    }

    public abstract Class<?> getResultClass();

    public abstract String getValueField();

    public boolean isTree()
    {
        return false;
    }
}
