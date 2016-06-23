package org.dangcat.persistence;

import org.dangcat.persistence.filter.FilterExpress;

/**
 * 数据读取接口。
 *
 * @author dangcat
 */
public interface DataReader extends DataAccess {
    /**
     * 读取过滤条件。
     */
    FilterExpress getFilterExpress();

    /**
     * 配置过滤条件。
     */
    void setFilterExpress(FilterExpress filterExpress);

    /**
     * 指定位置的栏位数据。
     *
     * @param index     行数。
     * @param fieldName 字段名。
     * @return 数值对象。
     */
    Object getValue(int index, String fieldName);

    /**
     * 刷新数据。
     */
    void refresh();
}
