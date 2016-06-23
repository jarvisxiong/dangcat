package org.dangcat.persistence.entity.domain;

import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.simulate.EntitySimulator;

import java.util.Collection;
import java.util.List;

public class EntitySimulatorUtils<T>
{
    private Class<T> classType = null;
    private EntitySimulator entitySimulator = null;

    public EntitySimulatorUtils(Class<T> classType)
    {
        this.classType = classType;
    }

    @SuppressWarnings("unchecked")
    public T create(int index)
    {
        return (T) this.entitySimulator.create(index);
    }

    public void createList(List<T> entityList, int size)
    {
        this.entitySimulator.create(entityList, size);
    }

    public void createSimulator()
    {
        EntitySimulator entitySimulator = new EntitySimulator(this.classType);
        entitySimulator.initialize();
        this.entitySimulator = entitySimulator;
    }

    public void createTable()
    {
        this.createTable(this.classType);
    }

    protected void createTable(Class<?> classType)
    {
        Table table = EntityHelper.getEntityMetaData(classType).getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    public void dropTable()
    {
        this.dropTable(this.classType);
    }

    protected void dropTable(Class<?> classType)
    {
        Table table = EntityHelper.getEntityMetaData(classType).getTable();

        if (table.exists())
            table.drop();
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

    public EntitySimulator getEntitySimulator()
    {
        return entitySimulator;
    }

    public Table getTable()
    {
        Table table = (Table) this.entitySimulator.getTable().clone();
        if (table.getOrderBy() == null)
            table.setOrderBy(TableUtils.getOrderBy(table));
        return table;
    }

    public void modify(T entity, int index)
    {
        this.entitySimulator.modify(entity, this.entitySimulator.getSize() - index - 1);
    }

    public void modifyList(List<T> entityList)
    {
        this.entitySimulator.modify(entityList);
    }
}
