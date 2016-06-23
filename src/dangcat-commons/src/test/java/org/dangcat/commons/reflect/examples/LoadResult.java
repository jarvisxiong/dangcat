package org.dangcat.commons.reflect.examples;

import java.util.Collection;

public class LoadResult<T>
{
    /** 当前页面记录集合 */
    private Collection<T> data = null;
    /** 当前页数,缺省为1，第一页为1，它的值必须大于等于1。 */
    private int pageNum = 1;
    /** 每页显示的记录数，缺省为10； */
    private int pageSize = 10;
    /** 查询记录总数。 */
    private int totalSize;

    public Collection<T> getData()
    {
        return data;
    }

    public void setData(Collection<T> data)
    {
        this.data = data;
    }

    public int getPageNum()
    {
        return pageNum;
    }

    public void setPageNum(int pageNum)
    {
        this.pageNum = pageNum;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public int getTotalSize()
    {
        return totalSize;
    }

    public void setTotalSize(int totalSize)
    {
        this.totalSize = totalSize;
    }

}
