package org.dangcat.business.service.impl;

import java.util.Collection;

import org.dangcat.business.service.DataFilter;

/**
 * 载入上下文。
 * @author dangcat
 * 
 * @param <T>
 */
public class QueryContext<T> extends OperateContext
{
    private Collection<T> data = null;
    private DataFilter dataFilter = null;

    public QueryContext(DataFilter dataFilter, Collection<T> data)
    {
        this.dataFilter = dataFilter;
        this.data = data;
    }

    public Collection<T> getData()
    {
        return this.data;
    }

    public DataFilter getDataFilter()
    {
        return dataFilter;
    }
}
