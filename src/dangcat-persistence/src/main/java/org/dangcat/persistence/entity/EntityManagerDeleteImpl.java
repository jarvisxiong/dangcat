package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SqlBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * 实体删除管理器。
 * @author dangcat
 * 
 */
class EntityManagerDeleteImpl extends EntityManagerTransaction
{
    private static final Logger logger = Logger.getLogger(EntityManager.class);

    protected EntityManagerDeleteImpl(EntityManagerImpl entityManager)
    {
        super(entityManager);
    }

    /**
     * 删除指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeyValues 主键值。
     * @return 是否删除。
     * @throws TableException
     */
    protected int delete(Class<?> entityClass, Object[] primaryKeyValues) throws EntityException
    {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityClass);

        if (entityMetaData == null)
            throw new EntityException(entityClass + " is not exists.");

        if (!entityMetaData.isPrimaryKeyValueValid(primaryKeyValues))
            throw new EntityException("The primarykey is invalid.");

        List<String> primaryKeyList = new LinkedList<String>();
        for (EntityField entityField : entityMetaData.getPrimaryKeyFieldCollection())
            primaryKeyList.add(entityField.getFilterFieldName());
        return this.delete(entityClass, primaryKeyList.toArray(new String[0]), primaryKeyValues);
    }

    /**
     * 删除指定属性条件的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名称。
     * @param values 属性值。
     * @return 是否删除。
     * @throws TableException
     */
    protected int delete(Class<?> entityClass, String[] fieldNames, Object[] values) throws EntityException
    {
        if (fieldNames != null && values != null && fieldNames.length == values.length)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityClass);
            if (entityMetaData == null)
                return 0;

            FilterExpress filterExpress = entityMetaData.createFilterExpress(fieldNames, values);
            return this.delete(new DeleteEntityContext(entityClass, filterExpress));
        }
        return 0;
    }

    /**
     * 按照上下文删除实体对象。
     * @param <T> 实体类型。
     * @param deleteEntityContext 删除上下文对象。
     * @return 删除的数目。
     * @throws EntityException 运行异常。
     */
    protected int delete(DeleteEntityContext deleteEntityContext) throws EntityException
    {
        this.prepare(deleteEntityContext);
        Class<?> entityClass = deleteEntityContext.getEntityClass();
        int result = 0;
        SqlBuilder sqlBuilder = null;
        try
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityClass);
            if (entityMetaData == null || !EntityUtils.exists(entityMetaData, deleteEntityContext))
                return 0;

            EntitySqlBuilder entitySqlBuilder = new EntitySqlBuilder(entityMetaData, this.getDatabaseName(), deleteEntityContext);
            // 载入数据。
            sqlBuilder = entitySqlBuilder.buildDeleteStatement();
            if (sqlBuilder.length() > 0)
            {
                long beginTime = DateUtils.currentTimeMillis();
                if (logger.isDebugEnabled())
                    logger.debug("Begin delete the entity: " + entityClass.getClass().getName());

                if (deleteEntityContext.beforeDelete())
                {
                    // 获取会话对象。
                    Session session = this.beginTransaction();
                    result = session.executeBatch(sqlBuilder.getBatchSqlList());
                    deleteEntityContext.afterDelete();
                    this.commit();
                }
                if (logger.isDebugEnabled())
                    logger.debug("End delete the entity " + entityClass.getClass().getName() + " cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
            }
        }
        catch (EntityException e)
        {
            this.rollback();
            deleteEntityContext.onDeleteError(e);

            throw e;
        }
        catch (Exception e)
        {
            throw new EntityException(sqlBuilder.toString(), e);
        }
        return result;
    }

    /**
     * 删除指定的实体对象。
     * @param <T> 实体类型。
     * @param entity 实体对象。
     * @return 是否删除。
     * @throws TableException
     */
    protected int delete(Object... entities) throws EntityException
    {
        int result = 0;
        if (entities != null && entities.length > 0)
        {
            SaveEntityContext saveEntityContext = new SaveEntityContext();
            this.prepare(saveEntityContext);
            for (Object entity : entities)
                saveEntityContext.delete(entity);

            if (saveEntityContext.size() > 0)
            {
                EntityManagerSaveImpl entityManagerSave = this.entityManager.getEntityManagerSaveImpl();
                entityManagerSave.setSaveEntityContext(saveEntityContext);
                entityManagerSave.execute();
            }
            result = saveEntityContext.size();
        }
        return result;
    }
}
