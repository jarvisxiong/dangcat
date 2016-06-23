package org.dangcat.persistence.index;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.filter.FilterUtils;

class RangeComparable implements Comparable<Object> {
    private Object maxValue = null;
    private Object minValue = null;

    protected void clear() {
        this.maxValue = null;
        this.minValue = null;
    }

    @Override
    public int compareTo(Object o) {
        if (this.isCompareAble(this.minValue, o)) {
            if (ValueUtils.compare(this.minValue, o) > 0)
                return 1;
        }
        if (this.isCompareAble(this.maxValue, o)) {
            if (ValueUtils.compare(this.maxValue, o) < 0)
                return -1;
        }
        return 0;
    }

    protected void createRange(FilterUnit filterUnit) {
        FilterType filterType = FilterUtils.transFilterType(filterUnit);
        Object[] values = this.getRange(filterType, filterUnit.getParams());
        if (values[0] != null) {
            if (this.minValue == null || ValueUtils.compare(values[0], this.minValue) < 0)
                this.minValue = values[0];
        }
        if (values[1] != null) {
            if (this.maxValue == null || ValueUtils.compare(values[1], this.maxValue) > 0)
                this.maxValue = values[1];
        }
    }

    private void getBetweenRange(Object[] values, Object[] params) {
        if (params.length > 0 && params[0] != null)
            values[0] = params[0];
        if (params.length > 1 && params[1] != null)
            values[1] = params[1];
    }

    private void getEqRange(Object[] values, Object[] params) {
        for (Object value : params) {
            if (value == null)
                continue;

            if (values[0] == null || ValueUtils.compare(value, values[0]) < 0)
                values[0] = value;
            if (values[1] == null || ValueUtils.compare(value, values[1]) > 0)
                values[1] = value;
        }
    }

    private void getGeRange(Object[] values, Object[] params) {
        if (params.length > 0 && params[0] != null)
            values[0] = params[0];
    }

    private void getLeRange(Object[] values, Object[] params) {
        if (params.length > 0 && params[0] != null)
            values[1] = params[0];
    }

    private void getLikeRange(Object[] values, Object[] params) {
        if (params.length > 0 && params[0] != null) {
            if (params[0] instanceof String) {
                String value = (String) params[0];
                if (!ValueUtils.isEmpty(value) && !value.startsWith("%")) {
                    if (value.endsWith("%"))
                        value = value.substring(0, value.length() - 1);
                    byte[] from = value.getBytes();
                    from[from.length - 1] = --from[from.length - 1];
                    values[0] = new String(from);
                    byte[] to = value.getBytes();
                    to[to.length - 1] = ++to[to.length - 1];
                    values[1] = new String(to);
                }
            }
        }
    }

    private Object[] getRange(FilterType filterType, Object[] params) {
        Object[] values = new Object[2];
        if (filterType != null && params != null && params.length > 0) {
            if (FilterType.between.equals(filterType))
                getBetweenRange(values, params);
            else if (FilterType.ge.equals(filterType) || FilterType.gt.equals(filterType))
                getGeRange(values, params);
            else if (FilterType.le.equals(filterType) || FilterType.lt.equals(filterType))
                getLeRange(values, params);
            else if (FilterType.eq.equals(filterType))
                getEqRange(values, params);
            else if (FilterType.like.equals(filterType))
                getLikeRange(values, params);
        }
        return values;
    }

    private boolean isCompareAble(Object from, Object to) {
        if (from == null || to == IndexNode.NULL)
            return false;
        return to == null || from.getClass().equals(to.getClass()) || to.getClass().isAssignableFrom(from.getClass());
    }
}
