package org.dangcat.persistence;

/**
 * 数值读取器。
 * @author dangcat
 * 
 */
public interface ValueReader
{
    /**
     * 读取指定字段的数值。
     * @param <T> 数值类型。
     * @param name 字段名称。
     * @return 读取的数值。
     */
    <T> T getValue(String name);
}
