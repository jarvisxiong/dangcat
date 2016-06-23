package org.dangcat.boot.test;

import org.apache.log4j.Logger;
import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.Launcher;
import org.dangcat.boot.service.TimerService;
import org.dangcat.boot.service.impl.EntityBatchServiceImpl;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Service;
import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.junit.AfterClass;
import org.junit.Before;

public abstract class SystemTestBase {
    protected static Logger logger = null;
    private static ServiceProvider mainServer = null;
    @Service
    private TimerService timerService;

    protected static void launcher(Class<?> classType, String serviceName) {
        if (mainServer == null) {
            logger = Logger.getLogger(classType);
            mainServer = Launcher.start(classType, serviceName, true);
        }
    }

    @AfterClass
    public static void stop() {
        ApplicationContext.getInstance().stop();
    }

    protected void dropEntityTable(Class<?>... classTypes) throws Exception {
        ServiceTestUtils.dropEntityTable(classTypes);
    }

    protected EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open();
    }

    protected ServiceProvider getMainServer() {
        return mainServer;
    }

    protected TimerService getTimerService() {
        return this.timerService;
    }

    protected void initEntityTable(Class<?>... classTypes) throws Exception {
        ServiceTestUtils.initEntityTable(classTypes);
    }

    @Before
    public void initialize() throws Exception {
        EntityCacheManager.getInstance().clear(true);
        EntityBatchServiceImpl.getInstance().clear();
        ServiceHelper.inject(this.getMainServer(), this);
    }
}
