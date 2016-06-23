package org.dangcat.persistence.model;

import org.dangcat.persistence.DataWriter;

/**
 * Table对象数据读取器。
 * @author dangcat
 * 
 */
public class TableDataWriter extends TableDataAccess implements DataWriter
{
    public TableDataWriter(Table table)
    {
        super(table);
    }

    @Override
    public void setValue(int index, String fieldName, Object value)
    {
        Row row = null;
        Rows rows = this.getTable().getRows();
        if (index < this.size())
            row = rows.get(index);
        else
        {
            row = rows.createNewRow();
            rows.add(row);
        }
        Field field = row.getField(fieldName);
        if (field != null)
            field.setObject(value);
    }
}
