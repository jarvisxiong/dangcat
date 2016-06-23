package org.dangcat.business.code.entity;

public class Column
{
    private int displaySize = 0;
    private Class<?> fieldClass = null;
    private String fieldName = null;
    private int index = -1;
    private boolean isAutoIncrement = false;
    private boolean isNullable = false;
    private boolean isPrimaryKey = false;
    private String name = null;

    public int getDisplaySize()
    {
        return displaySize;
    }

    public void setDisplaySize(int displaySize) {
        this.displaySize = displaySize;
    }

    public Class<?> getFieldClass()
    {
        return fieldClass;
    }

    public void setFieldClass(Class<?> fieldClass) {
        this.fieldClass = fieldClass;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getName()
    {
        if (this.name == null)
            this.name = TableUtils.toPropertyName(this.getFieldName());
        return this.name;
    }

    public boolean isAutoIncrement()
    {
        return isAutoIncrement;
    }

    public void setAutoIncrement(boolean isAutoIncrement)
    {
        this.isAutoIncrement = isAutoIncrement;
    }

    public boolean isNullable()
    {
        return isNullable;
    }

    public void setNullable(boolean isNullable)
    {
        this.isNullable = isNullable;
    }

    public boolean isPrimaryKey()
    {
        return isPrimaryKey;
    }

    public void setPrimaryKey(boolean isPrimaryKey)
    {
        this.isPrimaryKey = isPrimaryKey;
    }

    @Override
    public String toString()
    {
        StringBuffer info = new StringBuffer();
        info.append("Name=" + this.getName() + "\t");
        info.append("FieldName=" + this.getFieldName() + "\t");
        info.append("FieldClass=" + this.getFieldClass().getName() + "\t");
        if (this.getDisplaySize() != 0)
            info.append("DisplaySize=" + this.getDisplaySize() + "\t");
        if (this.isPrimaryKey())
            info.append("IsPrimaryKey=" + this.isPrimaryKey() + "\t");
        if (this.isAutoIncrement())
            info.append("IsAutoIncrement=" + this.isAutoIncrement() + "\t");
        if (this.isNullable())
            info.append("IsNullAble=" + this.isNullable() + "\t");
        return info.toString();
    }
}
