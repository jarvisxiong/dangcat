package org.dangcat.business.service.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.business.service.DataFilter;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.tablename.TableName;

class BusinessServiceSelect<Q extends EntityBase, V extends EntityBase, F extends DataFilter, N extends Number> extends BusinessServiceInvoker<Q, V, F>
{
    BusinessServiceSelect(BusinessServiceBase<Q, V, F> businessServiceBase)
    {
        super(businessServiceBase);
    }

    private Table createValueMapTable(Class<?> classType, String[] fieldNames)
    {
        Table valueMapTable = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData.getEntityField(fieldNames[0]) != null && entityMetaData.getEntityField(fieldNames[1]) != null)
        {
            valueMapTable = new Table(entityMetaData.getTableName().getName());
            valueMapTable.getColumns().add(fieldNames[0], Integer.class);
            valueMapTable.getColumns().add(fieldNames[1], String.class);
            valueMapTable.setDatabaseName(this.getDatabaseName());
        }
        return valueMapTable;
    }

    protected Map<N, String> execute(F dataFilter) throws ServiceException
    {
        return this.execute(dataFilter, null, null);
    }

    @SuppressWarnings("unchecked")
    protected Map<N, String> execute(F dataFilter, TableName tableName, String sqlName) throws ServiceException
    {
        Class<Q> classType = this.getQueryEntityClass();

        if (this.logger.isDebugEnabled())
            this.logger.debug("Begin select the entity: " + classType);

        Map<N, String> valueMap = null;
        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            String[] fieldNames = this.businessServiceBase.getSelectFieldNames();
            Table valueMapTable = this.createValueMapTable(classType, fieldNames);
            if (valueMapTable != null)
            {
                valueMap = new LinkedHashMap<N, String>();
                if (dataFilter != null)
                {
                    Range range = new Range();
                    range.setRange(dataFilter.getStartRow(), dataFilter.getPageSize());
                    range.setCalculateTotalSize(true);
                    valueMapTable.setRange(range);
                    if (!ValueUtils.isEmpty(dataFilter.getOrderBy()))
                        valueMapTable.setOrderBy(OrderBy.parse(dataFilter.getOrderBy()));
                    valueMapTable.setFilter(dataFilter.getFilterExpress());
                }
                valueMapTable.setTableName(tableName);
                valueMapTable.setSqlName(sqlName);
                valueMapTable.load();
                for (Row row : valueMapTable.getRows())
                {
                    N key = (N) row.getField(fieldNames[0]).getObject();
                    String value = row.getField(fieldNames[1]).getString();
                    valueMap.put(key, value);
                }
            }
        }
        catch (EntityException e)
        {
            this.logger.error(this, e);
            throw new BusinessException(BusinessException.LOAD_ERROR, classType);
        }
        finally
        {
            if (this.logger.isDebugEnabled())
                this.logger.debug("End select entity, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        return valueMap;
    }
}
