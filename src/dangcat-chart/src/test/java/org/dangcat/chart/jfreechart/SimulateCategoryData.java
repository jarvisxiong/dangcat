package org.dangcat.chart.jfreechart;

import org.dangcat.chart.jfreechart.data.ColumnDataModule;
import org.dangcat.chart.jfreechart.data.DataModule;
import org.dangcat.chart.jfreechart.data.RowDataModule;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataReader;

class SimulateCategoryData extends SimulateBase
{
    protected static DataModule createColumnDataModule(boolean reverse)
    {
        Table table = createTable(Name, Octets, UpOctets, DownOctets, OctetsVelocity, Percent, Value, Velocity, TimeLength);
        initColumnTableData(table);
        if (reverse)
            return new ColumnDataModule(new TableDataReader(table), new String[] { Name }, FieldNames);
        return new ColumnDataModule(new TableDataReader(table), FieldNames, new String[] { Name });
    }

    protected static DataModule createPieColumnDataModule()
    {
        Table table = createTable(Name, Octets, UpOctets, DownOctets, OctetsVelocity, Percent, Value, Velocity, TimeLength);
        initColumnTableData(table);
        return new ColumnDataModule(new TableDataReader(table), FieldNames);
    }

    protected static DataModule createPieRowDataModule()
    {
        Table table = createTable(Name, FieldName, Octets);
        initRowTableData(table);
        return new RowDataModule(new TableDataReader(table), new String[] { FieldName }, Octets);
    }

    protected static DataModule createRowDataModule(boolean reverse)
    {
        Table table = createTable(Name, FieldName, Octets);
        initRowTableData(table);
        if (reverse)
            return new RowDataModule(new TableDataReader(table), new String[] { Name }, new String[] { FieldName }, Octets);
        return new RowDataModule(new TableDataReader(table), new String[] { FieldName }, new String[] { Name }, Octets);
    }

    private static void initColumnTableData(Table table)
    {
        for (int i = 0; i < LABEL_COUNT; i++)
        {
            Row row = table.getRows().createNewRow();
            row.getField(Name).setString("Name " + i);
            initRow(row, i);
            table.getRows().add(row);
        }
    }

    private static void initRowTableData(Table table)
    {
        for (int i = 0; i < LABEL_COUNT; i++)
        {
            for (String fieldName : FieldNames)
            {
                Row row = table.getRows().createNewRow();
                row.getField(Name).setString("Name " + i);
                row.getField(FieldName).setString(fieldName);
                initRow(row, i);
                table.getRows().add(row);
            }
        }
    }
}
