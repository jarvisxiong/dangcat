package org.dangcat.persistence.cache;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.EntityDataUtils;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;

public class TestCacheUpdate extends TestCacheBase
{
    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();

        // 测试向数据库添加数据时自动增加缓存数据。
        List<EntityData> srcEntityList = new ArrayList<EntityData>();
        EntityDataUtils.createEntityDataList(srcEntityList, TEST_COUNT);
        this.getEntityManager().save(srcEntityList.toArray());
        List<EntityData> destEntityList = this.getEntityManager().load(EntityData.class);
        Assert.assertEquals(srcEntityList.size(), destEntityList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcEntityList, destEntityList));

        MemCache<EntityData> memCache = EntityCacheManager.getInstance().getMemCache(EntityData.class);
        Assert.assertEquals(srcEntityList.size(), memCache.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcEntityList, memCache.getDataCollection()));

        // 测试向数据库删除数据时自动删除缓存数据。
        List<EntityData> deleteList = new ArrayList<EntityData>();
        for (int i = srcEntityList.size() - 1; i >= 0; i--)
        {
            if (i % 2 == 0)
            {
                deleteList.add(srcEntityList.get(i));
                srcEntityList.remove(i);
            }
        }
        this.getEntityManager().delete(deleteList.toArray());
        Assert.assertEquals(srcEntityList.size(), memCache.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(srcEntityList, memCache.getDataCollection()));

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

}
