package org.dangcat.business.staff.filter;

import org.dangcat.business.service.DataFilter;
import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;

/**
 * The service filter for Operator.
 * @author dangcat
 * 
 */
@Table("OperatorInfo")
public class OperatorOptLogFilter extends DataFilter
{
    @Column
    private Integer month = null;
    @Column
    private Integer operatorId = null;

    @Override
    @Serialize(ignore = true)
    public FilterExpress getFilterExpress()
    {
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(OperateLog.OperatorId, FilterType.eq, this.getOperatorId()));
        return this.getFilterExpress(filterGroup);
    }

    public Integer getMonth()
    {
        return month;
    }

    public Integer getOperatorId()
    {
        return operatorId;
    }

    public void setMonth(Integer month)
    {
        this.month = month;
    }

    public void setOperatorId(Integer operatorId)
    {
        this.operatorId = operatorId;
    }
}
