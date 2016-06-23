package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orm.JdbcValueUtils;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SqlBuilder;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体载入管理器。
 * @author dangcat
 * 
 */
class EntityManagerLoadImpl<T>
{
    private static final Logger logger = Logger.getLogger(EntityManager.class);
    /** 实体管理器。 */
    private EntityManagerImpl entityManager = null;

    EntityManagerLoadImpl(EntityManagerImpl entityManager)
    {
        this.entityManager = entityManager;
    }

    private void calculateRowNum(List<T> entityList, Range range)
    {
        Integer startRow = null;
        if (range != null)
            startRow = range.getFrom();
        EntityCalculator.calculateRowNum(entityList, startRow);
    }

    private void calculateTotalSize(LoadEntityContext loadEntityContext, EntitySqlBuilder entitySqlBuilder) throws EntityException
    {
        Range range = loadEntityContext.getRange();
        // 计算记录总数。
        if (range != null && range.isCalculateTotalSize())
        {
            int totalSize = 0;
            SqlBuilder sqlBuilder = entitySqlBuilder.buildTotalSizeStatement();
            if (sqlBuilder.length() > 0)
            {
                // 获取会话对象。
                Session session = null;
                try
                {
                    session = this.entityManager.openSession();
                    ResultSet resultSet = session.executeQuery(sqlBuilder.toString());
                    while (resultSet.next())
                        totalSize = resultSet.getInt(Range.TOTALSIZE);
                    resultSet.close();
                }
                catch (Exception e)
                {
                    if (logger.isDebugEnabled())
                        logger.error(this, e);
                    throw new EntityException(sqlBuilder.toString(), e);
                }
                finally
                {
                    if (session != null)
                        session.release();
                }
            }
            range.setTotalSize(totalSize);
        }
    }

    /**
     * 找到指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeys 主键参数。
     * @return 找到的实体对象。
     */
    protected T load(Class<T> entityClass, Object... primaryKeyValues) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityClass);

        if (entityMetaData == null)
            throw new EntityException(entityClass + " is not exists.");

        if (!entityMetaData.isPrimaryKeyValueValid(primaryKeyValues))
            throw new EntityException("The primarykey is invalid.");

        T entity = null;
        List<T> entityList = this.load(new LoadEntityContext(entityClass, primaryKeyValues));
        if (entityList != null && entityList.size() > 0)
            entity = entityList.get(0);
        return entity;
    }

    /**
     * 找到指定属性的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeys 主键参数。
     * @return 找到的实体对象。
     */
    protected List<T> load(Class<T> entityClass, String[] fieldNames, Object[] values) throws EntityException
    {
        return this.load(new LoadEntityContext(entityClass, fieldNames, values));
    }

    /**
     * 根据指定条件、范围和排序要求查找实体对象。
     * @param <T> 实体类型。
     * @param loadEntityContext 载入上下文。
     * @return 查询结果。
     */
    protected List<T> load(LoadEntityContext loadEntityContext) throws EntityException
    {
        loadEntityContext.setEntityManager(this.entityManager);
        loadEntityContext.initialize();
        List<T> entityList = null;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(loadEntityContext.getEntityClass());
            if (entityMetaData == null || !EntityUtils.exists(entityMetaData, loadEntityContext))
                return entityList;

            EntitySqlBuilder entitySqlBuilder = new EntitySqlBuilder(entityMetaData, this.entityManager.getDatabaseName(), loadEntityContext);
            // 载入数据。
            sqlBuilder = entitySqlBuilder.buildLoadStatement();
            if (sqlBuilder.length() > 0)
            {
                long beginTime = DateUtils.currentTimeMillis();
                if (logger.isDebugEnabled())
                    logger.debug("Begin load the entity " + loadEntityContext.getEntityClass().getName());
                // 计算记录总数。
                this.calculateTotalSize(loadEntityContext, entitySqlBuilder);
                if (loadEntityContext.beforeLoad())
                {
                    // 获取会话对象。
                    session = this.entityManager.openSession();
                    sqlBuilder = entitySqlBuilder.buildLoadStatement();
                    ResultSet resultSet = session.executeQuery(sqlBuilder.toString());

                    // 如果栏位没有构建需要自动生成。
                    if (entityMetaData.getTable().getColumns().size() == 0)
                        session.loadMetaData(entityMetaData.getTable(), resultSet);

                    // 解析数据结果。
                    entityList = this.parse(entityMetaData, resultSet, loadEntityContext.getRange(), loadEntityContext.getEntity());
                    loadEntityContext.setEntityCollection(entityList);
                    this.calculateRowNum(entityList, loadEntityContext.getRange());
                    loadEntityContext.afterLoad();
                }
                if (logger.isDebugEnabled())
                    logger.debug("End load entity " + loadEntityContext.getEntityClass().getName() + " cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
            }
        }
        catch (EntityException e)
        {
            loadEntityContext.onLoadError(e);
            throw e;
        }
        catch (Exception e)
        {
            if (sqlBuilder != null)
                logger.error(sqlBuilder.toString());
            if (logger.isDebugEnabled())
                logger.error(this, e);
            throw new EntityException(this.toString(), e);
        }
        finally
        {
            if (session != null)
                session.release();
        }

        // 触发载入事件。
        if (entityList != null)
        {
            for (T entity : entityList)
                RelationManager.load(loadEntityContext, entity);
        }
        return entityList;
    }

    /**
     * 由数据库刷新实体实例内容。
     * @param <T>
     * @param entity 实体对象。
     * @throws EntityException 操作异常。
     */
    protected T load(T entity) throws EntityException
    {
        T destEntity = null;
        if (entity != null)
        {
            List<T> entityList = this.load(new LoadEntityContext(entity));
            if (entityList != null && entityList.size() > 0)
                destEntity = entityList.get(0);
        }
        return destEntity;
    }

    /**
     * 解析数据结果。
     * @param row 数据行对象。
     * @param resultSet 查询结果。
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    private List<T> parse(EntityMetaData entityMetaData, ResultSet resultSet, Range range, Object sourceEntity) throws SQLException
    {
        List<T> entityList = null;
        int position = 0;
        // 获取元数据。
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int columnCount = resultSetMetaData.getColumnCount();
        while (resultSet.next())
        {
            position++;
            if (range != null && range.getMode() != Range.BY_SQLSYNTAX)
            {
                if (position < range.getFrom())
                    continue;
                else if (position > range.getTo())
                    break;
            }

            try
            {
                if (entityList == null)
                    entityList = new ArrayList<T>();

                T entity = null;
                if (sourceEntity != null && entityList.size() == 0)
                    entity = (T) sourceEntity;
                else
                    entity = (T) entityMetaData.getEntityClass().newInstance();
                for (int i = 1; i <= columnCount; i++)
                {
                    String fieldName = resultSetMetaData.getColumnLabel(i);
                    EntityField entityField = entityMetaData.getEntityFieldByFieldName(fieldName);
                    if (entityField != null)
                    {
                        Object value = JdbcValueUtils.read(fieldName, resultSet, entityField.getClassType());
                        entityField.setValue(entity, value);
                    }
                }
                // 设置数据状态。
                if (entity instanceof DataStatus)
                {
                    DataStatus dataStatus = (DataStatus) entity;
                    dataStatus.setDataState(DataState.Browse);
                }
                entityList.add(entity);
            }
            catch (InstantiationException e)
            {
            }
            catch (IllegalAccessException e)
            {
            }
        }
        return entityList;
    }
}
