package org.dangcat.persistence.index;

import junit.framework.Assert;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.cache.MemCache;
import org.dangcat.persistence.cache.xml.Cache;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestUpdateMemIndex extends TestEntityBase {
    private static final int TEST_COUNT = 100;

    private void createCache() {
        Cache cache = new Cache();
        cache.getIndexList().add(ValueUtils.join(UserInfo.Name, UserInfo.Age));
        cache.setPreload(true);
        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        entityCacheManager.addCache(UserInfo.class, cache);
        entityCacheManager.loadData();
        MemCache<UserInfo> memCache = EntityCacheManager.getInstance().getMemCache(UserInfo.class);
        Assert.assertEquals(TEST_COUNT, memCache.size());
    }

    private void createData() {
        List<UserInfo> entityList = new ArrayList<UserInfo>();
        UserInfoUtils.createData(entityList, 100);
        for (UserInfo userInfo : entityList)
            userInfo.setId(null);
        this.getEntityManager().save(entityList.toArray());
    }

    private void createTable() throws TableException {
        Table table = UserInfoUtils.getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    private void removeCache() {
        EntityCacheManager.getInstance().removeCache(UserInfo.class);
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        this.logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.createTable();
        this.createData();
        this.createCache();
        this.updateEntity();
        this.removeCache();

        this.logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void updateEntity() {
        UserInfo userInfo10 = this.getEntityManager().load(UserInfo.class, 10);
        UserInfo userInfo20 = this.getEntityManager().load(UserInfo.class, 20);
        userInfo20.setName(userInfo10.getName());
        userInfo20.setAge(userInfo10.getAge());
        this.getEntityManager().save(userInfo20);

        MemCache<UserInfo> memCache = EntityCacheManager.getInstance().getMemCache(UserInfo.class);
        Collection<UserInfo> userInfoCollection = memCache.find(new String[]{UserInfo.Name, UserInfo.Age}, userInfo10.getName(), userInfo10.getAge());
        Assert.assertEquals(2, userInfoCollection.size());
        for (UserInfo userInfo : userInfoCollection) {
            Assert.assertEquals(UserInfoUtils.getName(10), userInfo.getName());
            Assert.assertEquals(UserInfoUtils.getAge(10), userInfo.getAge());
        }

        UserInfo userInfo = new UserInfo();
        UserInfoUtils.createData(userInfo, 101);
        this.getEntityManager().save(userInfo);
        UserInfo memUserInfo = memCache.locate(userInfo.getId());
        Assert.assertNotNull(memUserInfo);
        Assert.assertTrue(SimulateUtils.compareData(userInfo, memUserInfo));
        UserInfo saveUserInfo = this.getEntityManager().load(UserInfo.class, userInfo.getId());
        Assert.assertNotNull(saveUserInfo);
        Assert.assertTrue(SimulateUtils.compareData(userInfo, saveUserInfo));

        this.getEntityManager().delete(userInfo);
        Assert.assertNull(memCache.locate(userInfo.getId()));
        Assert.assertNull(this.getEntityManager().load(UserInfo.class, userInfo.getId()));
    }
}
