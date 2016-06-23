package org.dangcat.business.staff.service;

import org.dangcat.business.service.QueryResult;
import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.business.staff.domain.OperatorOptLog;
import org.dangcat.business.staff.filter.OperateLogFilter;
import org.dangcat.business.staff.filter.OperatorOptLogFilter;
import org.dangcat.commons.reflect.Parameter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.annotation.JndiName;

/**
 * The service interface for OperateLog.
 * @author dangcat
 * 
 */
@JndiName(module = "Staff", name = "OperateLog")
public interface OperateLogService
{
    /**
     * 查询指定条件的数据。
     * @param operatorOptLogFilter 查询条件。
     * @param month 查询月份。
     * @return 查询结果。
     */
    public QueryResult<OperatorOptLog> load(@Parameter(name = "operatorOptLogFilter") OperatorOptLogFilter operatorOptLogFilter) throws ServiceException;

    /**
     * 查询指定条件的数据。
     * @param operateLogFilter 查询条件。
     * @return 查询结果。
     */
    public QueryResult<OperateStat> query(@Parameter(name = "operateLogFilter") OperateLogFilter operateLogFilter) throws ServiceException;

    /**
     * 查看指定主键的数据。
     * @param id 主键值。
     * @param month 查询月份。
     * @return 查看结果。
     */
    public OperateStat view(@Parameter(name = "id") Integer id, @Parameter(name = "month") Integer month) throws ServiceException;
}
