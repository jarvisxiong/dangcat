package org.dangcat.business.service.impl;

/**
 * 实体操作上下文。
 * @author dangcat
 * 
 * @param <T>
 */
public class LoadContext<T> extends OperateContext
{
    private T data = null;
    private Object[] primaryKeyValues = null;

    public LoadContext(Object... primaryKeyValues)
    {
        this.primaryKeyValues = primaryKeyValues;
    }

    public LoadContext(T data)
    {
        this.data = data;
    }

    public T getData()
    {
        return this.data;
    }

    protected void setData(T data) {
        this.data = data;
    }

    public Integer getPrimaryKey()
    {
        Integer id = null;
        if (this.primaryKeyValues != null && this.primaryKeyValues.length == 1 && this.primaryKeyValues[0] instanceof Integer)
            id = (Integer) this.primaryKeyValues[0];
        return id;
    }

    public Object[] getPrimaryKeyValues()
    {
        return primaryKeyValues;
    }
}
