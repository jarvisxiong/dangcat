package org.dangcat.persistence;

/**
 * 数据写入接口。
 * @author dangcat
 * 
 */
public interface DataWriter extends DataAccess
{
    /**
     * 写入指定位置的栏位数据。
     * @param index 行数。
     * @param fieldName 字段名。
     * @return 数值对象。
     */
    public void setValue(int index, String fieldName, Object value);
}
