package org.dangcat.chart.jfreechart.data;

import org.dangcat.commons.formator.DataFormator;
import org.dangcat.commons.formator.ValueFormator;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public abstract class DataModule {
    protected static final String NULL = "NULL";
    private String[] columnKeyNames = null;
    private DataRange dataRange = new DataRange();
    private DataReader dataReader = null;
    private String[] rowKeyNames = null;
    private Map<Comparable<?>, RowCollection> rowMap = new HashMap<Comparable<?>, RowCollection>();
    private TransRateMap transRateMap = null;
    private ValueFormator valueFormator = null;

    public DataModule(DataReader dataReader, String[] rowKeyNames, String[] columnKeyNames) {
        this.dataReader = dataReader;
        this.rowKeyNames = rowKeyNames;
        this.columnKeyNames = columnKeyNames;
    }

    public void clear() {
        this.dataRange.clear();
        this.rowMap.clear();
    }

    protected Column getColumn(String fieldName) {
        return this.getDataReader().getColumns().find(fieldName);
    }

    public String[] getColumnKeyNames() {
        return columnKeyNames;
    }

    public Collection<Comparable<?>> getColumnKeys(Comparable<?> rowKey) {
        Collection<Comparable<?>> columnKeys = null;
        RowCollection rowCollection = this.rowMap.get(rowKey);
        if (rowCollection != null)
            columnKeys = rowCollection.getColumnKeys();
        return columnKeys;
    }

    public DataReader getDataReader() {
        return dataReader;
    }

    protected Comparable<?> getFieldsValue(int index, String[] fieldNames) {
        Comparable<?> keyValue = NULL;
        if (fieldNames != null && fieldNames.length > 0) {
            if (fieldNames.length == 1)
                return (Comparable<?>) this.dataReader.getValue(index, fieldNames[0]);

            StringBuilder info = new StringBuilder();
            for (String fieldName : fieldNames) {
                if (info.length() > 0)
                    info.append(" ");
                Object value = this.dataReader.getValue(index, fieldName);
                Column column = this.getColumn(fieldName);
                info.append(column.toString(value));
            }
            keyValue = info.toString();
        }
        return keyValue;
    }

    public abstract String getLabel(Comparable<?> rowKey, Comparable<?> columnKey);

    protected String getLabelValue(Comparable<?> keyValue, String[] fieldNames) {
        String label = null;
        if (keyValue instanceof String)
            label = (String) keyValue;
        else if (fieldNames != null && fieldNames.length > 0) {
            Column column = null;
            if (fieldNames.length == 1)
                column = this.getColumn(fieldNames[0]);
            if (column != null)
                label = column.toString(keyValue);
            if (ValueUtils.isEmpty(label))
                label = keyValue.toString();
        }
        return label;
    }

    public Number getMaxValue() {
        return this.dataRange.getMaxValue();
    }

    public void setMaxValue(Number value) {
        this.dataRange.setMaxValue(value);
    }

    public Number getMinValue() {
        return this.dataRange.getMinValue();
    }

    public void setMinValue(Number value) {
        this.dataRange.setMinValue(value);
    }

    public abstract Column getNumberColumn();

    public String[] getRowKeyNames() {
        return rowKeyNames;
    }

    public Collection<Comparable<?>> getRowKeys() {
        return this.rowMap.keySet();
    }

    public Number getRowMaxValue(Comparable<?>... rowKeys) {
        return this.dataRange.getRowMax(rowKeys);
    }

    public Number getRowMinValue(Comparable<?>... rowKeys) {
        return this.dataRange.getRowMin(rowKeys);
    }

    public TransRateMap getTransRateMap() {
        return transRateMap;
    }

    public void setTransRateMap(TransRateMap transRateMap) {
        this.transRateMap = transRateMap;
    }

    public Number getValue(Comparable<?> rowKey, Comparable<?> columnKey) {
        if (columnKey == null)
            columnKey = NULL;

        Number value = null;
        RowCollection rowCollection = this.rowMap.get(rowKey);
        if (rowCollection != null)
            value = rowCollection.getValue(columnKey);
        return value;
    }

    public ValueFormator getValueFormator() {
        if (this.valueFormator == null) {
            DataFormator dataFormator = this.getNumberColumn().getDataFormator();
            if (dataFormator instanceof ValueFormator)
                this.valueFormator = (ValueFormator) dataFormator;
        }
        return this.valueFormator;
    }

    public abstract void initialize();

    protected boolean isNull(Object value) {
        return value == null || NULL.equals(value);
    }

    protected void putValue(Comparable<?> rowKey, Comparable<?> columnKey, Number value) {
        if (value == null)
            return;

        if (this.transRateMap != null)
            value = this.transRateMap.getValue(rowKey, columnKey, value.doubleValue());

        RowCollection rowCollection = this.rowMap.get(rowKey);
        if (rowCollection == null) {
            rowCollection = new RowCollection();
            this.rowMap.put(rowKey, rowCollection);
        }

        if (columnKey == null)
            columnKey = NULL;
        rowCollection.putValue(columnKey, value);

        this.dataRange.putValue(rowKey, columnKey, value);
    }

    public void setRowMaxValue(Number value, Comparable<?>... rowKeys) {
        this.dataRange.setRowMaxValue(value, rowKeys);
    }

    public void setRowMinValue(Number value, Comparable<?>... rowKeys) {
        this.dataRange.setRowMinValue(value, rowKeys);
    }
}
