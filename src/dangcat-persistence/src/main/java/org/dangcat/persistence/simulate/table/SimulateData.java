package org.dangcat.persistence.simulate.table;

import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.simulate.DataSimulator;
import org.dangcat.persistence.simulate.DatabaseSimulator;

public abstract class SimulateData
{
    protected DataSimulator dataSimulator = null;
    private int size;

    public abstract void clear();

    public abstract void create();

    public abstract void createDataSimulator(DatabaseSimulator databaseSimulator);

    public DataSimulator getDataSimulator()
    {
        return dataSimulator;
    }

    public int getSize()
    {
        return size;
    }

    public Table getTable()
    {
        return this.getDataSimulator().getTable();
    }

    public void initTable()
    {
        Table table = this.getTable();
        if (table.exists())
            table.drop();
        table.create();
    }

    public abstract void save();

    public void setSize(int size)
    {
        this.size = size;
    }
}
