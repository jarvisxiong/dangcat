package org.dangcat.business.staff.service.impl;

import org.dangcat.business.service.QueryResult;
import org.dangcat.business.service.impl.BusinessServiceBase;
import org.dangcat.business.staff.domain.OperatorOptLog;
import org.dangcat.business.staff.filter.OperatorOptLogFilter;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.tablename.TableName;

/**
 * The service implements for Operator.
 *
 * @author dangcat
 */
public class OperateLogLoader extends BusinessServiceBase<OperatorOptLog, OperatorOptLog, OperatorOptLogFilter> {
    private TableName tableName = null;

    public OperateLogLoader(TableName tableName) {
        super(null);
        this.tableName = tableName;
    }

    @Override
    public QueryResult<OperatorOptLog> query(OperatorOptLogFilter operatorOptLogFilter) throws ServiceException {
        return super.query(operatorOptLogFilter, this.tableName, null);
    }
}
