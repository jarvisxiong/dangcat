package org.dangcat.persistence.tablename;

/**
 * 动态表。
 * @author dangcat
 * 
 */
public interface DynamicTable extends Cloneable
{
    /**
     * 根据数据决定表的位置。
     * @param value 数据。
     * @return 表名。
     */
    String getName(Object value);
}
