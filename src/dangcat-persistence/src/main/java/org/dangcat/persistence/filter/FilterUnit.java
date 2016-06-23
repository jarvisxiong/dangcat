package org.dangcat.persistence.filter;

import org.dangcat.commons.utils.CloneAble;

/**
 * 过滤表达式。
 *
 * @author dangcat
 */
public class FilterUnit implements FilterExpress, ValueObject, CloneAble<FilterExpress> {
    private static final long serialVersionUID = 1L;
    /**
     * 字段名。
     */
    private String fieldName = null;
    /**
     * 过滤类型。
     */
    private FilterType filterType = null;
    /**
     * 是否是NOT运算。
     */
    private boolean not = false;
    /**
     * 过滤值。
     */
    private Object[] params;
    /**
     * 过滤名称。
     */
    private Object value;

    public FilterUnit() {
    }

    /**
     * 构造过滤表达式。
     *
     * @param fieldName  字段名。
     * @param filterType 过滤类型。
     * @param not        取反过滤。
     * @param params     过滤参数。
     */
    public FilterUnit(String fieldName, FilterType filterType, Object... values) {
        this.fieldName = fieldName;
        this.filterType = filterType;
        this.params = values;
    }

    @Override
    public FilterExpress clone() {
        FilterUnit filterUnit = new FilterUnit(this.getFieldName(), this.getFilterType(), this.getParams());
        filterUnit.setNot(this.isNot());
        filterUnit.setValue(this.getValue());
        return filterUnit;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public FilterType getFilterType() {
        return this.filterType;
    }

    public void setFilterType(FilterType filterType) {
        this.filterType = filterType;
    }

    public Object[] getParams() {
        return this.params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    /**
     * 判断数据是否满足要求。
     *
     * @param instance 数据对象。
     * @return 满足则为true，否则为false。
     */
    public boolean isValid(Object instance) {
        Object value = FilterUtils.getValue(instance, this.getFieldName());
        if (value == null)
            value = instance;
        return this.isValidData(value);
    }

    /**
     * 判断数据行是否满足要求。
     *
     * @param field 数据字段。
     * @return 满足则为true，否则为false。
     */
    private boolean isValidData(Object value) {
        Filter filter = FilterFactory.getInstance().getFilter(this);
        if (filter != null)
            return filter.isValid(this.params, value);
        return false;
    }

    /**
     * 转成SQL表达语句。
     */
    @Override
    public String toString() {
        if (this.params != null && this.params.length > 0) {
            Filter filter = FilterFactory.getInstance().getFilter(this);
            if (filter != null)
                return filter.toSql(this.fieldName, this.params);
        }
        return null;
    }
}
