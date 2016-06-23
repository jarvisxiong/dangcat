package org.dangcat.persistence.orderby;

import java.io.Serializable;

/**
 * ÅÅÐòµ¥Ôª¡£
 * @author dangcat
 * 
 */
public class OrderByUnit implements Serializable, Comparable<OrderByUnit>
{
    private static final long serialVersionUID = 1L;
    /**
     * ×Ö¶ÎÃû³Æ¡£
     */
    private String fieldName;
    /**
     * ÅÅÐò¹«Ê½¡£
     */
    private String formula;

    /**
     * Ë÷ÒýË³Ðò¡£
     */
    private int index = 0;

    /**
     * ÅÅÐò·½Ê½¡£
     */
    private OrderByType orderByType = OrderByType.Asc;

    public OrderByUnit(String fieldName)
    {
        this.fieldName = fieldName;
        this.formula = null;
    }

    public OrderByUnit(String fieldName, OrderByType orderByType)
    {
        this(fieldName, orderByType, 0);
    }

    public OrderByUnit(String fieldName, OrderByType orderByType, int index)
    {
        this.fieldName = fieldName;
        this.orderByType = orderByType;
        this.formula = null;
        this.index = index;
    }

    public OrderByUnit(String fieldName, String formula, OrderByType orderByType)
    {
        this.fieldName = fieldName;
        this.orderByType = orderByType;
        this.formula = formula;
    }

    @Override
    public int compareTo(OrderByUnit orderByUnit)
    {
        return this.index - orderByUnit.getIndex();
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public String getFormula()
    {
        return formula;
    }

    public int getIndex()
    {
        return index;
    }

    public OrderByType getOrderByType()
    {
        return orderByType;
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }

    public void setFormula(String formula)
    {
        this.formula = formula;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public void setOrderByType(OrderByType orderByType)
    {
        this.orderByType = orderByType;
    }

    /**
     * ×ª»»ÅÅÐòÓï¾ä¡£
     */
    public String toString()
    {
        String formula = this.getFormula();
        if (formula == null || formula.equals(""))
            formula = this.getFieldName();

        if (orderByType == OrderByType.Desc)
            return formula + " DESC";
        return formula;
    }

}
