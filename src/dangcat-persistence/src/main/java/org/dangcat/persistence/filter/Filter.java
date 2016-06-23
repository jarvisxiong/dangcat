package org.dangcat.persistence.filter;

/**
 * 过滤器。
 * @author dangcat
 * 
 */
interface Filter
{
    /**
     * 校验数值是否有效。
     * @param values 数值数组。
     * @param value 比较对象。
     * @return 是否有效。
     */
    boolean isValid(Object[] values, Object value);

    /**
     * 转换成SQL表达式。
     * @param fieldName 字段名。
     * @param values 数值数组。
     * @return 表达式。
     */
    String toSql(String fieldName, Object[] values);
}
