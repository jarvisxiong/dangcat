package org.dangcat.commons.serialize.json;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.utils.ValueUtils;

import com.google.gson.stream.JsonWriter;

public abstract class CustomJsonSerializer implements JsonSerialize
{
    @Override
    public String getSerializeName(Class<?> classType)
    {
        return null;
    }

    protected String getSerializeName(String name, Object value)
    {
        return name;
    }

    protected Object getSerializeValue(String name, Object value)
    {
        return value;
    }

    @Override
    public boolean serialize(JsonWriter jsonWriter, String name, Object instance) throws IOException
    {
        if (instance != null)
        {
            Map<String, Object> valueMap = null;
            Map<String, Method> propertyMap = BeanUtils.getPropertyMethodMap(instance.getClass(), false);
            for (Entry<String, Method> entry : propertyMap.entrySet())
            {
                Object propertyValue = null;
                try
                {
                    propertyValue = entry.getValue().invoke(instance);
                }
                catch (Exception e)
                {
                }
                String propertyName = this.getSerializeName(entry.getKey(), propertyValue);
                if (!ValueUtils.isEmpty(name))
                {
                    if (valueMap == null)
                        valueMap = new HashMap<String, Object>();
                    propertyValue = this.getSerializeValue(propertyName, propertyValue);
                    valueMap.put(propertyName, propertyValue);
                }
            }
            JsonSerializer.serialize(jsonWriter, name, valueMap);
            return true;
        }
        return false;
    }

    @Override
    public boolean serializeClassType(JsonWriter jsonWriter, Class<?> classType) throws IOException
    {
        return false;
    }
}
