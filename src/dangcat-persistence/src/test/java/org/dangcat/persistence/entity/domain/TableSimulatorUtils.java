package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.model.Row;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.simulate.TableSimulator;

import java.util.Collection;

public class TableSimulatorUtils
{
    private TableSimulator tableSimulator = null;

    public void create(Table table, int size)
    {
        this.tableSimulator.create(table, size);
    }

    public void createSimulator(Table table)
    {
        TableSimulator tableSimulator = new TableSimulator(table);
        tableSimulator.initialize();
        this.tableSimulator = tableSimulator;
    }

    public void dropTables(Collection<String> tabelNames)
    {
        Table table = new Table();
        for (String name : tabelNames)
        {
            table.setName(name);
            if (table.exists())
                table.drop();
        }
    }

    public Table getTable()
    {
        Table table = (Table) this.tableSimulator.getTable().clone();
        if (table.getOrderBy() == null)
            table.setOrderBy(TableUtils.getOrderBy(table));
        return table;
    }

    public void modify(Row row, int index)
    {
        this.tableSimulator.modify(row, this.tableSimulator.getSize() - index - 1);
    }

    public void modify(Table table)
    {
        this.tableSimulator.modify(table);
    }
}
