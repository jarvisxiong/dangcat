package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.TestDatabase;
import org.dangcat.persistence.domain.UserServerBind;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestEntityInherit extends TestDatabase {
    protected static final Logger logger = Logger.getLogger(TestEntityInherit.class);
    private static final int TEST_COUNT = 1000;

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException {
        EntityInheritUtils.configure();
    }

    private void checkEntityInherit() throws EntityException {
        EntityManager entityManager = this.getEntityManager();

        List<UserServerBind> entityList1 = new ArrayList<UserServerBind>();
        EntityInheritUtils.createData(entityList1, TEST_COUNT);
        entityManager.save(entityList1.toArray());

        List<UserServerBind> entityList2 = entityManager.load(UserServerBind.class);
        Assert.assertEquals(TEST_COUNT, entityList2.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityList1, entityList2));
    }

    private EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open();
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        Table table = EntityInheritUtils.getTable();
        if (table.exists())
            table.drop();
        table.create();

        this.checkEntityInherit();

        table.drop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
