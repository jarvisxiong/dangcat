package org.dangcat.persistence.batch;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.EntityDataUtils;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TestEntityBatch extends TestEntityBase
{
    private static final int TEST_COUNT = 1000;

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();
        this.testEntityInsert();
        this.testEntityBatchManager();
        this.testTableDrop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testEntityBatchManager() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        List<EntityData> saveEntityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(TEST_COUNT, saveEntityDataList.size());

        EntityBatchStorer entityBatchStorer = new EntityBatchStorer(null);

        List<EntityData> expectEntityDataList = new ArrayList<EntityData>();
        for (int index = 0; index < TEST_COUNT; index++)
        {
            EntityData entityData = saveEntityDataList.get(index);
            if (index % 2 == 0)
                entityBatchStorer.delete(entityData);
            else
            {
                EntityDataUtils.modifyEntityData(entityData, TEST_COUNT - index);
                entityBatchStorer.save(entityData);
                expectEntityDataList.add(entityData);
            }
        }
        for (int index = 0; index < TEST_COUNT; index++)
        {
            EntityData entityData = EntityDataUtils.createEntityData(TEST_COUNT + index);
            entityBatchStorer.save(entityData);
            expectEntityDataList.add(entityData);
        }

        entityBatchStorer.save();

        List<EntityData> actualEntityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(expectEntityDataList.size(), actualEntityDataList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(expectEntityDataList, actualEntityDataList));
    }

    private void testEntityInsert() throws EntityException
    {
        List<EntityData> entityDataList = new LinkedList<EntityData>();
        EntityDataUtils.createEntityDataList(entityDataList, TEST_COUNT);

        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(entityDataList.toArray());

        // 检查数据存储正确否
        List<EntityData> saveEntityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(entityDataList.size(), saveEntityDataList.size());
    }

    private void testTableCreate() throws TableException
    {
        Table table = EntityDataUtils.getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    private void testTableDrop() throws TableException
    {
        Table table = EntityDataUtils.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }
}
