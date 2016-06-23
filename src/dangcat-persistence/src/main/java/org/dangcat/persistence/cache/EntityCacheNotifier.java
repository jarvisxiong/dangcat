package org.dangcat.persistence.cache;

import org.dangcat.persistence.entity.EntityPending;

import java.util.Collection;
import java.util.LinkedHashSet;

class EntityCacheNotifier
{
    private Collection<EntityUpdateNotifier> entityUpdateNotifiers = new LinkedHashSet<EntityUpdateNotifier>();

    protected void addNotifier(EntityUpdateNotifier entityUpdateNotifier)
    {
        if (entityUpdateNotifier != null)
            this.entityUpdateNotifiers.add(entityUpdateNotifier);
    }

    private Collection<Object> createDeletedRecord(Collection<Object> primaryKeys, Collection<Object> deletedPrimaryKeys, EntityCacheImpl<?> entityCacheImpl)
    {
        if (primaryKeys != null && !primaryKeys.isEmpty())
        {
            for (Object primaryKey : primaryKeys)
            {
                entityCacheImpl.removeEntity((Object[]) primaryKey);
                if (entityCacheImpl.isNotify())
                {
                    if (deletedPrimaryKeys == null)
                        deletedPrimaryKeys = new LinkedHashSet<Object>();
                    deletedPrimaryKeys.add(primaryKey);
                }
            }
        }
        return deletedPrimaryKeys;
    }

    private EntityUpdateRecord createEntityUpdateRecord(EntityPending entityPending)
    {
        EntityUpdateRecord entityUpdateRecord = new EntityUpdateRecord(entityPending.getEntityClass());
        Collection<Object> deleteCollection = entityPending.getDeletedCollection();
        if (deleteCollection != null && !deleteCollection.isEmpty())
            entityUpdateRecord.addDelete(deleteCollection.toArray());
        Collection<Object> modifiedCollection = entityPending.getModifiedCollection();
        if (modifiedCollection != null && !modifiedCollection.isEmpty())
            entityUpdateRecord.addModify(modifiedCollection.toArray());
        Collection<Object> insertCollection = entityPending.getInsertCollection();
        if (insertCollection != null && !insertCollection.isEmpty())
            entityUpdateRecord.addInsert(insertCollection.toArray());
        return entityUpdateRecord;
    }

    private Collection<Object> createModifyRecord(Collection<Object> primaryKeys, Collection<Object> updateEntities, EntityCacheImpl<?> entityCacheImpl)
    {
        if (primaryKeys != null && !primaryKeys.isEmpty())
        {
            for (Object primaryKey : primaryKeys)
            {
                Object entity = entityCacheImpl.refreshEntity((Object[]) primaryKey);
                if (entity != null && entityCacheImpl.isNotify())
                {
                    if (updateEntities == null)
                        updateEntities = new LinkedHashSet<Object>();
                    updateEntities.add(entity);
                }
            }
        }
        return updateEntities;
    }

    protected void removeNotifier(EntityUpdateNotifier entityUpdateNotifier)
    {
        if (entityUpdateNotifier != null)
            this.entityUpdateNotifiers.remove(entityUpdateNotifier);
    }

    protected void update(EntityPending entityPending, Collection<EntityCacheImpl<?>> entityCaches)
    {
        EntityUpdateRecord entityUpdateRecord = this.createEntityUpdateRecord(entityPending);
        Collection<Object> deletedPrimaryKeys = null;
        Collection<Object> updateEntities = null;
        for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
        {
            deletedPrimaryKeys = this.createDeletedRecord(entityUpdateRecord.getDeleted(), deletedPrimaryKeys, entityCacheImpl);
            updateEntities = this.createModifyRecord(entityUpdateRecord.getModified(), updateEntities, entityCacheImpl);
            updateEntities = this.createModifyRecord(entityUpdateRecord.getInserted(), updateEntities, entityCacheImpl);
        }
        if (this.entityUpdateNotifiers != null && (deletedPrimaryKeys != null || updateEntities != null))
        {
            for (EntityUpdateNotifier entityUpdateNotifier : this.entityUpdateNotifiers)
                entityUpdateNotifier.notifyUpdate(entityPending.getTableName(), deletedPrimaryKeys, updateEntities);
        }
    }
}
