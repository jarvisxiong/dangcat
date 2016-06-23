package org.dangcat.business.service.impl;

import java.util.List;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.business.service.QueryResult;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityCalculator;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.EntityUtils;
import org.dangcat.persistence.entity.LoadEntityContext;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.tablename.TableName;

class BusinessServiceQuery<Q extends EntityBase, V extends EntityBase, F extends DataFilter> extends BusinessServiceInvoker<Q, V, F>
{
    BusinessServiceQuery(BusinessServiceBase<Q, V, F> businessServiceBase)
    {
        super(businessServiceBase);
    }

    /**
     * 触发加载后事件。
     * @param queryContext 操作上下文。
     */
    private void afterQuery(QueryContext<Q> queryContext)
    {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.afterQuery(queryContext);
    }

    /**
     * 触发加载前事件。
     * @param queryContext 操作上下文。
     */
    private void beforeQuery(QueryContext<Q> queryContext) throws ServiceException
    {
        if (this.isExtendEventEnabled())
            this.businessServiceBase.beforeQuery(queryContext);
    }

    protected QueryResult<Q> execute(F dataFilter) throws ServiceException
    {
        return this.execute(dataFilter, null, null);
    }

    protected QueryResult<Q> execute(F dataFilter, TableName tableName, String sqlName) throws ServiceException
    {
        Class<Q> classType = (Class<Q>) this.getQueryEntityClass();

        if (logger.isDebugEnabled())
            logger.debug("Begin query the entity: " + classType);

        QueryResult<Q> queryResult = new QueryResult<Q>();
        QueryContext<Q> queryContext = new QueryContext<Q>(dataFilter, queryResult.getData());
        queryContext.setClassType(classType);

        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            EntityManager entityManager = this.getEntityManager();
            queryContext.setEntityManager(entityManager);

            this.beforeQuery(queryContext);

            Range range = new Range();
            range.setRange(dataFilter.getStartRow(), dataFilter.getPageSize());
            range.setCalculateTotalSize(true);

            FilterExpress filterExpress = dataFilter.getFilterExpress();
            OrderBy orderBy = this.getOrderBy(dataFilter, classType);

            LoadEntityContext loadEntityContext = new LoadEntityContext(classType, filterExpress, range, orderBy);
            loadEntityContext.setTableName(tableName);
            loadEntityContext.setSqlName(sqlName);
            List<Q> dataList = entityManager.load(loadEntityContext);
            if (range != null)
            {
                queryResult.setTotalSize(range.getTotalSize());
                queryResult.setStartRow(range.getFrom());
            }
            if (dataList != null && dataList.size() > 0)
            {
                queryResult.getData().addAll(dataList);
                EntityCalculator.calculate(dataList);
                EntityUtils.resetDataState(dataList);
            }
            this.afterQuery(queryContext);
        }
        catch (EntityException e)
        {
            logger.error(this, e);
            throw new BusinessException(BusinessException.LOAD_ERROR, classType);
        }
        finally
        {
            if (logger.isDebugEnabled())
                logger.debug("End query entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return queryResult;
    }

    private OrderBy getOrderBy(F dataFilter, Class<Q> classType)
    {
        OrderBy orderBy = null;
        if (!ValueUtils.isEmpty(dataFilter.getOrderBy()))
            orderBy = OrderBy.parse(dataFilter.getOrderBy());
        else
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            if (entityMetaData != null)
            {
                orderBy = entityMetaData.getTable().getOrderBy();
                if (orderBy == null)
                    orderBy = TableUtils.getOrderBy(entityMetaData.getTable());
            }
        }
        return orderBy;
    }
}
