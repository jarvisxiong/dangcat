package org.dangcat.persistence.filter;

/**
 * 过滤表达式。
 * @author dangcat
 * 
 */
public interface FilterExpress extends java.io.Serializable
{
    /**
     * 判断数据行是否满足要求。
     * @param value 数据对象。
     * @return 满足则为true，否则为false。
     */
    boolean isValid(Object value);
}
