package org.dangcat.chart.jfreechart.data;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

public class ColumnDataModule extends DataModule
{
    private boolean reverse = false;

    public ColumnDataModule(DataReader dataReader, String[] columnKeyNames)
    {
        this(dataReader, new String[] {}, columnKeyNames);
    }

    public ColumnDataModule(DataReader dataReader, String[] rowKeyNames, String columnKeyName)
    {
        super(dataReader, new String[] { columnKeyName }, rowKeyNames);
    }

    public ColumnDataModule(DataReader dataReader, String[] rowKeyNames, String[] columnKeyNames)
    {
        super(dataReader, rowKeyNames, columnKeyNames);

        Column column = this.getDataReader().getColumns().find(columnKeyNames[0]);
        this.reverse = !(column != null && !column.isPrimaryKey() && ValueUtils.isNumber(column.getFieldClass()));
    }

    private Comparable<?> getColumnKeyValue(int columnIndex, String[] fieldNames)
    {
        Comparable<?> keyValue = NULL;
        if (fieldNames != null && columnIndex < fieldNames.length)
            keyValue = this.getDataReader().getTitle(fieldNames[columnIndex]);
        return keyValue;
    }

    @Override
    public String getLabel(Comparable<?> rowKey, Comparable<?> columnKey)
    {
        Number number = this.getValue(rowKey, columnKey);
        if (number == null)
            return null;

        StringBuilder info = new StringBuilder();
        if (!this.isNull(rowKey))
        {
            String[] fieldNames = this.reverse ? this.getRowKeyNames() : this.getColumnKeyNames();
            info.append(this.getLabelValue(rowKey, fieldNames));
            info.append(" ");
        }
        if (!this.isNull(columnKey))
        {
            String[] fieldNames = this.reverse ? this.getColumnKeyNames() : this.getRowKeyNames();
            info.append(this.getLabelValue(columnKey, fieldNames));
            info.append(" ");
        }
        Column numberColumn = this.getNumberColumn();
        info.append(this.getDataReader().getTitle(numberColumn.getName()));
        info.append(": ");

        String numberLabel = numberColumn.getDataFormator().format(number);
        info.append(numberLabel);
        return info.toString();
    }

    @Override
    public Column getNumberColumn()
    {
        return this.getColumn(this.reverse ? this.getRowKeyNames()[0] : this.getColumnKeyNames()[0]);
    }

    @Override
    public void initialize()
    {
        DataReader dataReader = this.getDataReader();
        for (int i = 0; i < dataReader.size(); i++)
        {
            if (!this.reverse)
            {
                Comparable<?> rowKey = this.getFieldsValue(i, this.getRowKeyNames());
                String[] fieldNames = this.getColumnKeyNames();
                for (int columnIndex = 0; columnIndex < fieldNames.length; columnIndex++)
                {
                    Comparable<?> columnKey = this.getColumnKeyValue(columnIndex, fieldNames);
                    Number number = (Number) dataReader.getValue(i, fieldNames[columnIndex]);
                    this.putValue(rowKey, columnKey, number);
                }
            }
            else
            {
                Comparable<?> columnKey = this.getFieldsValue(i, this.getColumnKeyNames());
                String[] fieldNames = this.getRowKeyNames();
                for (int columnIndex = 0; columnIndex < fieldNames.length; columnIndex++)
                {
                    Comparable<?> rowKey = this.getColumnKeyValue(columnIndex, fieldNames);
                    Number number = (Number) dataReader.getValue(i, fieldNames[columnIndex]);
                    this.putValue(rowKey, columnKey, number);
                }
            }
        }
    }
}
