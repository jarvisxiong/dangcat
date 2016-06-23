package org.dangcat.persistence.cache.xml;

import java.util.ArrayList;
import java.util.List;

public class Cache
{
    private String classType = null;
    private String databaseName = null;
    private List<String> indexList = new ArrayList<String>();
    private Integer interval = null;
    private boolean isPreload = false;
    private String loaderClass = null;
    private boolean notify = false;

    public String getClassType()
    {
        return this.classType;
    }

    public String getDatabaseName()
    {
        return this.databaseName;
    }

    public List<String> getIndexList()
    {
        return this.indexList;
    }

    public Integer getInterval()
    {
        return this.interval;
    }

    public String getLoaderClass()
    {
        return this.loaderClass;
    }

    public boolean isNotify()
    {
        return this.notify;
    }

    public boolean isPreload()
    {
        return this.isPreload;
    }

    public void setClassType(String classType)
    {
        this.classType = classType;
    }

    public void setDatabaseName(String databaseName)
    {
        this.databaseName = databaseName;
    }

    public void setInterval(Integer interval)
    {
        this.interval = interval;
    }

    public void setLoaderClass(String loaderClass)
    {
        this.loaderClass = loaderClass;
    }

    public void setNotify(boolean notify)
    {
        this.notify = notify;
    }

    public void setPreload(boolean isPreload)
    {
        this.isPreload = isPreload;
    }
}
