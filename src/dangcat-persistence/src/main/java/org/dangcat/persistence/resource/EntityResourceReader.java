package org.dangcat.persistence.resource;

import java.util.Locale;
import java.util.Map;

import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.resource.ResourceReaderImpl;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityMetaData;

public class EntityResourceReader extends ResourceReader
{
    private EntityMetaData entityMetaData = null;
    private ResourceReader[] resourceReaders = null;

    public EntityResourceReader(EntityMetaData entityMetaData, ResourceReader[] resourceReaders)
    {
        this.entityMetaData = entityMetaData;
        this.resourceReaders = resourceReaders;
    }

    @Override
    public Object getObject(Locale locale, String key)
    {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        Object value = null;
        for (ResourceReader resourceReader : this.resourceReaders)
        {
            Class<?> classType = this.getResourceReaderClassType(resourceReader);
            if (classType != null)
            {
                String resourceKey = classType.getSimpleName() + "." + key;
                value = resourceReader.getObject(locale, resourceKey);
                if (value != null)
                    break;
            }
            if (!ValueUtils.isEmpty(this.getTableName()))
            {
                String tableNameResourceKey = this.getTableName() + "." + key;
                value = resourceReader.getObject(locale, tableNameResourceKey);
                if (value != null)
                    break;
            }
            value = resourceReader.getObject(locale, key);
            if (value != null)
                break;
        }
        return value;
    }

    private Class<?> getResourceReaderClassType(ResourceReader resourceReader)
    {
        Class<?> classType = null;
        if (resourceReader instanceof ResourceReaderImpl)
            classType = ((ResourceReaderImpl) resourceReader).getClassType();
        return classType;
    }

    public String getTableName()
    {
        return this.entityMetaData.getTableName().getPrefix();
    }

    @Override
    public String getText(Locale locale, String key, Object... params)
    {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        String text = null;
        for (ResourceReader resourceReader : this.resourceReaders)
        {
            Class<?> classType = this.getResourceReaderClassType(resourceReader);
            if (classType != null)
            {
                String resourceKey = classType.getSimpleName() + "." + key;
                text = resourceReader.getText(locale, resourceKey, params);
                if (!ValueUtils.isEmpty(text) && !resourceKey.equals(text))
                    break;
            }
            if (!ValueUtils.isEmpty(this.getTableName()))
            {
                String tableNameResourceKey = this.getTableName() + "." + key;
                text = resourceReader.getText(locale, tableNameResourceKey, params);
                if (!ValueUtils.isEmpty(text) && !tableNameResourceKey.equals(text))
                    break;
            }
            text = resourceReader.getText(locale, key, params);
            if (!ValueUtils.isEmpty(text) && !key.equals(text))
                break;
        }
        if (ValueUtils.isEmpty(text))
            return key;
        return this.format(text, params);
    }

    @Override
    public Map<Integer, String> getValueMap(Locale locale, String key)
    {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        Map<Integer, String> valueMap = null;
        for (ResourceReader resourceReader : this.resourceReaders)
        {
            Class<?> classType = this.getResourceReaderClassType(resourceReader);
            if (classType != null)
            {
                String resourceKey = classType.getSimpleName() + "." + key;
                valueMap = resourceReader.getValueMap(locale, resourceKey);
                if (valueMap != null)
                    break;
            }
            if (!ValueUtils.isEmpty(this.getTableName()))
            {
                String tableNameResourceKey = this.getTableName() + "." + key;
                valueMap = resourceReader.getValueMap(locale, tableNameResourceKey);
                if (valueMap != null)
                    break;
            }
            valueMap = resourceReader.getValueMap(locale, key);
            if (valueMap != null)
                break;
        }
        return valueMap;
    }
}
