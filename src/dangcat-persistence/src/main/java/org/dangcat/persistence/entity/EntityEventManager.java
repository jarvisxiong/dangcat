package org.dangcat.persistence.entity;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.persistence.exception.EntityException;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * 实体事件管理。
 * @author dangcat
 * 
 */
class EntityEventManager
{
    /**
     * 事物提交后事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void afterCommit(SaveEntityContext saveEntityContext, Collection<Object> entityCollection) throws EntityException
    {
        if (entityCollection != null)
        {
            for (Object entity : entityCollection)
            {
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
                for (Method method : entityMetaData.getAfterCommitCollection())
                    ReflectUtils.invoke(entity, method, saveEntityContext);
            }
        }
    }

    /**
     * 删除后事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void afterDelete(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getAfterDeleteCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }

    /**
     * 新增后事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void afterInsert(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        RelationManager.update(entity);

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getAfterInsertCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }

    /**
     * 实体载入事件。
     * @param loadEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void afterLoad(LoadEntityContext loadEntityContext, Object entity) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getAfterLoadCollection())
            ReflectUtils.invoke(entity, method, loadEntityContext);
    }

    /**
     * 存储后事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void afterSave(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        RelationManager.update(entity);

        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getAfterSaveCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }

    /**
     * 删除前事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void beforeDelete(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getBeforeDeleteCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }

    /**
     * 新增前事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void beforeInsert(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getBeforeInsertCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }

    /**
     * 存储前事件。
     * @param saveEntityContext 实体会话对象。
     * @param entity 实体对象。
     * @throws EntityException 执行异常。
     */
    protected static void beforeSave(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
        for (Method method : entityMetaData.getBeforeSaveCollection())
            ReflectUtils.invoke(entity, method, saveEntityContext);
    }
}
