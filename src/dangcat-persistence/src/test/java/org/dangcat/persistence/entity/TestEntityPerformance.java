package org.dangcat.persistence.entity;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;

public class TestEntityPerformance extends TestEntityBase
{
    private static final int TEST_COUNT = 10000;
    private List<EntityData> entityList = new ArrayList<EntityData>();

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        if (this.entityList.isEmpty())
            EntityDataUtils.createEntityDataList(this.entityList, TEST_COUNT);
        SessionFactory.getInstance().setDefaultName(databaseName);

        Table table = EntityDataUtils.getTable();
        if (table.exists())
            table.drop();
        table.create();

        logger.info("Begin test database: " + databaseName);

        // ´æ´¢Êý¾Ý±í¡£
        EntityUtils.resetAutoIncrement(this.entityList);
        long beginTime = DateUtils.currentTimeMillis();
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(this.entityList.toArray());
        logger.info("\t save data cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms). count = " + TEST_COUNT + ",  velocity = "
                + (TEST_COUNT * 1000 / (DateUtils.currentTimeMillis() - beginTime)));

        beginTime = DateUtils.currentTimeMillis();
        List<EntityData> entityDataList2 = entityManager.load(EntityData.class);
        logger.info("\t load data cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
        Assert.assertEquals(TEST_COUNT, entityDataList2.size());

        beginTime = DateUtils.currentTimeMillis();
        Assert.assertTrue(SimulateUtils.compareDataCollection(this.entityList, entityDataList2));
        logger.info("\t check dat cost: " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");

        logger.info("End test database: " + databaseName);

        table.drop();
    }
}
