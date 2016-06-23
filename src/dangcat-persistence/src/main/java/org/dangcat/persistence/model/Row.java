package org.dangcat.persistence.model;

import org.dangcat.persistence.ValueReader;
import org.dangcat.persistence.ValueWriter;
import org.dangcat.persistence.event.TableEventAdapter;
import org.dangcat.persistence.tablename.DynamicTable;
import org.dangcat.persistence.tablename.TableName;

import java.util.ArrayList;

/**
 * 数据行对象。
 * @author dangcat
 * 
 */
public class Row extends ArrayList<Field> implements ValueWriter, ValueReader, java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    /** 数据行状态：新增、删除或被修改。 */
    private DataState dataState = DataState.Browse;
    /** 错误信息。 */
    private String error;
    /** 数据行错误级别。 */
    private String errorLevel;
    /** 所属父表对象。 */
    private Table parent;
    /**
     * 构造函数。
     * @param parent 父表对象。
     */
    public Row()
    {
    }

    /**
     * 构造函数。
     * @param parent 父表对象。
     */
    public Row(Table parent)
    {
        this.parent = parent;
    }

    /**
     * 建立新的实例。
     *
     * @return
     */
    public static Row newInstance() {
        return new Row();
    }

    @Override
    public void clear()
    {
        for (Field field : this)
            field.setParent(null);
        super.clear();
    }

    private boolean containsNull(Object[] values)
    {
        if (values != null)
        {
            for (Object value : values)
            {
                if (value == null)
                    return true;
            }
        }
        return false;
    }

    /**
     * 从目标行拷贝相同名字的栏位。
     * @param dstRow 目标数据行。
     */
    public void copy(Row srcRow)
    {
        for (Column column : this.parent.getColumns())
        {
            Field srcField = srcRow.getField(column.getName());
            if (srcField != null)
            {
                Field dstField = this.getField(column.getName());
                dstField.setObject(srcField.getObject());
            }
        }
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o != null && o instanceof Row)
        {
            Row srcRow = (Row) o;
            if (srcRow.size() != this.size())
                return false;
            if (srcRow.getParent() != null && this.getParent() != null)
            {
                if (!this.getTableName().equals(srcRow.getTableName()))
                    return false;

                Column[] primaryKeys = this.getParent().getColumns().getPrimaryKeys();
                for (Column column : primaryKeys)
                {
                    Field srcField = this.getField(column.getName());
                    Field dstField = srcRow.getField(column.getName());
                    if (srcField == null || srcField.compareTo(dstField) != 0)
                        return false;
                }
                return true;
            }
        }
        return false;
    }

    public DataState getDataState()
    {
        return this.dataState;
    }

    public void setDataState(DataState dataState) {
        this.dataState = dataState;
        if (dataState == DataState.Browse || dataState == DataState.Insert) {
            for (Field field : this)
                field.setDataState(dataState);
        }
    }

    public String getError()
    {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getErrorLevel()
    {
        return errorLevel;
    }

    public void setErrorLevel(String errorLevel) {
        this.errorLevel = errorLevel;
    }

    /**
     * 通过栏位索引值获得单元数据对象。
     * @param index 栏位索引值。
     * @return 单元数据对象。
     */
    public Field getField(int index)
    {
        if (index >= 0 && index < this.size())
            return this.get(index);
        return null;
    }

    /**
     * 通过栏位名获得单元数据对象。
     * @param fieldName 栏位名。
     * @return 单元数据对象。
     */
    public Field getField(String fieldName)
    {
        int index = this.getParent().getColumns().indexOf(fieldName);
        if (index == -1)
            return null;
        return this.get(index);
    }

    /**
     * 读取字段对象的字段名。。
     * @param field 字段对象。
     * @return 字段名。
     */
    public String getFieldName(Field field)
    {
        int index = this.indexOf(field);
        if (index == -1)
            return null;
        return this.getParent().getColumns().get(index).getName();
    }

    public Integer getNum()
    {
        Column rowNumColumn = this.getParent().getColumns().getRowNumColumn();
        if (rowNumColumn == null)
            return null;
        return this.getValue(rowNumColumn.getName());
    }

    public void setNum(Integer value) {
        Column rowNumColumn = this.getParent().getColumns().getRowNumColumn();
        if (rowNumColumn != null)
            this.setValue(rowNumColumn.getName(), value);
    }

    public Table getParent()
    {
        return parent;
    }

    public void setParent(Table parent) {
        this.parent = parent;
    }

    /**
     * 读取主键字段数值。
     * @return 数值数组。
     */
    public Object[] getPrimaryKeyValues()
    {
        Column[] primaryKeys = this.getParent().getColumns().getPrimaryKeys();
        Object[] values = new Object[primaryKeys.length];
        for (int i = 0; i < primaryKeys.length; i++)
            values[i] = this.getField(primaryKeys[i].getName()).getObject();
        return values;
    }

    public String getTableName()
    {
        TableName tableName = TableUtils.getTableName(this);
        if (tableName instanceof DynamicTable)
            return ((DynamicTable) tableName).getName(this);
        return tableName == null ? null : tableName.getName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getValue(String name)
    {
        T value = null;
        Field field = this.getField(name);
        if (field != null)
            value = field.getObject();
        return value;
    }

    @Override
    public int hashCode()
    {
        if (this.getParent() != null)
        {
            Object[] srcPrimaryValues = this.getPrimaryKeyValues();
            if (srcPrimaryValues != null && !this.containsNull(srcPrimaryValues))
            {
                final int prime = 31;
                int result = this.getTableName() == null ? 1 : this.getTableName().hashCode();
                for (int i = 0; i < srcPrimaryValues.length; i++)
                    result = prime * result + (srcPrimaryValues[i] == null ? 0 : srcPrimaryValues[i].hashCode());
                return result;
            }
        }
        return super.hashCode();
    }

    /**
     * 通知数据行，数据对象发生了改变。
     */
    public void notify(Field field)
    {
        if (field.getDataState() == DataState.Modified)
        {
            this.dataState = DataState.Modified;
            this.parent.getRows().notify(this, field);
            for (TableEventAdapter tableEventAdapter : this.getParent().getTableEventAdapterList())
                tableEventAdapter.onFieldStateChanged(this, field);
        }
    }

    public void release()
    {
        this.clear();
        this.parent = null;
    }

    @Override
    public void setValue(String name, Object value)
    {
        Field field = this.getField(name);
        if (field != null)
            field.setObject(value);
    }

    /**
     * 输出数据行内容。
     */
    public String toString()
    {
        StringBuffer info = new StringBuffer();
        for (Field field : this)
            info.append(field + "\t");
        return info.toString();
    }
}
