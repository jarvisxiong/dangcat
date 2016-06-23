package org.dangcat.business.log;

import org.dangcat.business.settle.SettleEntity;
import org.dangcat.business.settle.SettleUnit;
import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.persistence.tablename.DateTimeTableName;

/**
 * 操作日志结算。
 * @author fanst174766
 * 
 */
public class OperateLogSettle implements SettleUnit
{
    private DateTimeTableName sourceTableName = new DateTimeTableName(OperateLog.class.getSimpleName());

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OperateLogSettle other = (OperateLogSettle) obj;
        return this.sourceTableName.equals(other.getSourceTableName());
    }

    @Override
    public Class<? extends SettleEntity> getClassType()
    {
        return OperateStat.class;
    }

    @Override
    public DateTimeTableName getSourceTableName()
    {
        return this.sourceTableName;
    }

    @Override
    public int hashCode()
    {
        return this.sourceTableName.hashCode();
    }

    @Override
    public void merge(Object srcEntity, Object destEntity)
    {
        OperateStat srcOperateStat = (OperateStat) srcEntity;
        OperateStat destOperateStat = (OperateStat) destEntity;

        destOperateStat.setSuccess(destOperateStat.getSuccess() + srcOperateStat.getSuccess());
        destOperateStat.setFailure(destOperateStat.getFailure() + srcOperateStat.getFailure());
        destOperateStat.setMaxId(srcOperateStat.getMaxId());
    }
}