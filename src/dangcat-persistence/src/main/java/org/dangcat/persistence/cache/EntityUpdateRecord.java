package org.dangcat.persistence.cache;

import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 实体保存缓存区。
 * @author dangcat
 * 
 */
class EntityUpdateRecord
{
    private Collection<Object> deleted = null;
    private Class<?> entityClass = null;
    private EntityMetaData entityMetaData = null;
    private Collection<Object> inserted = null;
    private Collection<Object> modified = null;

    EntityUpdateRecord(Class<?> entityClass)
    {
        this.entityClass = entityClass;
        this.entityMetaData = EntityHelper.getEntityMetaData(entityClass);
    }

    protected void addDelete(Object... entities)
    {
        if (entities != null)
        {
            for (Object entity : entities)
            {
                if (this.deleted == null)
                    this.deleted = new LinkedHashSet<Object>();
                Object[] values = this.entityMetaData.getPrimaryKeyValues(entity);
                if (values != null)
                    this.deleted.add(values);
            }
        }
    }

    protected void addInsert(Object... entities)
    {
        if (entities != null)
        {
            for (Object entity : entities)
            {
                if (this.inserted == null)
                    this.inserted = new LinkedHashSet<Object>();
                Object[] values = this.entityMetaData.getPrimaryKeyValues(entity);
                if (values != null)
                    this.inserted.add(values);
            }
        }
    }

    protected void addModify(Object... entities)
    {
        if (entities != null)
        {
            for (Object entity : entities)
            {
                if (this.modified == null)
                    this.modified = new LinkedHashSet<Object>();
                Object[] values = this.entityMetaData.getPrimaryKeyValues(entity);
                if (values != null)
                    this.modified.add(values);
            }
        }
    }

    protected Collection<Object> getDeleted()
    {
        return this.deleted;
    }

    protected Class<?> getEntityClass()
    {
        return this.entityClass;
    }

    protected Collection<Object> getInserted()
    {
        return this.inserted;
    }

    protected Collection<Object> getModified()
    {
        return this.modified;
    }
}
