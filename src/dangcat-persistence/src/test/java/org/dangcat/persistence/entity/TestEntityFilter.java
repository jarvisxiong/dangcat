package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterGroupType;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TestEntityFilter extends TestEntityBase
{
    private static final int TEST_COUNT = 10;

    private void createEntityData() throws EntityException
    {
        List<EntityData> entityDataList = new LinkedList<EntityData>();
        EntityDataUtils.createEntityDataList(entityDataList, TEST_COUNT);
        Assert.assertEquals(TEST_COUNT, entityDataList.size());

        // 存储数据表。
        this.getEntityManager().save(entityDataList.toArray());
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        Table table = EntityDataUtils.getTable();
        if (table.exists())
            table.drop();
        // 产生新的数据表
        table.create();
        // 产生数据
        this.createEntityData();

        this.testEntityFilter1();
        this.testEntityFilter2();

        table.drop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testEntityFilter1() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006"));
        // 载入数据
        List<EntityData> entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        EntityData entityData = entityDataList.get(0);
        Assert.assertEquals("A-00000000000000000000000000000000000006", entityData.getFieldA());
        Assert.assertTrue(filterGroup.isValid(entityData));

        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009"));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(9, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 2, 9));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(8, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldC的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, 3 * 3.14, 9 * 3.14 + 0.01));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(7, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.between, (long) 4 * 100000000, (long) 9 * 100000000));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(6, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.DAY, DateUtils.add(DateUtils.DAY, DateUtils.now(), 5));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 9);
        filterGroup.add(new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(5, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(9, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));
    }

    private void testEntityFilter2() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        // 载入数据
        List<EntityData> entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        EntityData entityData = entityDataList.get(0);
        Assert.assertEquals("A-00000000000000000000000000000000000006", entityData.getFieldA());
        Assert.assertTrue(filterGroup.isValid(entityData));
        entityData = entityDataList.get(1);
        Assert.assertEquals("A-00000000000000000000000000000000000009", entityData.getFieldA());
        Assert.assertTrue(filterGroup.isValid(entityData));

        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        FilterUnit filterUnit = new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009");
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 测试BETWEEN
        filterGroup.clear();
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.lt, "A-00000000000000000000000000000000000002"));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 1, null));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(1, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldC的过滤条件
        filterGroup.clear();
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, null, 7 * 3.14 + 0.01));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(8, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.eq, (long) 4 * 100000000, (long) 5 * 100000000));
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.SECOND, DateUtils.add(DateUtils.DAY, DateUtils.now(), 6));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 8);
        filterUnit = new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay);
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(2, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));

        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 载入数据
        entityDataList = entityManager.load(EntityData.class, filterGroup);
        // 判断过滤内容
        Assert.assertEquals(10, entityDataList.size());
        for (EntityData entityData1 : entityDataList)
            Assert.assertTrue(filterGroup.isValid(entityData1));
    }
}
