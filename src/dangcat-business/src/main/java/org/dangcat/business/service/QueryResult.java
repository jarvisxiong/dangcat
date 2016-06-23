package org.dangcat.business.service;

import java.util.ArrayList;
import java.util.Collection;

/**
 * 执行结果
 * @author dangcat
 * 
 */
public class QueryResult<T>
{
    /** 数据集合。 */
    private Collection<T> dataCollection = new ArrayList<T>();
    /** 起始位置。 */
    private Integer startRow = null;
    /** 总记录数。 */
    private Integer totalSize = null;

    public Collection<T> getData()
    {
        return this.dataCollection;
    }

    public Integer getStartRow()
    {
        return startRow;
    }

    public void setStartRow(Integer startRow)
    {
        this.startRow = startRow;
    }

    public Integer getTotalSize()
    {
        return totalSize;
    }

    public void setTotalSize(Integer totalSize)
    {
        this.totalSize = totalSize;
    }
}
