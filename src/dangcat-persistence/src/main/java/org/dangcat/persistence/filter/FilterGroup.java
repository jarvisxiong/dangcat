package org.dangcat.persistence.filter;

import java.util.ArrayList;
import java.util.List;

import org.dangcat.commons.utils.CloneAble;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 过滤组合。
 * @author dangcat
 * 
 */
public class FilterGroup implements FilterExpress, ValueObject, CloneAble<FilterExpress>
{
    private static final long serialVersionUID = 1L;
    /**
     * 表达组合。
     */
    private List<FilterExpress> filterExpressList = new ArrayList<FilterExpress>();
    /**
     * 组合类型。
     */
    private FilterGroupType groupType = FilterGroupType.and;
    /**
     * 过滤名称。
     */
    private Object value;

    /**
     * 构造过滤组合，默认关系是AND。
     */
    public FilterGroup()
    {
    }

    /**
     * 通过组合关系和表达式构造过滤组合。
     * @param groupType 组合关系。
     * @param filterExpresses 表达式数组。
     */
    public FilterGroup(FilterGroupType groupType, FilterExpress... filterExpresses)
    {
        this.groupType = groupType;
        this.add(filterExpresses);
    }

    /**
     * 构造过滤组合，默认关系是AND。
     * @param name 过滤组名。
     * @param groupType 组合关系。
     */
    public FilterGroup(Object value, FilterGroupType groupType)
    {
        this.value = value;
        this.groupType = groupType;
    }

    /**
     * 添加表达式。
     * @param filterExpresses 表达式数组。
     */
    public void add(FilterExpress... filterExpresses)
    {
        if (filterExpresses != null && filterExpresses.length > 0)
        {
            for (FilterExpress filterExpress : filterExpresses)
                this.filterExpressList.add(filterExpress);
        }
    }

    /**
     * 添加表达式。
     * @param fieldName 字段名。
     * @param filterType 过滤类型。
     * @param params 过滤参数。
     */
    public FilterUnit add(String fieldName, FilterType filterType, Object... params)
    {
        FilterUnit filterUnit = null;
        if (params != null && params.length > 0)
        {
            filterUnit = new FilterUnit(fieldName, filterType, params);
            this.filterExpressList.add(filterUnit);
        }
        return filterUnit;
    }

    /**
     * 清除所有表达式。
     */
    public void clear()
    {
        this.filterExpressList.clear();
    }

    @Override
    @SuppressWarnings("unchecked")
    public FilterExpress clone()
    {
        FilterGroup clone = new FilterGroup();
        clone.setGroupType(this.getGroupType());
        clone.setValue(this.getValue());
        for (FilterExpress filterExpress : this.getFilterExpressList())
            clone.getFilterExpressList().add(((CloneAble<FilterExpress>) filterExpress).clone());
        return clone;
    }

    /**
     * 判断过滤组别中是否包含过滤对象。
     * @param filterExpress 过滤对象。
     */
    public boolean contains(FilterExpress filterExpress)
    {
        return this.filterExpressList.contains(filterExpress);
    }

    public List<FilterExpress> getFilterExpressList()
    {
        return this.filterExpressList;
    }

    public FilterGroupType getGroupType()
    {
        return this.groupType;
    }

    @Override
    public Object getValue()
    {
        return this.value;
    }

    /**
     * 判断数据是否满足要求。
     * @param value 数据对象。
     * @return 满足则为true，否则为false。
     */
    public boolean isValid(Object value)
    {
        boolean isValid = true;
        for (FilterExpress filterExpress : this.filterExpressList)
        {
            if (filterExpress.isValid(value))
            {
                if (this.groupType.equals(FilterGroupType.or))
                    return true;
            }
            else
            {
                if (this.groupType.equals(FilterGroupType.and))
                    return false;
                else if (isValid)
                    isValid = false;
            }
        }
        return isValid;
    }

    public void setGroupType(FilterGroupType groupType)
    {
        this.groupType = groupType;
    }

    public void setValue(Object value)
    {
        this.value = value;
    }

    /**
     * 转成CASE表达语句。
     */
    public String toCaseExpress()
    {
        return FilterHelper.toCaseExpress(this);
    }

    /**
     * 转成SQL表达语句。
     */
    @Override
    public String toString()
    {
        int count = 0;
        StringBuilder info = new StringBuilder();
        for (FilterExpress filterExpress : this.getFilterExpressList())
        {
            String sql = filterExpress.toString();
            if (!ValueUtils.isEmpty(sql))
            {
                // 为了防止语句太长，每20个换一次行。
                if (count > 20)
                {
                    info.append(Environment.LINE_SEPARATOR);
                    count = 0;
                }

                if (info.length() > 0)
                {
                    info.append(" ");
                    info.append(this.groupType.name().toUpperCase());
                    info.append(" ");
                }
                // 如果是或运算，需要对子过滤组进行保护。
                if (this.getGroupType() == FilterGroupType.or && filterExpress instanceof FilterGroup)
                    info.append("(");

                info.append(filterExpress);

                // 如果是或运算，需要对子过滤组进行保护。
                if (this.getGroupType() == FilterGroupType.or && filterExpress instanceof FilterGroup)
                    info.append(")");

                count++;
            }
        }
        // 只有在或关系而且表达式多于一个的情况下才加括号保护。
        if (info.length() > 0 && this.getFilterExpressList().size() > 1 && this.getGroupType() == FilterGroupType.or)
        {
            info.insert(0, "(");
            info.append(")");
        }
        return info.toString();
    }
}
