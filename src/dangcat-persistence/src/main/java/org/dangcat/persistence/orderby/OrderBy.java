package org.dangcat.persistence.orderby;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityCalculator;
import org.dangcat.persistence.filter.FilterUtils;

import java.util.*;

/**
 * 排序对象。
 * @author dangcat
 * 
 */
public class OrderBy extends ArrayList<OrderByUnit> implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    /**
     * 通过排序单元构造排序内容。
     * @param orderByUnits 排序单元数组。
     */
    public OrderBy(OrderByUnit... orderByUnits) {
        if (orderByUnits != null && orderByUnits.length > 0)
            this.addAll(Arrays.asList(orderByUnits));
    }

    /**
     * 转换字串到排序对象。
     */
    public static OrderBy parse(String orderByText)
    {
        OrderBy orderBy = null;
        if (!ValueUtils.isEmpty(orderByText))
        {
            orderByText = orderByText.trim();
            if (orderByText.toLowerCase().startsWith("order by"))
                orderByText = orderByText.substring(8);

            String[] subOrderByArray = null;
            if (orderByText.indexOf(";") > 0)
                subOrderByArray = orderByText.split(";");
            else
                subOrderByArray = orderByText.split(",");
            if (subOrderByArray != null && subOrderByArray.length > 0)
            {
                for (String subOrderBy : subOrderByArray)
                {
                    subOrderBy = subOrderBy.trim();
                    OrderByType orderByType = null;
                    String fieldName = subOrderBy;
                    if (subOrderBy.toLowerCase().endsWith(" desc"))
                    {
                        fieldName = subOrderBy.substring(0, subOrderBy.length() - 5);
                        orderByType = OrderByType.Desc;
                    }
                    else if (subOrderBy.toLowerCase().endsWith(" asc"))
                    {
                        fieldName = subOrderBy.substring(0, subOrderBy.length() - 4);
                        orderByType = OrderByType.Asc;
                    }
                    if (orderBy == null)
                        orderBy = new OrderBy();
                    if (orderByType == null)
                        orderBy.add(new OrderByUnit(fieldName));
                    else
                        orderBy.add(new OrderByUnit(fieldName, orderByType));
                }
            }
        }
        return orderBy;
    }

    /**
     * 转换字串到排序对象。
     */
    public static OrderBy parse(String fieldName, String orderByText)
    {
        OrderBy orderBy = null;
        if (!ValueUtils.isEmpty(orderByText))
        {
            orderByText = orderByText.trim();
            if (orderByText.toLowerCase().startsWith("order by"))
                orderByText = orderByText.substring(8);

            String[] subOrderByArray = null;
            if (orderByText.indexOf(";") > 0)
                subOrderByArray = orderByText.split(";");
            else
                subOrderByArray = orderByText.split(",");
            if (subOrderByArray != null && subOrderByArray.length > 0)
            {
                for (String subOrderBy : subOrderByArray)
                {
                    subOrderBy = subOrderBy.trim();
                    OrderByType orderByType = OrderByType.Desc;
                    String formula = subOrderBy;
                    if (subOrderBy.toLowerCase().endsWith(" desc"))
                        formula = subOrderBy.substring(0, subOrderBy.length() - 5);
                    else if (subOrderBy.toLowerCase().endsWith(" asc"))
                    {
                        formula = subOrderBy.substring(0, subOrderBy.length() - 4);
                        orderByType = OrderByType.Asc;
                    }
                    if (orderBy == null)
                        orderBy = new OrderBy();
                    orderBy.add(new OrderByUnit(fieldName, formula, orderByType));
                }
            }
        }
        return orderBy;
    }

    @Override
    public boolean add(OrderByUnit orderByUnit)
    {
        boolean result = super.add(orderByUnit);
        if (orderByUnit.getIndex() == 0)
            orderByUnit.setIndex(this.size());
        Collections.sort(this);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj != null)
        {
            if (obj == this)
                return true;

            String index = this.toString();
            if (index == null)
                return obj.toString() == null;
            return index.equals(obj.toString());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
        String index = this.toIndex();
        if (index != null)
            return index.hashCode();
        return super.hashCode();
    }

    /**
     * 根据当前排序对象进行排序数据行集合。
     */
    public void sort(Collection<Object> collection)
    {
        if (collection == null || collection.size() == 0)
            return;

        final OrderByUnit[] orderByUnits = this.toArray(new OrderByUnit[0]);

        List<Object> dataList = new ArrayList<Object>();
        dataList.addAll(collection);
        Collections.sort(dataList, new Comparator<Object>()
        {
            public int compare(Object srcData, Object dstData)
            {
                int result = 0;
                for (OrderByUnit orderByUnit : orderByUnits)
                {
                    Object srcValue = FilterUtils.getValue(srcData, orderByUnit.getFieldName());
                    Object dstValue = FilterUtils.getValue(srcData, orderByUnit.getFieldName());
                    result = ValueUtils.compare(srcValue, dstValue);
                    if (orderByUnit.getOrderByType().equals(OrderByType.Desc))
                        result *= -1;
                    if (result != 0)
                        break;
                }
                return result;
            }

        });
        EntityCalculator.calculateRowNum(dataList, null);
        collection.clear();
        collection.addAll(dataList);
    }

    public String toIndex()
    {
        StringBuilder index = new StringBuilder();
        for (OrderByUnit orderByUnit : this)
        {
            if (index.length() > 0)
                index.append(", ");
            index.append(orderByUnit);
        }
        if (index.length() == 0)
            return null;
        return index.toString();
    }

    /**
     * 转换排序语句。
     */
    public String toString()
    {
        String index = this.toIndex();
        return index == null ? null : "ORDER BY " + index;
    }
}
