package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

import java.util.List;

public class TestMultiThreadSave extends TestEntityBase
{
    private static final int TEST_COUNT = 1000;
    private static final int THREAD_COUNT = 10;

    private void assertResult()
    {
        EntityManager entityManager = this.getEntityManager();
        List<EntityData> entities = entityManager.load(EntityData.class);
        Assert.assertNotNull(entities);
        Assert.assertEquals(TEST_COUNT * THREAD_COUNT, entities.size());
    }

    private MultiThreadSaver[] createMultiThreadSavers()
    {
        EntityManager entityManager = this.getEntityManager();
        MultiThreadSaver[] threadSavers = new MultiThreadSaver[THREAD_COUNT];
        for (int i = 0; i < threadSavers.length; i++)
            threadSavers[i] = new MultiThreadSaver(entityManager, TEST_COUNT);
        return threadSavers;
    }

    private void executeSave(MultiThreadSaver[] threadSavers)
    {
        for (int i = 0; i < threadSavers.length; i++)
            threadSavers[i].start();

        boolean finishedAll = false;
        do
        {
            try
            {
                Thread.sleep(1000l);
                finishedAll = true;
                for (int i = 0; i < threadSavers.length; i++)
                {
                    if (!threadSavers[i].isFinished())
                    {
                        finishedAll = false;
                        break;
                    }
                }
            }
            catch (InterruptedException e)
            {
            }
        } while (!finishedAll);
    }

    private void initTable()
    {
        EntityDataUtils.setSize(TEST_COUNT * THREAD_COUNT);
        Table table = EntityDataUtils.getTable();
        if (table.exists())
            table.drop();
        // 产生新的数据表
        table.create();
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        this.logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.initTable();

        MultiThreadSaver[] threadSavers = this.createMultiThreadSavers();
        this.executeSave(threadSavers);
        this.assertResult();

        this.logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
