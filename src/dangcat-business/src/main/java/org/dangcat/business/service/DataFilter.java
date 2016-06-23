package org.dangcat.business.service;

import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;

/**
 * 查询参数。
 *
 * @author dangcat
 */
public abstract class DataFilter {
    private static final int DEFAULT_PAGESIZE = 100;
    private static final int DEFAULT_STARTROW = 0;
    /**
     * 排序方式。
     */
    private String orderBy = null;
    /**
     * 每页大小。
     */
    private Integer pageSize = null;
    /**
     * 起始位置。
     */
    private Integer startRow = null;

    public abstract FilterExpress getFilterExpress();

    protected FilterExpress getFilterExpress(FilterExpress filterExpress) {
        if (filterExpress instanceof FilterGroup) {
            FilterGroup filterGroup = (FilterGroup) filterExpress;
            if (filterGroup.getFilterExpressList().size() == 0)
                return null;
            if (filterGroup.getFilterExpressList().size() == 1)
                return this.getFilterExpress(filterGroup.getFilterExpressList().get(0));
        }
        return filterExpress;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public Integer getPageSize() {
        return this.pageSize == null ? DEFAULT_PAGESIZE : this.pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getStartRow() {
        return this.startRow == null ? DEFAULT_STARTROW : this.startRow;
    }

    public void setStartRow(Integer startRow) {
        this.startRow = startRow;
    }
}
