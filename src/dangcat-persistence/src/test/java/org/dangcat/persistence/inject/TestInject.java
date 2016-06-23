package org.dangcat.persistence.inject;

import java.io.IOException;

import org.dangcat.framework.pool.SessionException;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.persistence.TestDatabase;
import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestInject extends TestDatabase
{
    private static final String RESOURCE_NAME = "entity.cache.xml";

    @BeforeClass
    public static void setUpBeforeClass() throws IOException, SessionException
    {
        SimulateUtils.configure();
        EntityCacheManager.getInstance().load(TestInject.class, RESOURCE_NAME);
        EntityManagerFactory.getInstance();
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
    }

    @Test
    public void testInject()
    {
        EntityService entityService = new EntityService();
        ServiceHelper.inject(null, entityService);

        Assert.assertNotNull(entityService.getAccountInfoAliasCache());
        Assert.assertNotNull(entityService.getAccountInfoCache());
        Assert.assertNotNull(entityService.getEntityDataCache());
        Assert.assertNotNull(entityService.getDefaultEntityManager());
        Assert.assertNotNull(entityService.getHsqldbEntityManager());
        Assert.assertNotNull(entityService.getMySqlEntityManager());
        Assert.assertNotNull(entityService.getOracleEntityManager());
        Assert.assertNotNull(entityService.getSqlServerEntityManager());
    }
}
