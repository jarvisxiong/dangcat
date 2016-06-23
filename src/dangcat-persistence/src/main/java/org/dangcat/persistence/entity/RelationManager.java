package org.dangcat.persistence.entity;

import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.tablename.DateTimeTableName;
import org.dangcat.persistence.tablename.TableName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 实体关系事件管理。
 * @author dangcat
 * 
 */
class RelationManager
{
    /**
     * 删除主表的关联子表。
     */
    protected static void delete(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        if (entity != null)
        {
            Class<?> classType = entity.getClass();
            EntityEventManager.beforeDelete(saveEntityContext, entity);
            saveEntityContext.getEntityPending(classType).addDelete(entity);

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            for (Relation relation : entityMetaData.getRelations())
            {
                if (!relation.isAssociateDelete())
                    continue;

                FilterExpress filterExpress = relation.getChildFilterExpress(entity);
                if (filterExpress != null)
                {
                    List<?> entityList = load(saveEntityContext, relation.getMemberType(), filterExpress);
                    // 删除已经被清除的实体对象。
                    if (entityList != null)
                    {
                        for (Object childEntity : entityList)
                            delete(saveEntityContext, childEntity);
                    }
                }
            }
        }
    }

    /**
     * 主表插入后插入关联表。
     */
    protected static void insert(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        if (entity != null)
        {
            Class<?> classType = entity.getClass();
            EntityEventManager.beforeInsert(saveEntityContext, entity);
            saveEntityContext.getEntityPending(classType).addInsert(entity);

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            for (Relation relation : entityMetaData.getRelations())
            {
                if (!relation.isAssociateSave())
                    continue;

                Collection<Object> members = relation.getMembers(entity);
                if (members != null)
                {
                    for (Object member : members)
                        insert(saveEntityContext, member);
                }
            }
        }
    }

    private static List<Object> load(EntityContext entityContext, Class<?> classType, FilterExpress filterExpress)
    {
        LoadEntityContext loadEntityContext = new LoadEntityContext(classType, filterExpress);
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        TableName tableName = entityMetaData.getTableName();
        if (tableName instanceof DateTimeTableName && entityContext.getTableName() instanceof DateTimeTableName)
        {
            DateTimeTableName dateTimeTableName = new DateTimeTableName(tableName.getPrefix());
            dateTimeTableName.setDateTime(((DateTimeTableName) entityContext.getTableName()).getDateTime());
            loadEntityContext.setTableName(dateTimeTableName);
        }
        return entityContext.getEntityManager().load(loadEntityContext);
    }

    /**
     * 主表载入后载入关联表。
     */
    protected static void load(LoadEntityContext loadEntityContext, Object entity) throws EntityException
    {
        if (entity != null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            for (Relation relation : entityMetaData.getRelations())
            {
                if (!relation.isAssociateLoad())
                    continue;

                FilterExpress filterExpress = relation.getChildFilterExpress(entity);
                if (filterExpress != null)
                {
                    List<?> entityList = load(loadEntityContext, relation.getMemberType(), filterExpress);
                    if (entityList != null)
                    {
                        relation.load(entity, entityList);
                        for (Object childEntity : entityList)
                            load(loadEntityContext, childEntity);
                    }
                }
            }

            EntityEventManager.afterLoad(loadEntityContext, entity);
        }
    }

    private static List<Object> loadMembers(SaveEntityContext saveEntityContext, Relation relation, Object entity)
    {
        List<Object> entityList = null;
        if (relation.isCollectionMember())
        {
            FilterExpress filterExpress = relation.getChildFilterExpress(entity);
            if (filterExpress != null)
                entityList = load(saveEntityContext, relation.getMemberType(), filterExpress);
        }
        else
        {
            Collection<Object> members = relation.getMembers(entity);
            if (members != null)
            {
                Object childEntity = members.iterator().next();
                if (!saveEntityContext.exists(childEntity))
                {
                    entityList = new ArrayList<Object>();
                    entityList.add(childEntity);
                }
            }

        }
        return entityList;
    }

    /**
     * 主表存储之后同时存储关联表。
     */
    protected static void modify(SaveEntityContext saveEntityContext, Object entity) throws EntityException
    {
        if (entity != null)
        {
            Class<?> classType = entity.getClass();
            EntityEventManager.beforeSave(saveEntityContext, entity);
            saveEntityContext.getEntityPending(classType).addModify(entity);

            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            for (Relation relation : entityMetaData.getRelations())
            {
                List<Object> entityList = null;
                if (relation.isAssociateDelete())
                    entityList = loadMembers(saveEntityContext, relation, entity);

                // 删除已经处理的实体对象。
                if (relation.isAssociateSave())
                {
                    Collection<Object> members = relation.getMembers(entity);
                    if (members != null)
                        saveMembers(saveEntityContext, relation, entityList, members);
                }

                // 删除已经被清除的实体对象。
                if (relation.isAssociateDelete() && entityList != null)
                {
                    for (Object childEntity : entityList)
                        delete(saveEntityContext, childEntity);
                }
            }
        }
    }

    protected static void rebuild(Collection<Object> entityCollection) throws EntityException
    {
        if (entityCollection != null)
        {
            for (Object insertEntity : entityCollection)
            {
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(insertEntity.getClass());
                for (Relation relation : entityMetaData.getRelations())
                {
                    if (relation.isCollectionMember())
                    {
                        Collection<Object> memberCollection = relation.getMembers(insertEntity);
                        if (memberCollection != null && memberCollection.size() > 0)
                        {
                            synchronized (entityCollection)
                            {
                                Object[] entities = memberCollection.toArray();
                                memberCollection.clear();
                                for (Object entity : entities)
                                    memberCollection.add(entity);
                            }
                        }
                    }
                }
            }
        }
    }

    private static void saveMembers(SaveEntityContext saveEntityContext, Relation relation, List<?> entityList, Collection<Object> members)
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(relation.getMemberType());
        for (Object member : members)
        {
            Object foundEntity = null;
            if (entityList != null)
            {
                for (Object entity : entityList)
                {
                    if (entityMetaData.compareByPrimaryKey(entity, member) == 0)
                    {
                        foundEntity = entity;
                        break;
                    }
                }
                if (foundEntity != null)
                    entityList.remove(foundEntity);
            }
            if (relation.isAssociateSave())
            {
                if (foundEntity != null)
                    modify(saveEntityContext, member);
                else
                    insert(saveEntityContext, member);
            }
        }
    }

    /**
     * 主表插入后更新关联字段。
     */
    protected static void update(Object entity) throws EntityException
    {
        if (entity != null)
        {
            // 同步明细表的关联字段。
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            for (Relation relation : entityMetaData.getRelations())
            {
                if (!relation.isAssociateSave())
                    continue;

                Collection<Object> members = relation.getMembers(entity);
                if (members != null)
                    relation.update(entity, members);
            }
        }
    }
}
