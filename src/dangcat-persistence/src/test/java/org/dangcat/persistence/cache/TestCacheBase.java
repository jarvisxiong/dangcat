package org.dangcat.persistence.cache;

import java.io.IOException;

import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.TestDatabase;
import org.dangcat.persistence.entity.EntityDataUtils;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableDataUtils;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class TestCacheBase extends TestDatabase
{
    private static final String RESOURCE_NAME = "entity.cache.xml";
    protected static final int TEST_COUNT = 10;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException
    {
        SimulateUtils.configure();
        EntityCacheManager.getInstance();
        EntityDataUtils.createEntitySimulator();
        TableDataUtils.createTableSimulator();
    }

    @Before
    public void beforeTest() throws TableException
    {
        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        entityCacheManager.clear(true);
        entityCacheManager.load(TestCacheBase.class, RESOURCE_NAME);
    }

    protected EntityManager getEntityManager()
    {
        return EntityManagerFactory.getInstance().open();
    }

    protected void testTableCreate() throws TableException
    {
        Table table = TableDataUtils.getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    protected void testTableDrop() throws TableException
    {
        Table table = TableDataUtils.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }
}
