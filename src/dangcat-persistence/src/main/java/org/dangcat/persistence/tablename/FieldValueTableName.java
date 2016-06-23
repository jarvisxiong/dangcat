package org.dangcat.persistence.tablename;

import org.dangcat.persistence.filter.FilterUtils;

import java.text.MessageFormat;

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

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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
}
