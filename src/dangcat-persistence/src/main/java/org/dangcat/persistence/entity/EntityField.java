package org.dangcat.persistence.entity;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.orm.TableGenerator;
import org.dangcat.persistence.tablename.TableName;

/**
 * ×Ö¶ÎÐÅÏ¢¡£
 * @author dangcat
 * 
 */
public class EntityField
{
    private Class<?> classType = null;
    private Column column;
    private Field field;
    private boolean isJoin = false;
    private PropertyDescriptor propertyDescriptor = null;
    private TableGenerator tableGenerator;

    public EntityField(Column column)
    {
        this.column = column;
    }

    public Class<?> getClassType()
    {
        return classType;
    }

    public Column getColumn()
    {
        return column;
    }

    public Field getField()
    {
        return field;
    }

    public String getFieldName()
    {
        return this.column == null ? null : this.column.getFieldName();
    }

    public String getFilterFieldName()
    {
        StringBuilder info = new StringBuilder();
        if (this.getTableName() != null)
        {
            if (!ValueUtils.isEmpty(this.getTableName().getAlias()))
                info.append(this.getTableName().getAlias());
            else if (!ValueUtils.isEmpty(this.getTableName().getName()))
                info.append(this.getTableName().getName());
            info.append(".");
        }
        info.append(this.getFieldName());
        return info.toString();
    }

    public Type getGenericType()
    {
        Type genericType = null;
        if (this.propertyDescriptor != null)
        {
            Method readMethod = this.propertyDescriptor.getReadMethod();
            if (readMethod != null)
                genericType = readMethod.getGenericReturnType();
        }
        else if (this.field != null)
            genericType = this.field.getGenericType();
        return genericType;
    }

    public String getName()
    {
        return this.column == null ? null : this.column.getName();
    }

    public PropertyDescriptor getPropertyDescriptor()
    {
        return propertyDescriptor;
    }

    public TableGenerator getTableGenerator()
    {
        return tableGenerator;
    }

    public TableName getTableName()
    {
        return this.column.getTableName();
    }

    public Object getValue(Object instance)
    {
        Object value = null;
        if (instance != null)
        {
            try
            {
                if (this.propertyDescriptor != null)
                {
                    Method readMethod = this.propertyDescriptor.getReadMethod();
                    if (readMethod != null)
                        value = readMethod.invoke(instance);
                }
                else if (this.field != null)
                    value = this.field.get(instance);
            }
            catch (Exception e)
            {
            }
        }
        return value;

    }

    public boolean isJoin()
    {
        return isJoin;
    }

    public boolean isRowNum()
    {
        return this.column.isRowNum();
    }

    protected void setField(Field field)
    {
        this.field = field;
        this.field.setAccessible(true);
        this.classType = field.getType();
    }

    protected void setFieldName(String fieldName)
    {
        if (this.column != null)
            this.column.setFieldName(fieldName);
    }

    public void setJoin(boolean isJoin)
    {
        this.isJoin = isJoin;
    }

    protected void setPropertyDescriptor(PropertyDescriptor propertyDescriptor)
    {
        this.propertyDescriptor = propertyDescriptor;
        this.classType = propertyDescriptor.getPropertyType();
    }

    public void setTableGenerator(TableGenerator tableGenerator)
    {
        this.tableGenerator = tableGenerator;
    }

    protected void setTableName(TableName tableName)
    {
        this.column.setTableName(tableName);
    }

    public void setValue(Object instance, Object value)
    {
        if (instance != null)
        {
            try
            {
                if (this.propertyDescriptor != null)
                {
                    Method writeMethod = this.propertyDescriptor.getWriteMethod();
                    if (writeMethod != null)
                        writeMethod.invoke(instance, value);
                }
                else if (this.field != null)
                    this.field.set(instance, value);
            }
            catch (Exception e)
            {
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(this.getFilterFieldName());
        if (!this.getFieldName().equalsIgnoreCase(this.getName()))
        {
            info.append(" ");
            info.append(this.getName());
        }
        return info.toString();
    }
}
