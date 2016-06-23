package org.dangcat.framework.service.impl;

import org.dangcat.commons.utils.DateUtils;

public class ServiceLiver
{
    private long lastAccessTime = DateUtils.currentTimeMillis();
    private Object service = null;

    public ServiceLiver(Object service)
    {
        this.service = service;
    }

    public long getLastAccessTime()
    {
        return lastAccessTime;
    }

    public Object getService()
    {
        this.lastAccessTime = DateUtils.currentTimeMillis();
        return service;
    }
}
