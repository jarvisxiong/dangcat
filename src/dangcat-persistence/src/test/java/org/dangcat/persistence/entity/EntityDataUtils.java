package org.dangcat.persistence.entity;

import java.util.List;

import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.dangcat.persistence.simulate.data.NumberSimulator;
import org.dangcat.persistence.simulate.data.StringSimulator;

public class EntityDataUtils
{
    private static EntitySimulator entitySimulator = null;

    public static EntityData createEntityData(int index)
    {
        return (EntityData) entitySimulator.create(index);
    }

    public static void createEntityDataList(List<EntityData> entityList, int size)
    {
        entitySimulator.create(entityList, size);
    }

    public static void createEntitySimulator()
    {
        EntitySimulator entitySimulator = new EntitySimulator(EntityData.class);
        entitySimulator.initialize();
        // FieldA
        StringSimulator filedASimulator = entitySimulator.findValueSimulator(EntityData.FieldA);
        filedASimulator.setPrefix("A-");
        // FieldC
        NumberSimulator filedCSimulator = entitySimulator.findValueSimulator(EntityData.FieldC);
        filedCSimulator.setStep(3.14);
        // FieldD
        NumberSimulator filedDSimulator = entitySimulator.findValueSimulator(EntityData.FieldD);
        filedDSimulator.setStep(100000000l);

        EntityDataUtils.entitySimulator = entitySimulator;
    }

    public static Table getTable()
    {
        Table table = (Table) entitySimulator.getTable().clone();
        if (table.getOrderBy() == null)
            table.setOrderBy(TableUtils.getOrderBy(table));
        return table;
    }

    public static void modifyEntityData(EntityData entityData, int index)
    {
        entitySimulator.modify(entityData, entitySimulator.getSize() - index - 1);
    }

    public static void modifyEntityDataList(List<EntityData> entityList)
    {
        entitySimulator.modify(entityList);
    }

    public static void setSize(int size)
    {
        entitySimulator.setSize(size);
    }
}
