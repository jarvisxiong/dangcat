package org.dangcat.persistence.entity;

import org.dangcat.persistence.filter.FilterExpress;

/**
 * 删除实体上下文。
 * @author dangcat
 * 
 */
public class DeleteEntityContext extends SaveEntityContext
{
    private Class<?> entityClass;
    private String[] fieldNames;
    private FilterExpress filterExpress = null;
    private Object[] values;

    /**
     * 按照过滤条件删除指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     */
    public DeleteEntityContext(Class<?> entityClass, FilterExpress filterExpress)
    {
        this.entityClass = entityClass;
        this.filterExpress = filterExpress;
    }

    /**
     * 删除指定主键的实体对象。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param primaryKeyValues 主键值。
     */
    public DeleteEntityContext(Class<?> entityClass, Object... primaryKeyValues)
    {
        this(entityClass, null, primaryKeyValues);
    }

    /**
     * 删除指定属性的的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名列表。
     * @param values 属性值。
     */
    public DeleteEntityContext(Class<?> entityClass, String[] fieldNames, Object... values)
    {
        this.entityClass = entityClass;
        this.fieldNames = fieldNames;
        this.values = values;
    }

    public Class<?> getEntityClass()
    {
        return this.entityClass;
    }

    public String[] getFieldNames()
    {
        return this.fieldNames;
    }

    public FilterExpress getFilterExpress()
    {
        return this.filterExpress;
    }

    public Object[] getValues()
    {
        return this.values;
    }

    @Override
    public void initialize()
    {
        if (this.filterExpress == null && this.values != null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.entityClass);
            String[] fieldNames = this.fieldNames;
            if (fieldNames == null)
                fieldNames = entityMetaData.getPrimaryKeyNames();
            this.filterExpress = entityMetaData.createFilterExpress(fieldNames, this.values);
        }
    }

    public void setFieldNames(String[] fieldNames)
    {
        this.fieldNames = fieldNames;
    }

    public void setFilterExpress(FilterExpress filterExpress)
    {
        this.filterExpress = filterExpress;
    }
}