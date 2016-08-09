package org.dangcat.persistence.orderby;

import java.io.Serializable;

/**
 * 排序单元。
 *
 * @author dangcat
 */
public class OrderByUnit implements Serializable, Comparable<OrderByUnit> {
    private static final long serialVersionUID = 1L;
    /**
     * 字段名称。
     */
    private String fieldName;
    /**
     * 排序公式。
     */
    private String formula;

    /**
     * 索引顺序。
     */
    private int index = 0;

    /**
     * 排序方式。
     */
    private OrderByType orderByType = OrderByType.Asc;

    public OrderByUnit(String fieldName) {
        this.fieldName = fieldName;
        this.formula = null;
    }

    public OrderByUnit(String fieldName, OrderByType orderByType) {
        this(fieldName, orderByType, 0);
    }

    public OrderByUnit(String fieldName, OrderByType orderByType, int index) {
        this.fieldName = fieldName;
        this.orderByType = orderByType;
        this.formula = null;
        this.index = index;
    }

    public OrderByUnit(String fieldName, String formula, OrderByType orderByType) {
        this.fieldName = fieldName;
        this.orderByType = orderByType;
        this.formula = formula;
    }

    @Override
    public int compareTo(OrderByUnit orderByUnit) {
        return this.index - orderByUnit.getIndex();
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public OrderByType getOrderByType() {
        return orderByType;
    }

    public void setOrderByType(OrderByType orderByType) {
        this.orderByType = orderByType;
    }

    /**
     * 转换排序语句。
     */
    public String toString() {
        String formula = this.getFormula();
        if (formula == null || formula.equals(""))
            formula = this.getFieldName();

        if (orderByType == OrderByType.Desc)
            return formula + " DESC";
        return formula;
    }

}
