package org.dangcat.chart.jfreechart;

import java.util.Date;

import org.dangcat.commons.formator.DataFormatorFactory;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Field;
import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;

class SimulateBase
{
    protected static final String DateTime = "DateTime";
    protected static final String DownOctets = "DownOctets";
    protected static final String FieldName = "FieldName";
    protected static String[] FieldNames = new String[] { "Octets", "UpOctets", "DownOctets", "OctetsVelocity", "Percent", "Value", "Velocity", "TimeLength" };
    protected static final int LABEL_COUNT = 11;
    protected static final String Name = "Name";
    protected static final String Octets = "Octets";
    protected static final String OctetsVelocity = "OctetsVelocity";
    protected static final String Percent = "Percent";
    protected static final String TimeLength = "TimeLength";
    protected static final String UpOctets = "UpOctets";
    protected static final String Value = "Value";
    protected static final String Velocity = "Velocity";

    private static void addColumn(Table table, String fieldName, String logic, Class<?> classType)
    {
        Column column = table.getColumns().add(fieldName, classType);
        column.setLogic(logic);
        column.setDataFormator(DataFormatorFactory.getDataFormator(column.getLogic()));
    }

    protected static Table createTable(String... fieldNames)
    {
        Table table = new Table();
        for (String fieldName : fieldNames)
        {
            if (Name.equalsIgnoreCase(fieldName))
                table.getColumns().add(Name, String.class, 20, true);
            else if (FieldName.equalsIgnoreCase(fieldName))
                table.getColumns().add(FieldName, String.class, 20, true);
            else if (DateTime.equalsIgnoreCase(fieldName))
                table.getColumns().add(DateTime, Date.class);
            else if (Percent.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "percent", Double.class);
            else if (Octets.equalsIgnoreCase(fieldName) || UpOctets.equalsIgnoreCase(fieldName) || DownOctets.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "octets", Long.class);
            else if (OctetsVelocity.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "octetsVelocity", Long.class);
            else if (Value.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "value", Long.class);
            else if (Velocity.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "velocity", Long.class);
            else if (TimeLength.equalsIgnoreCase(fieldName))
                addColumn(table, fieldName, "timeLength", Long.class);
            else
                addColumn(table, fieldName, "octets", Long.class);
        }
        return table;
    }

    protected static void initField(Field field, int i, double value)
    {
        if (field != null)
            field.setDouble((i + 1) * value + Math.random() * 10);
    }

    private static void initField(Field field, int i, long value)
    {
        if (field != null)
            field.setLong((long) ((i + 1) * value + Math.random() * 10));
    }

    protected static void initRow(Row row, int i)
    {
        initField(row.getField(Percent), i, 10.0);
        initField(row.getField(Octets), i, 30l);
        initField(row.getField(UpOctets), i, 40l);
        initField(row.getField(DownOctets), i, 50l);
        initField(row.getField(OctetsVelocity), i, 60l);
        initField(row.getField(TimeLength), i, 70l);
        initField(row.getField(Value), i, 80l);
        initField(row.getField(Velocity), i, 90l);
    }

    protected static Date[] today()
    {
        Date beginTime = DateUtils.clear(DateUtils.HOUR, DateUtils.now());
        Date endTime = DateUtils.add(DateUtils.DAY, beginTime, 1);
        return new Date[] { beginTime, endTime };
    }
}
