package org.dangcat.persistence.entity;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 实体保存缓存区。
 * @author dangcat
 * 
 */
public class EntityPending
{
    private Collection<Object> deletedCollection = null;
    private Class<?> entityClass = null;
    private EntityStatement entityStatement = null;
    private Collection<Object> insertCollection = null;
    private Collection<Object> modifiedCollection = null;

    public EntityPending(Class<?> entityClass)
    {
        this.entityClass = entityClass;
    }

    public void addDelete(Object entity)
    {
        if (entity != null)
        {
            if (this.deletedCollection == null)
                this.deletedCollection = new LinkedHashSet<Object>();
            this.deletedCollection.add(entity);
        }
    }

    public void addInsert(Object entity)
    {
        if (entity != null)
        {
            if (this.insertCollection == null)
                this.insertCollection = new LinkedHashSet<Object>();
            this.insertCollection.add(entity);
        }
    }

    public void addModify(Object entity)
    {
        if (entity != null)
        {
            if (this.modifiedCollection == null)
                this.modifiedCollection = new LinkedHashSet<Object>();
            this.modifiedCollection.add(entity);
        }
    }

    public Collection<Object> getDeletedCollection()
    {
        return this.deletedCollection;
    }

    public Class<?> getEntityClass()
    {
        return this.entityClass;
    }

    public EntityStatement getEntityStatement(SaveEntityContext saveEntityContext, String databaseName)
    {
        if (this.entityStatement == null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getEntityClass());
            if (saveEntityContext.isCustom())
            {
                this.entityStatement = new EntityStatement(entityMetaData, databaseName);
                this.entityStatement.initialize(saveEntityContext);
            }
            else
                this.entityStatement = entityMetaData.getEntityStatement(databaseName);
        }
        return this.entityStatement;
    }

    public Collection<Object> getInsertCollection()
    {
        return this.insertCollection;
    }

    public Collection<Object> getModifiedCollection()
    {
        return this.modifiedCollection;
    }

    public String getTableName()
    {
        String tableName = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getEntityClass());
        if (entityMetaData != null)
            tableName = entityMetaData.getTableName().getName();
        return tableName;
    }

    public int size()
    {
        int size = 0;
        if (this.deletedCollection != null)
            size += this.deletedCollection.size();
        if (this.insertCollection != null)
            size += this.insertCollection.size();
        if (this.modifiedCollection != null)
            size += this.modifiedCollection.size();
        return size;
    }
}
