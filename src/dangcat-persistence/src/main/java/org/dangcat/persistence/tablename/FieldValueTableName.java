package org.dangcat.persistence.tablename;

import java.text.MessageFormat;

import org.dangcat.persistence.filter.FilterUtils;

/**
 * 按时间分表名对象。
 * @author dangcat
 * 
 */
public class FieldValueTableName extends TableName implements DynamicTable
{
    private String fieldName = null;

    public FieldValueTableName(String name, String fieldName)
    {
        this(name, null, fieldName);
    }

    public FieldValueTableName(String name, String alias, String fieldName)
    {
        super(name, alias);
        this.fieldName = fieldName;
    }

    public String getFieldName()
    {
        return this.fieldName;
    }

    @Override
    public String getName(Object data)
    {
        Object value = this.getValue(data);
        return MessageFormat.format(this.getName(), value);
    }

    protected Object getValue(Object data)
    {
        return FilterUtils.getValue(data, this.getFieldName());
    }

    public void setFieldName(String fieldName)
    {
        this.fieldName = fieldName;
    }
}
