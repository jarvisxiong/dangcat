package org.dangcat.persistence.model;

import org.dangcat.persistence.exception.TableException;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据表集合。
 * @author dangcat
 * 
 */
public class DataSet implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    private List<Table> tables = new ArrayList<Table>();

    /**
     * 增加数据表对象。
     */
    public void add(Table table)
    {
        if (!tables.contains(table))
            tables.add(table);
    }

    /**
     * 数据表集合。
     */
    public List<Table> getTables()
    {
        return this.tables;
    }

    /**
     * 载入数据。
     */
    public void load() throws TableException
    {
        for (Table table : this.tables)
            table.load();
    }

    /**
     * 删除数据表对象。
     */
    public void remove(Table table)
    {
        if (tables.contains(table))
            tables.remove(table);
    }

    /**
     * 存储数据。
     */
    public void save() throws TableException
    {
        for (Table table : this.tables)
            table.save();
    }
}
