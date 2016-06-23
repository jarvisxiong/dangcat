package org.dangcat.business.service.impl;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.*;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.tablename.TableName;

import java.util.List;

class BusinessServicePick<Q extends EntityBase, V extends EntityBase, F extends DataFilter> extends BusinessServiceInvoker<Q, V, F>
{
    BusinessServicePick(BusinessServiceBase<Q, V, F> businessServiceBase)
    {
        super(businessServiceBase);
    }

    protected <T extends EntityBase> List<T> execute(Class<T> classType) throws ServiceException
    {
        return this.execute(classType, null, null, null);
    }

    protected <T extends EntityBase> List<T> execute(Class<T> classType, DataFilter dataFilter) throws ServiceException
    {
        return this.execute(classType, dataFilter, null, null);
    }

    protected <T extends EntityBase> List<T> execute(Class<T> classType, DataFilter dataFilter, TableName tableName, String sqlName) throws ServiceException
    {
        if (logger.isDebugEnabled())
            logger.debug("Begin pick the entity: " + classType);

        List<T> dataList = null;
        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            FilterExpress filterExpress = dataFilter.getFilterExpress();
            OrderBy orderBy = this.getOrderBy(dataFilter, classType);
            LoadEntityContext loadEntityContext = new LoadEntityContext(classType, filterExpress, null, orderBy);
            loadEntityContext.setTableName(tableName);
            loadEntityContext.setSqlName(sqlName);

            dataList = this.getEntityManager().load(loadEntityContext);
            if (dataList != null && dataList.size() > 0)
            {
                EntityCalculator.calculate(dataList);
                EntityUtils.resetDataState(dataList);
            }
        }
        catch (EntityException e)
        {
            logger.error(this, e);
            throw new BusinessException(BusinessException.LOAD_ERROR, classType);
        }
        finally
        {
            if (logger.isDebugEnabled())
                logger.debug("End select entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return dataList;
    }

    private OrderBy getOrderBy(DataFilter dataFilter, Class<?> classType)
    {
        OrderBy orderBy = null;
        if (dataFilter != null)
        {
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
        }
        return orderBy;
    }
}
