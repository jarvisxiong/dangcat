package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.ColumnDataModule;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.chart.jfreechart.data.RowDataModule;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;

import java.util.Date;

class SimulateTimeData extends SimulateBase {
    private static final int TIME_COUNT = 288;

    protected static DataModule createDiffColumnDataModule() {
        Table table = createTable(Name, DateTime, UpOctets, DownOctets);
        createTableColumnData(table);
        return new ColumnDataModule(new TableDataReader(table), new String[]{UpOctets, DownOctets}, new String[]{DateTime});
    }

    protected static DataModule createMultiColumnDataModule() {
        Table table = createTable(Name, DateTime, Octets, UpOctets, DownOctets, OctetsVelocity, Percent, Value, Velocity, TimeLength);
        createTableColumnData(table);
        return new ColumnDataModule(new TableDataReader(table), FieldNames, new String[]{DateTime});
    }

    protected static DataModule createMultiRowDataModule() {
        Table table = createTable(Name, FieldName, DateTime, Octets);
        createTableRowData(table);
        return new RowDataModule(new TableDataReader(table), new String[]{FieldName}, new String[]{DateTime}, Octets);
    }

    protected static DataModule createSingleColumnDataModule() {
        Table table = createTable(Name, DateTime, Octets);
        createTableColumnData(table);
        return new ColumnDataModule(new TableDataReader(table), new String[]{Octets}, new String[]{DateTime});
    }

    protected static DataModule createSingleRowDataModule() {
        Table table = createTable(Name, DateTime, Octets);
        createTableRowData(table);
        return new RowDataModule(new TableDataReader(table), new String[]{Name}, new String[]{DateTime}, Octets);
    }

    protected static void createTableColumnData(Table table) {
        Date beginTime = today()[0];
        for (int i = 0; i < LABEL_COUNT; i++) {
            String label = Name + " " + i;
            for (int j = 0; j <= TIME_COUNT; j++) {
                if (j >= 200 && j < 212)
                    continue;

                Row row = table.getRows().createNewRow();
                row.getField(Name).setString(label);
                Date dateTime = DateUtils.add(DateUtils.MINUTE, beginTime, j * 5);
                row.getField(DateTime).setDate(dateTime);
                initRow(row, i);
                table.getRows().add(row);
            }
        }
    }

    protected static void createTableRowData(Table table) {
        Date beginTime = today()[0];
        for (int i = 0; i < LABEL_COUNT; i++) {
            String label = Name + " " + i;
            for (int j = 0; j <= TIME_COUNT; j++) {
                if (j >= 200 && j < 212)
                    continue;

                Date dateTime = DateUtils.add(DateUtils.MINUTE, beginTime, j * 5);
                for (String fieldName : FieldNames) {
                    Row row = table.getRows().createNewRow();
                    row.getField(Name).setString(label);
                    if (row.getField(FieldName) != null)
                        row.getField(FieldName).setString(fieldName);
                    row.getField(DateTime).setDate(dateTime);
                    initRow(row, i);
                    table.getRows().add(row);
                }
            }
        }
    }
}
