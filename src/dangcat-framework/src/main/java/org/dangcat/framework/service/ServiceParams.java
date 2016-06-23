package org.dangcat.framework.service;

import java.util.HashMap;
import java.util.Map;

public abstract class ServiceParams implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    private Map<String, Object> params = new HashMap<String, Object>();

    public void addParam(Class<?> classType, Object value)
    {
        this.addParam(classType.getName(), value);
    }

    public void addParam(String name, Object value)
    {
        this.params.put(name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(Class<T> classType)
    {
        return (T) this.params.get(classType.getName());
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(String name)
    {
        return (T) this.params.get(name);
    }
}
