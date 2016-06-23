package org.dangcat.business.test;

import org.dangcat.persistence.filter.FilterExpress;

public class QueryAssert<T>
{
    private Class<?> classType = null;
    private T dataFilter = null;
    private FilterExpress exceptFilterExpress = null;
    private Integer expectPageSize = null;
    private Integer expectStartRow = null;
    private Integer expectTotaleSize = null;

    public QueryAssert(Class<?> classType)
    {
        this.classType = classType;
    }

    public Class<?> getClassType()
    {
        return classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public T getDataFilter()
    {
        return dataFilter;
    }

    public void setDataFilter(T dataFilter) {
        this.dataFilter = dataFilter;
    }

    public FilterExpress getExceptFilterExpress()
    {
        return exceptFilterExpress;
    }

    public Integer getExpectPageSize()
    {
        return expectPageSize;
    }

    public void setExpectPageSize(Integer expectPageSize) {
        this.expectPageSize = expectPageSize;
    }

    public Integer getExpectStartRow()
    {
        return expectStartRow;
    }

    public void setExpectStartRow(Integer expectStartRow)
    {
        this.expectStartRow = expectStartRow;
    }

    public Integer getExpectTotaleSize()
    {
        return expectTotaleSize;
    }

    public void setExpectTotaleSize(Integer expectTotaleSize)
    {
        this.expectTotaleSize = expectTotaleSize;
    }

    public void setExpectFilterExpress(FilterExpress exceptFilterExpress)
    {
        this.exceptFilterExpress = exceptFilterExpress;
    }
}
