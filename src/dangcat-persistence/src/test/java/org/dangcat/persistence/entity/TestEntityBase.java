package org.dangcat.persistence.entity;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.TestDatabase;
import org.dangcat.persistence.model.TableDataUtils;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.BeforeClass;

public abstract class TestEntityBase extends TestDatabase
{
    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException
    {
        SimulateUtils.configure();
        EntityDataUtils.createEntitySimulator();
        TableDataUtils.createTableSimulator();
    }

    protected Logger logger = Logger.getLogger(this.getClass());

    protected EntityManager getEntityManager()
    {
        return EntityManagerFactory.getInstance().open();
    }
}
