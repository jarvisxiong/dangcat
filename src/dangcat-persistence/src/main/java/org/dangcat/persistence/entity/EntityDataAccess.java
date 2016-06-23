package org.dangcat.persistence.entity;

import org.dangcat.persistence.model.DataAccessBase;
import org.dangcat.persistence.model.Table;

import java.util.List;

/**
 * Entity对象数据读取器。
 * @author dangcat
 * 
 */
class EntityDataAccess<T> extends DataAccessBase
{
    private Class<?> classType = null;
    private List<T> entityList = null;
    private T total = null;

    public EntityDataAccess(List<T> entityList)
    {
        this.entityList = entityList;
    }

    public Class<?> getClassType()
    {
        if (this.classType == null && this.size() > 0)
            this.classType = this.getEntityList().get(0).getClass();
        return this.classType;
    }

    protected void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public List<T> getEntityList()
    {
        return entityList;
    }

    protected EntityMetaData getEntityMetaData()
    {
        return EntityHelper.getEntityMetaData(this.getClassType());
    }

    @Override
    protected Table getTable()
    {
        return this.getEntityMetaData().getTable();
    }

    public T getTotal()
    {
        return total;
    }

    public void setTotal(T total)
    {
        this.total = total;
    }

    public int size()
    {
        return this.entityList.size();
    }
}
