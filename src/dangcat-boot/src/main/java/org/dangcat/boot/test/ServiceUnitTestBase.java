package org.dangcat.boot.test;

import org.apache.log4j.Logger;
import org.dangcat.boot.service.EntityBatchService;
import org.dangcat.boot.service.TimerService;
import org.dangcat.boot.service.impl.EntityBatchServiceImpl;
import org.dangcat.boot.service.impl.EntityCacheServiceImpl;
import org.dangcat.boot.service.impl.TimerServiceImpl;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.conf.ConfigureManager;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.junit.Before;
import org.junit.BeforeClass;

import java.io.File;

public abstract class ServiceUnitTestBase extends ServiceBase {
    private static final String RESOURCE_DEFAULT_PATH = "/test-classes/META-INF/resource.properties";
    protected Logger logger = Logger.getLogger(this.getClass());

    public ServiceUnitTestBase() {
        super(null);

        // 定时器服务。
        TimerService timerService = TimerServiceImpl.createInstance(this);
        this.addService(TimerService.class, timerService);

        // 数据缓存服务。
        EntityManagerFactory.getInstance();
        EntityCacheServiceImpl entityCacheService = new EntityCacheServiceImpl(this);
        this.addService(EntityCacheServiceImpl.class, entityCacheService);
        entityCacheService.initialize();

        // 数据批量操作服务。
        EntityBatchService entityBatchService = EntityBatchServiceImpl.createInstance(this);
        this.addService(EntityBatchService.class, entityBatchService);

        this.initialize();
    }

    @BeforeClass
    public static void configure() {
        Environment.setModuleEnabled("test", true);
        String path = Environment.getHomePath() + RESOURCE_DEFAULT_PATH;
        ConfigureManager.getInstance().configure(new File(FileUtils.decodePath(path)));
    }

    protected void dropEntityTable(Class<?>... classTypes) throws Exception {
        ServiceTestUtils.dropEntityTable(classTypes);
    }

    protected void initEntityTable(Class<?>... classTypes) throws Exception {
        ServiceTestUtils.initEntityTable(classTypes);
    }

    @Before
    public void injectServices() {
        super.inject();
    }
}
