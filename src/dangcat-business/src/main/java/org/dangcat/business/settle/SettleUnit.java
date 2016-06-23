package org.dangcat.business.settle;

import org.dangcat.persistence.tablename.DateTimeTableName;

/**
 * 结算单元。
 * @author dangcat
 * 
 */
public interface SettleUnit
{
    /**
     * 结算类型。
     */
    Class<? extends SettleEntity> getClassType();

    /**
     * 来源表定义。
     */
    DateTimeTableName getSourceTableName();

    /**
     * 合并数据。
     * @param srcEntity 来源数据。
     * @param destEntity 目标数据。
     */
    void merge(Object srcEntity, Object destEntity);
}
