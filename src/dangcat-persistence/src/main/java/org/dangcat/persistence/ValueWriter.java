package org.dangcat.persistence;

/**
 * 数值写入器。
 * @author dangcat
 * 
 */
public interface ValueWriter
{
    /**
     * 写入指定字段的数值。
     * @param <T> 数值类型。
     * @param name 字段名称。
     * @param value 字段数值。
     */
    void setValue(String name, Object value);
}
