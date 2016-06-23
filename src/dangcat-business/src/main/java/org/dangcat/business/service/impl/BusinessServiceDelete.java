package org.dangcat.business.service.impl;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.LoadEntityContext;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.tablename.TableName;

import java.util.List;

class BusinessServiceDelete<Q extends EntityBase, V extends EntityBase, K extends DataFilter> extends BusinessServiceInvoker<Q, V, K>
{
    BusinessServiceDelete(BusinessServiceBase<Q, V, K> businessServiceBase)
    {
        super(businessServiceBase);
    }

    /**
     * 触发删除后事件。
     * @param deleteContext 操作上下文。
     */
    private void afterDelete(LoadContext<V> deleteContext)
    {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.afterDelete(deleteContext);
    }

    /**
     * 触发删除前事件。
     * @param deleteContext 操作上下文。
     */
    private void beforeDelete(LoadContext<V> deleteContext) throws ServiceException
    {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.beforeDelete(deleteContext);
    }

    protected boolean execute(Object primaryKeyValue) throws ServiceException
    {
        return this.execute(null, null, primaryKeyValue);
    }

    protected boolean execute(TableName tableName, String sqlName, Object... primaryKeyValues) throws ServiceException
    {
        Class<V> classType = this.getViewEntityClass();

        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin delete the entity: " + classType);

        LoadContext<V> deleteContext = new LoadContext<V>(primaryKeyValues);
        deleteContext.setClassType(classType);

        boolean result = false;
        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            EntityManager entityManager = this.getEntityManager();
            LoadEntityContext loadEntityContext = new LoadEntityContext(classType, primaryKeyValues);
            loadEntityContext.setTableName(tableName);
            loadEntityContext.setSqlName(sqlName);
            List<V> dataList = entityManager.load(loadEntityContext);
            if (dataList != null && dataList.size() > 0)
            {
                deleteContext.setData(dataList.get(0));
                deleteContext.setEntityManager(entityManager);

                this.beforeDelete(deleteContext);
                entityManager.delete(deleteContext.getData());
                this.afterDelete(deleteContext);
                result = true;
            }
        }
        catch (EntityException e)
        {
            this.logger.error(this, e);
            throw new BusinessException(BusinessException.DELETE_ERROR, classType);
        }
        finally
        {
            if (this.logger.isDebugEnabled())
                this.logger.debug("End delete entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return result;
    }
}
