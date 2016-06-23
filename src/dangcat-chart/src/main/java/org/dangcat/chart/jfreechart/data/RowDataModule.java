package org.dangcat.chart.jfreechart.data;

import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

public class RowDataModule extends DataModule {
    private String valueFieldName = null;

    public RowDataModule(DataReader dataReader, String[] columnKeyNames, String valueFieldName) {
        this(dataReader, null, columnKeyNames, valueFieldName);
    }

    public RowDataModule(DataReader dataReader, String[] rowKeyNames, String[] columnKeyNames, String valueFieldName) {
        super(dataReader, rowKeyNames, columnKeyNames);
        this.valueFieldName = valueFieldName;
    }

    @Override
    public String getLabel(Comparable<?> rowKey, Comparable<?> columnKey) {
        Number number = this.getValue(rowKey, columnKey);
        if (number == null)
            return null;

        StringBuilder info = new StringBuilder();
        if (!this.isNull(rowKey)) {
            info.append(this.getLabelValue(rowKey, this.getRowKeyNames()));
            info.append(" ");
        }
        if (!this.isNull(columnKey)) {
            info.append(this.getLabelValue(columnKey, this.getColumnKeyNames()));
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
    public Column getNumberColumn() {
        return this.getColumn(this.valueFieldName);
    }

    @Override
    public void initialize() {
        DataReader dataReader = this.getDataReader();
        for (int i = 0; i < dataReader.size(); i++) {
            Comparable<?> rowKey = this.getFieldsValue(i, this.getRowKeyNames());
            Comparable<?> columnKey = this.getFieldsValue(i, this.getColumnKeyNames());
            Number number = (Number) dataReader.getValue(i, this.valueFieldName);
            this.putValue(rowKey, columnKey, number);
        }
    }
}
