package org.dangcat.persistence;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;

/**
 * 数据访问接口。
 * @author dangcat
 * 
 */
public interface DataAccess
{
    /**
     * 添加指定输入输出栏位。
     * @param fieldName 字段名。
     * @return 栏位对象。
     */
    Column addColumn(String fieldName);

    /**
     * 要读取的栏位集合。
     * @return
     */
    Columns getColumns();

    /**
     * 读取字段标题。
     * @param fieldName 字段名称。
     * @return 字段标题。
     */
    String getTitle(String fieldName);

    /**
     * 数据行数。
     * @return
     */
    int size();
}
