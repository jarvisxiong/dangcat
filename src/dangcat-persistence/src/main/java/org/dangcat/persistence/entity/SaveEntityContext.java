package org.dangcat.persistence.entity;

import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;

import java.util.*;

/**
 * 实体会话。
 *
 * @author dangcat
 */
public class SaveEntityContext extends EntityContext {
    private ClassAccessSorter classAccessSorter = new ClassAccessSorter();
    private Map<Class<?>, EntityPending> entityPendingMap = new HashMap<Class<?>, EntityPending>();

    protected void afterCommit() {
        for (EntityPending entityPending : this.entityPendingMap.values()) {
            this.resetDataState(entityPending.getInsertCollection());
            this.resetDataState(entityPending.getModifiedCollection());

            RelationManager.rebuild(entityPending.getInsertCollection());
            RelationManager.rebuild(entityPending.getModifiedCollection());

            EntityEventManager.afterCommit(this, entityPending.getDeletedCollection());
            EntityEventManager.afterCommit(this, entityPending.getModifiedCollection());
            EntityEventManager.afterCommit(this, entityPending.getInsertCollection());

            EntityCacheManager.getInstance().update(entityPending);
        }

        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().afterCommit(this);
    }

    protected void afterDelete() {
        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().afterDelete(this);
    }

    protected void afterSave() {
        if (this.getEntityEventAdapter() != null) {
            this.getEntityEventAdapter().afterDelete(this);
            this.getEntityEventAdapter().afterSave(this);
        }
    }

    protected boolean beforeDelete() {
        if (this.getEntityEventAdapter() != null)
            return this.getEntityEventAdapter().beforeDelete(this);
        return true;
    }

    protected boolean beforeSave() {
        if (this.getEntityEventAdapter() != null) {
            if (!this.getEntityEventAdapter().beforeDelete(this))
                return false;
            if (!this.getEntityEventAdapter().beforeSave(this))
                return false;
        }
        return true;
    }

    /**
     * 删除指定的实体对象。
     *
     * @param <T>    实体类型。
     * @param entity 实体对象。
     * @throws EntityException
     */
    public void delete(Object entity) throws EntityException {
        if (entity != null) {
            this.classAccessSorter.access(entity.getClass());
            if (!DataState.Insert.equals(EntityUtils.checkDataState(entity)))
                RelationManager.delete(this, entity);
        }
    }

    public boolean exists(Object entity) {
        return this.loadOriginal(entity) != null;
    }

    /**
     * 实体类型的缓冲。
     *
     * @param entityClass 实体类型。
     * @return 缓冲对象。
     */
    public EntityPending getEntityPending(Class<?> entityClass) {
        EntityPending entityPending = this.entityPendingMap.get(entityClass);
        if (entityPending == null) {
            entityPending = new EntityPending(entityClass);
            this.entityPendingMap.put(entityClass, entityPending);
        }
        return entityPending;
    }

    public Queue<EntityPending> getEntityPendingQueue() {
        Queue<EntityPending> entityCacheQueue = new LinkedList<EntityPending>();
        for (Class<?> classType : this.classAccessSorter.getClassTypes()) {
            if (!this.entityPendingMap.containsKey(classType))
                this.getEntityPending(classType);
            entityCacheQueue.add(this.entityPendingMap.get(classType));
        }
        return entityCacheQueue;
    }

    public void initialize() {
    }

    @SuppressWarnings("unchecked")
    public <T> T loadOriginal(T entity) {
        if (entity == null || DataState.Insert.equals(EntityUtils.checkDataState(entity)))
            return null;

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        Object[] primaryKeyValues = entityMetaData.getPrimaryKeyValues(entity);
        return (T) this.getEntityManager().load(entity.getClass(), primaryKeyValues);
    }

    protected void onDeleteError(EntityException entityException) {
        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().onDeleteError(this, entityException);
    }

    protected void onSaveError(EntityException e) {
        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().onSaveError(this, e);
    }

    private void resetDataState(Collection<Object> collection) {
        if (collection != null) {
            for (Object entity : collection) {
                if (entity instanceof DataStatus) {
                    DataStatus dataStatus = (DataStatus) entity;
                    dataStatus.setDataState(DataState.Browse);
                }
            }
        }
    }

    /**
     * 保存指定实体对象。
     *
     * @param <T>    实体类型。
     * @param entity 实体对象。
     * @return 保存结果。
     * @throws EntityException
     */
    public void save(Object entity) throws EntityException {
        if (entity != null) {
            DataState dataState = EntityUtils.checkDataState(entity);
            this.classAccessSorter.access(entity.getClass());

            if (DataState.Deleted.equals(dataState))
                RelationManager.delete(this, entity);
            else if (DataState.Insert.equals(dataState))
                RelationManager.insert(this, entity);
            else
                RelationManager.modify(this, entity);
        }
    }

    /**
     * 要操作的实体数量。
     *
     * @return 总数量。
     */
    public int size() {
        int size = 0;
        for (EntityPending entityCache : this.entityPendingMap.values())
            size += entityCache.size();
        return size;
    }
}