package org.dangcat.persistence.model;

/**
 * 实体数据状态。
 * @author dangcat
 * 
 */
public interface DataStatus
{
    /**
     * 读取状态。
     * @return
     */
    DataState getDataState();

    /**
     * 设置状态。
     * @param dataState
     */
    void setDataState(DataState dataState);
}
