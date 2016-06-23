package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.JdbcValueUtils;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.tablename.DynamicTableData;
import org.dangcat.persistence.tablename.DynamicTableUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 实体会话。
 * @author dangcat
 * 
 */
public class EntityManagerSaveImpl extends EntityManagerTransaction
{
    private static final Logger logger = Logger.getLogger(EntityManager.class);
    private Queue<InsertEntry> insertEntityQueue = new LinkedList<InsertEntry>();
    private SaveEntityContext saveEntityContext = null;

    public EntityManagerSaveImpl(EntityManager entityManager)
    {
        this(entityManager, null);
    }

    public EntityManagerSaveImpl(EntityManager entityManager, SaveEntityContext saveEntityContext)
    {
        super(entityManager);
        this.saveEntityContext = saveEntityContext;
    }

    private void addInsertEntiry(Object entity, EntityField entityField, Object value)
    {
        InsertEntry insertEntry = new InsertEntry(entity, entityField, value);
        insertEntry.update();
        this.insertEntityQueue.add(insertEntry);
    }

    /**
     * 提交会话。
     * @param session 会话对象。
     * @throws EntityException 运行异常。
     * @throws SQLException
     */
    private void commit(Session session) throws EntityException, SQLException
    {
        String databaseName = this.getDatabaseName();
        EntityPending[] entityPendings = this.saveEntityContext.getEntityPendingQueue().toArray(new EntityPending[0]);
        if (entityPendings.length > 0)
        {
            for (int i = entityPendings.length - 1; i >= 0; i--)
            {
                EntityPending entityPending = entityPendings[i];
                Collection<Object> deletedCollection = entityPending.getDeletedCollection();
                if (deletedCollection != null && deletedCollection.size() > 0)
                {
                    EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityPending.getEntityClass());
                    EntityStatement entityStatement = entityPending.getEntityStatement(this.saveEntityContext, databaseName);
                    // 删除缓存中的实体。
                    this.executeDelete(session, entityMetaData, entityStatement, entityPending.getDeletedCollection());
                }
            }
            for (EntityPending entityPending : entityPendings)
            {
                Collection<Object> modifyCollection = entityPending.getModifiedCollection();
                if (modifyCollection != null && modifyCollection.size() > 0)
                {
                    EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityPending.getEntityClass());
                    EntityStatement entityStatement = entityPending.getEntityStatement(this.saveEntityContext, databaseName);
                    // 修改缓存中的实体。
                    this.executeModified(session, entityMetaData, entityStatement, entityPending.getModifiedCollection());
                }

                // 插入缓存中的实体。
                Collection<Object> insertCollection = entityPending.getInsertCollection();
                if (insertCollection != null && insertCollection.size() > 0)
                {
                    EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityPending.getEntityClass());
                    EntityStatement entityStatement = entityPending.getEntityStatement(this.saveEntityContext, databaseName);
                    this.executeInsert(session, entityMetaData, entityStatement, entityPending.getInsertCollection());
                }
            }
        }
    }

    /**
     * 执行提交会话对象。
     * @throws EntityException 运行异常。
     */
    public void execute() throws EntityException
    {
        this.prepare(this.saveEntityContext);
        try
        {
            if (this.saveEntityContext.size() > 0)
            {
                long beginTime = DateUtils.currentTimeMillis();
                if (logger.isDebugEnabled())
                    logger.debug("Begin execute save entities: ");

                if (this.saveEntityContext.beforeSave())
                {
                    Session session = this.beginTransaction();
                    this.commit(session);
                    this.saveEntityContext.afterSave();

                    // 回填主键自增值。
                    for (InsertEntry insertEntry : this.insertEntityQueue)
                        insertEntry.writeSequenceValue();
                    this.commit();
                }
                if (logger.isDebugEnabled())
                    logger.debug("End execute save entities cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
            }
        }
        catch (EntityException e)
        {
            this.rollback();
            this.saveEntityContext.onSaveError(e);

            throw e;
        }
        catch (Exception e)
        {
            this.rollback();
            throw new EntityException(e);
        }
        finally
        {
            this.insertEntityQueue.clear();
        }
    }

    /**
     * 删除缓存中的实体。
     * @param entityObject 实体队列。
     * @throws EntityException
     */
    private void executeDelete(Session session, EntityMetaData entityMetaData, EntityStatement entityStatement, Collection<Object> entities) throws SQLException, EntityException
    {
        if (entities == null)
            return;

        Collection<DynamicTableData<Object>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(entities, this.saveEntityContext.getTableName());
        if (dynamicTableDataCollection == null)
            this.executeDelete(session, entityMetaData, entityMetaData.getTableName().getName(), entityStatement.getDeleteSql(), entityStatement.getPrimaryKeyList(), entities);
        else
        {
            for (DynamicTableData<Object> dynamicTableData : dynamicTableDataCollection)
            {
                Table currentTable = dynamicTableData.getTable();
                if (!currentTable.exists())
                    continue;
                String currentSql = DynamicTableUtils.replaceTableName(entityStatement.getDeleteSql(), currentTable.getName());
                Collection<Object> currentCollection = dynamicTableData.getDataCollection();
                this.executeDelete(session, entityMetaData, currentTable.getName(), currentSql, entityStatement.getPrimaryKeyList(), currentCollection);
            }
        }
    }

    private void executeDelete(Session session, EntityMetaData entityMetaData, String tableName, String sql, List<String> primaryKeyList, Collection<Object> entities) throws SQLException
    {
        session.prepare(sql);
        int count = entities.size();
        for (Object entity : entities)
        {
            this.setSessinParam(session, entityMetaData, tableName, entity, primaryKeyList, 0);
            session.executeBatchUpdate(--count == 0);
            EntityEventManager.afterDelete(this.saveEntityContext, entity);
        }
    }

    /**
     * 插入缓存中的实体。
     * @param entityObject 实体队列。
     * @throws SQLException
     * @throws EntityException
     */
    private void executeInsert(Session session, EntityMetaData entityMetaData, EntityStatement entityStatement, Collection<Object> entityCollection) throws SQLException, EntityException
    {
        List<String> fieldNameList = entityStatement.getInsertFieldNameList();
        // 自增主键列表。
        List<String> generatedKeyList = entityStatement.getGeneratedKeyList();
        String[] primaryFieldNames = null;
        if (generatedKeyList != null && !generatedKeyList.isEmpty())
            primaryFieldNames = generatedKeyList.toArray(new String[0]);

        Collection<DynamicTableData<Object>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(entityCollection, this.saveEntityContext.getTableName());
        if (dynamicTableDataCollection == null)
            this.executeInsert(session, entityMetaData, entityMetaData.getTableName().getName(), entityStatement.getInsertSql(), fieldNameList, primaryFieldNames, entityCollection);
        else
        {
            for (DynamicTableData<Object> dynamicTableData : dynamicTableDataCollection)
            {
                Table currentTable = dynamicTableData.getTable();
                if (!currentTable.exists())
                    currentTable.create();
                String currentSql = DynamicTableUtils.replaceTableName(entityStatement.getInsertSql(), currentTable.getName());
                Collection<Object> currentCollection = dynamicTableData.getDataCollection();
                this.executeInsert(session, entityMetaData, currentTable.getName(), currentSql, fieldNameList, primaryFieldNames, currentCollection);
            }
        }
    }

    /**
     * 插入缓存中的实体。
     * @param entityObject 实体队列。
     * @throws SQLException
     * @throws EntityException
     */
    private void executeInsert(Session session, EntityMetaData entityMetaData, String tableName, String sql, List<String> fieldNameList, String[] primaryFieldNames, Collection<Object> entityCollection)
            throws SQLException, EntityException
    {
        if (entityCollection == null || entityCollection.size() == 0)
            return;

        // 准备表达对象。
        session.prepare(sql, primaryFieldNames);
        // 批处理队列。
        Queue<Object> batchUpdateQueue = new LinkedList<Object>();
        int count = entityCollection.size();
        Object currentEntity = null;
        try
        {
            for (Object entity : entityCollection)
            {
                if (!DataState.Insert.equals(EntityUtils.checkDataState(entity)))
                    continue;

                currentEntity = entity;
                batchUpdateQueue.add(entity);
                this.setSessinParam(session, entityMetaData, tableName, entity, fieldNameList, 0);
                int submitCount = session.executeBatchUpdate(--count == 0);
                if (submitCount > 0)
                {
                    if (primaryFieldNames != null && primaryFieldNames.length > 0)
                        this.parseData(entityMetaData, batchUpdateQueue, session.getGeneratedKeys(), primaryFieldNames);
                    batchUpdateQueue.clear();
                }
                EntityEventManager.afterInsert(this.saveEntityContext, entity);
            }
        }
        catch (SQLException e)
        {
            logger.error("Execute batch insert error : totalSize = " + entityCollection.size() + ", position = " + (entityCollection.size() - count) + ": " + e);
            if (currentEntity != null)
                logger.error(currentEntity);
            if (logger.isDebugEnabled())
            {
                for (Object entity : batchUpdateQueue)
                    logger.error(entity);
                logger.error(this, e);
            }
            throw e;
        }
    }

    /**
     * 修改缓存中的实体。
     * @param entityObject 实体队列。
     * @throws SQLException
     * @throws EntityException
     */
    private void executeModified(Session session, EntityMetaData entityMetaData, EntityStatement entityStatement, Collection<Object> entities) throws SQLException, EntityException
    {
        List<String> fieldNameList = entityStatement.getModifiedFieldNameList();
        List<String> primaryKeyList = entityStatement.getPrimaryKeyList();
        Collection<DynamicTableData<Object>> dynamicTableDataCollection = DynamicTableUtils.createDynamicTableDataCollection(entities, this.saveEntityContext.getTableName());
        if (dynamicTableDataCollection == null)
            this.executeModified(session, entityMetaData, entityMetaData.getTableName().getName(), entityStatement.getModifiedSql(), fieldNameList, primaryKeyList, entities);
        else
        {
            for (DynamicTableData<Object> dynamicTableData : dynamicTableDataCollection)
            {
                Table currentTable = dynamicTableData.getTable();
                if (!currentTable.exists())
                    continue;
                String currentSql = DynamicTableUtils.replaceTableName(entityStatement.getModifiedSql(), currentTable.getName());
                Collection<Object> currentCollection = dynamicTableData.getDataCollection();
                this.executeModified(session, entityMetaData, currentTable.getName(), currentSql, fieldNameList, primaryKeyList, currentCollection);
            }
        }
    }

    private void executeModified(Session session, EntityMetaData entityMetaData, String tableName, String sql, List<String> fieldNameList, List<String> primaryKeyList, Collection<Object> entities)
            throws SQLException, EntityException
    {
        if (entities == null)
            return;

        session.prepare(sql);
        int count = entities.size();
        for (Object entity : entities)
        {
            this.setSessinParam(session, entityMetaData, tableName, entity, fieldNameList, 0);
            this.setSessinParam(session, entityMetaData, tableName, entity, primaryKeyList, fieldNameList.size());
            session.executeBatchUpdate(--count == 0);
            EntityEventManager.afterSave(this.saveEntityContext, entity);
        }
    }

    protected SaveEntityContext getSaveEntityContext()
    {
        return this.saveEntityContext;
    }

    protected void setSaveEntityContext(SaveEntityContext saveEntityContext) {
        this.saveEntityContext = saveEntityContext;
    }

    /**
     * 解析数据结果。
     * @param row 数据行对象。
     * @param resultSet 查询结果。
     * @throws SQLException
     */
    private void parseData(EntityMetaData entityMetaData, Queue<Object> batchUpdateQueue, ResultSet resultSet, String[] fieldNames) throws SQLException
    {
        // 获取元数据。
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next())
        {
            Object entity = batchUpdateQueue.poll();
            for (int i = 0; i < columnCount; i++)
            {
                String fieldName = resultSetMetaData.getColumnLabel(i + 1);
                EntityField entityField = entityMetaData.getEntityFieldByFieldName(fieldName);
                if (entityField == null && fieldNames != null && fieldNames.length > 0)
                    entityField = entityMetaData.getEntityFieldByFieldName(fieldNames[i]);
                if (entityField != null)
                {
                    Object value = JdbcValueUtils.read(fieldName, resultSet, entityField.getClassType());
                    if (entityField.getColumn().isSequenceGeneration())
                        this.addInsertEntiry(entity, entityField, value);
                    else
                        entityField.setValue(entity, value);
                }
            }
        }
        resultSet.close();
    }

    /**
     * 设置会话参数。
     * @param session 会话对象。
     * @param entityMetaData 实体元数据。
     * @param entity 实体对象。
     * @param fieldNameList 字段列表。
     * @param beginIndex 参数起始索引。
     * @return
     * @throws SQLException
     */
    private void setSessinParam(Session session, EntityMetaData entityMetaData, String tableName, Object entity, List<String> fieldNameList, int beginIndex) throws SQLException
    {
        int index = 0;
        for (String fieldName : fieldNameList)
        {
            EntityField entityField = entityMetaData.getEntityField(fieldName);
            Object value = entityField.getValue(entity);
            // 自增字段需要手动产生
            if (value == null && entityField.getColumn().isSequenceGeneration())
            {
                value = session.nextSequence(tableName, fieldName, entityField.getClassType(), entityField.getTableGenerator());
                this.addInsertEntiry(entity, entityField, value);
            }
            session.setParam(beginIndex + index, value, entityField.getColumn());
            index++;
        }
    }
}