package org.dangcat.commons.serialize.json;

/**
 * 实体数据状态。
 * @author dangcat
 * 
 */
interface DataStatus
{
    /**
     * 读取状态。
     * @return
     */
    public DataState getDataState();

    /**
     * 设置状态。
     * @param dataState
     */
    public void setDataState(DataState dataState);
}
