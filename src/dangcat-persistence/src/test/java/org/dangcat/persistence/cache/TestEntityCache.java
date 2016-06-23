package org.dangcat.persistence.cache;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.AccountInfoAlias;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.EntityDataUtils;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.*;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TestEntityCache extends TestCacheBase {
    private static final String TABLE_NAME = "ModelData";

    @Test
    public void testCacheConfig() {
        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        Assert.assertEquals(3, entityCacheManager.size());

        EntityCacheImpl<AccountInfoAlias> entityCache = (EntityCacheImpl<AccountInfoAlias>) entityCacheManager.getEntityCache(AccountInfoAlias.class);
        Assert.assertNotNull(entityCache);
        Assert.assertEquals(3, entityCache.getIndexSize());
        Assert.assertEquals("Default", entityCache.getDatabaseName());
        Assert.assertEquals(30, entityCache.getInterval().intValue());
    }

    private void testCacheFilter1(EntityCache<EntityData> entityCache) {
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006"));
        // 载入数据
        Collection<EntityData> entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        EntityData entityData = entityDataList.iterator().next();
        Assert.assertEquals("A-00000000000000000000000000000000000006", entityData.getFieldA());
        Assert.assertTrue(filterGroup.isValid(entityData));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009"));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(9, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 2, 9));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(8, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldC的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, 3 * 3.14, 9 * 3.14 + 0.01));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(7, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.between, (long) 4 * 100000000, (long) 9 * 100000000));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(6, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.DAY, DateUtils.add(DateUtils.DAY, DateUtils.now(), 5));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 9);
        filterGroup.add(new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(5, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(9, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));
    }

    private void testCacheFilter2(EntityCache<EntityData> entityCache) {
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        // 载入数据
        Collection<EntityData> entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData : entityDataList) {
            if (entityData.getFieldB() == 6)
                Assert.assertEquals("A-00000000000000000000000000000000000006", entityData.getFieldA());
            else if (entityData.getFieldB() == 9)
                Assert.assertEquals("A-00000000000000000000000000000000000009", entityData.getFieldA());
            else
                Assert.assertEquals("A-00000000000000000000000000000000000009", entityData.getFieldA());
            Assert.assertTrue(filterGroup.isValid(entityData));
        }

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        FilterUnit filterUnit = new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009");
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.lt, "A-00000000000000000000000000000000000002"));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 1, null));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldC的过滤条件
        filterGroup.clear();
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, null, 7 * 3.14 + 0.01));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(8, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.eq, (long) 4 * 100000000, (long) 5 * 100000000));
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.SECOND, DateUtils.add(DateUtils.DAY, DateUtils.now(), 6));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 8);
        filterUnit = new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay);
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        EntityCacheManager.getInstance().remove(TABLE_NAME, filterGroup);
        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 载入数据
        entityDataList = entityCache.load(filterGroup);
        // 判断过滤内容
        Assert.assertEquals(10, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));
    }

    private void testCacheFilter3(EntityCache<EntityData> entityCache) {
        EntityManager entityManager = this.getEntityManager();
        EntityData srcEntityData = entityManager.load(EntityData.class, 6);

        // 测试定位数据。
        Collection<EntityData> entityDataList = entityCache.load(new String[]{EntityData.FieldA}, srcEntityData.getFieldA());
        Assert.assertEquals(1, entityDataList.size());
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, entityDataList.iterator().next()));

        // 测试删除数据。
        entityCache.delete(entityDataList.iterator().next());
        Collection<EntityData> entityDataList2 = entityCache.load(new String[]{EntityData.FieldA}, srcEntityData.getFieldA());
        Assert.assertNull(entityDataList2);
    }

    private void testCacheFilter4(EntityCache<EntityData> entityCache) {
        EntityManager entityManager = this.getEntityManager();
        EntityData srcEntityData = entityManager.load(EntityData.class, 5);
        // 测试定位数据。
        Collection<EntityData> entityDataList = entityCache.load(new String[]{EntityData.FieldA}, srcEntityData.getFieldA());
        EntityData destEntityData = entityDataList.iterator().next();
        Assert.assertEquals(1, entityDataList.size());
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, destEntityData));

        // 测试修改数据。
        EntityDataUtils.modifyEntityData(srcEntityData, 5);
        entityManager.save(srcEntityData);
        entityCache.refresh(destEntityData);

        EntityData destEntityData2 = entityCache.load(srcEntityData.getId());
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, destEntityData2));
    }

    private void testCacheFilter5(EntityCache<EntityData> entityCache) {
        EntityManager entityManager = this.getEntityManager();
        EntityData srcEntityData = entityManager.load(EntityData.class, 7);

        // 测试缓存刷新
        EntityData entityData1 = entityCache.load(srcEntityData.getId());
        EntityCacheManager.getInstance().remove(TABLE_NAME, srcEntityData.getId());
        EntityData entityData2 = entityCache.load(srcEntityData.getId());
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, entityData1));
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, entityData2));

        FilterExpress filterExpress = new FilterUnit("Id", FilterType.eq, srcEntityData.getId());
        EntityCacheManager.getInstance().remove(TABLE_NAME, filterExpress);
        EntityData entityData3 = entityCache.load(srcEntityData.getId());
        Assert.assertTrue(SimulateUtils.compareData(srcEntityData, entityData3));
        Assert.assertFalse(entityData2 == entityData3);
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();

        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        entityCacheManager.clear(true);
        EntityCache<EntityData> entityCache = entityCacheManager.getEntityCache(EntityData.class);
        Assert.assertNotNull(entityCache);

        // 测试添加内存数据。
        List<EntityData> entityList = new ArrayList<EntityData>();
        EntityDataUtils.createEntityDataList(entityList, TEST_COUNT);
        for (EntityData entityData : entityList)
            entityCache.save(entityData);
        Assert.assertEquals(TEST_COUNT, entityCache.size());

        // 测试存储数据
        EntityManager entityManager = this.getEntityManager();
        List<EntityData> entityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(TEST_COUNT, entityDataList.size());

        entityCache.clear(true);
        Assert.assertEquals(0, entityCache.size());
        this.testCacheFilter1(entityCache);

        entityCache.clear(true);
        this.testCacheFilter2(entityCache);

        entityCache.clear(true);
        this.testCacheFilter3(entityCache);

        entityCache.clear(true);
        this.testCacheFilter4(entityCache);

        entityCache.clear(true);
        this.testCacheFilter5(entityCache);

        this.testTableDrop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
