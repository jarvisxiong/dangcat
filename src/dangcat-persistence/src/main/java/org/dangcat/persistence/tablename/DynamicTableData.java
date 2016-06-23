package org.dangcat.persistence.tablename;

import java.util.Collection;
import java.util.HashSet;

import org.dangcat.persistence.model.Table;

public class DynamicTableData<T>
{
    private Collection<T> dataCollection = new HashSet<T>();
    private Table table = null;

    public DynamicTableData(String tableName, Table table)
    {
        this.table = table;
        table.setName(tableName);
    }

    public Collection<T> getDataCollection()
    {
        return dataCollection;
    }

    public Table getTable()
    {
        return table;
    }
}
