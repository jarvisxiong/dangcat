package org.dangcat.business.staff.service.impl;

import org.dangcat.business.security.BusinessPermissionProvider;
import org.dangcat.business.service.QueryResult;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.business.staff.domain.OperatorOptLog;
import org.dangcat.business.staff.filter.OperateLogFilter;
import org.dangcat.business.staff.filter.OperatorOptLogFilter;
import org.dangcat.business.staff.service.OperateLogService;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.PermissionProvider;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.tablename.TableName;

/**
 * The service implements for Operator.
 * @author dangcat
 * 
 */
@PermissionProvider(BusinessPermissionProvider.class)
public class OperateLogServiceImpl extends BusinessServiceBase<OperateStat, OperateStat, OperateLogFilter> implements OperateLogService
{
    public OperateLogServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    protected OperateLogFilter filter(OperateLogFilter operateLogFilter) throws ServiceException
    {
        OperatorGroupLoader operatorGroupLoader = new OperatorGroupLoader(this.getEntityManager());
        Integer[] groupIds = operatorGroupLoader.loadMemberIds();
        if (groupIds != null)
        {
            if (operateLogFilter == null)
                operateLogFilter = new OperateLogFilter();
            operateLogFilter.setGroupIds(groupIds);
        }
        return operateLogFilter;
    }

    @Override
    public QueryResult<OperatorOptLog> load(OperatorOptLogFilter operatorOptLogFilter) throws ServiceException
    {
        TableName tableName = TableUtils.getDateTimeTableName(OperateLog.class, DateUtils.MONTH, operatorOptLogFilter.getMonth());
        return new OperateLogLoader(tableName).query(operatorOptLogFilter);
    }

    @Override
    public QueryResult<OperateStat> query(OperateLogFilter operatorOptLogFilter) throws ServiceException
    {
        TableName tableName = TableUtils.getDateTimeTableName(OperateStat.class, DateUtils.MONTH, operatorOptLogFilter.getMonth());
        return super.query(operatorOptLogFilter, tableName, null);
    }

    @Override
    public OperateStat view(Integer id, Integer month) throws ServiceException
    {
        TableName tableName = TableUtils.getDateTimeTableName(OperateStat.class, DateUtils.MONTH, month);
        return super.view(tableName, null, id);
    }
}
