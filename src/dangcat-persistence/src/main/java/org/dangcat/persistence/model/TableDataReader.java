package org.dangcat.persistence.model;

import java.util.LinkedList;
import java.util.List;

import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.filter.FilterExpress;

/**
 * Table对象数据读取器。
 * @author dangcat
 * 
 */
public class TableDataReader extends TableDataAccess implements DataReader
{
    private FilterExpress filterExpress = null;
    private List<Row> rows = null;

    public TableDataReader(Table table)
    {
        super(table);
    }

    @Override
    public FilterExpress getFilterExpress()
    {
        return filterExpress;
    }

    @Override
    public Object getValue(int index, String fieldName)
    {
        List<Row> rows = this.rows;
        if (rows == null)
            rows = this.getTable().getRows();

        Object value = null;
        if (index < this.size())
        {
            Row row = rows.get(index);
            value = row.getField(fieldName).getObject();
        }
        else if (index == this.size())
        {
            Row total = this.getTable().getTotal();
            if (total != null)
                value = total.getField(fieldName).getObject();
        }
        return value;
    }

    @Override
    public void refresh()
    {
        if (this.filterExpress == null)
        {
            this.rows = null;
            return;
        }
        List<Row> rows = new LinkedList<Row>();
        for (Row row : this.getTable().getRows())
        {
            if (this.filterExpress.isValid(row))
                rows.add(row);
        }
        this.rows = rows;
    }

    @Override
    public void setFilterExpress(FilterExpress filterExpress)
    {
        this.filterExpress = filterExpress;
        this.refresh();
    }

    @Override
    public int size()
    {
        return this.rows == null ? super.size() : this.rows.size();
    }
}
