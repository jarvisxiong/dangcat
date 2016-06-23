package org.dangcat.persistence.simulate.table;

import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.TableSimulator;

public abstract class TableData extends SimulateData
{
    private Table table = null;

    @Override
    public void clear()
    {
        this.table.getRows().clear();
    }

    @Override
    public void create()
    {
        TableSimulator tableSimulator = (TableSimulator) this.getDataSimulator();
        tableSimulator.create(this.getTable(), this.getSize());
    }

    @Override
    public void createDataSimulator(DatabaseSimulator databaseSimulator)
    {
        TableSimulator tableSimulator = new TableSimulator(this.getTable());
        tableSimulator.setDatabaseSimulator(databaseSimulator);
        tableSimulator.initialize();
        this.initTableSimulator(tableSimulator);
        this.dataSimulator = tableSimulator;
    }

    protected abstract Table createTable();

    public Table getTable()
    {
        if (this.table == null)
            this.table = this.createTable();
        return this.table;
    }

    protected abstract void initTableSimulator(TableSimulator tableSimulator);

    @Override
    public void save()
    {
        this.getTable().save();
    }
}
