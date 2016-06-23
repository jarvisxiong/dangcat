package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orderby.OrderByType;
import org.dangcat.persistence.orderby.OrderByUnit;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.LinkedList;
import java.util.List;

public class TestEntityData extends TestEntityBase {
    private static final int TEST_COUNT = 1000;

    private void compareTableFieldB(List<EntityData> entityDataList, int beginValue, int endValue, int count) {
        int index = 0;
        for (EntityData entityData : entityDataList) {
            Assert.assertEquals((Integer) (beginValue + index), entityData.getFieldB());
            index++;
        }
        Assert.assertEquals(count, index);
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();
        this.testEntityInsert();
        this.testEntityRange();
        this.testEntityModify();
        this.testEntityDelete();
        this.testEntityRefresh();
        this.testTableDrop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testEntityDelete() throws EntityException {
        EntityManager entityManager = this.getEntityManager();
        // 检查数据存储正确否
        List<EntityData> entityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(TEST_COUNT, entityDataList.size());

        // 删除第四行数据。
        EntityData entityData = entityDataList.get(4);
        entityDataList.remove(entityData);
        entityManager.delete(entityData);
        Assert.assertEquals(TEST_COUNT - 1, entityDataList.size());
        Assert.assertEquals(2, entityData.getBeforeDeleteCount());
        Assert.assertEquals(2, entityData.getAfterDeleteCount());

        // 删除第五行数据。
        entityData = entityDataList.get(4);
        entityDataList.remove(entityData);
        entityManager.delete(entityData);
        Assert.assertEquals(TEST_COUNT - 2, entityDataList.size());

        // 检查数据存储正确否
        entityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(TEST_COUNT - 2, entityDataList.size());

        // 检查数据存储正确否
        entityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(TEST_COUNT - 2, entityDataList.size());
        OrderBy orderBy = new OrderBy(new OrderByUnit("FieldC", OrderByType.Desc), new OrderByUnit("FieldD"));
        entityDataList = entityManager.load(EntityData.class, null, new Range(2), orderBy);
        Assert.assertEquals(2, entityDataList.size());
    }

    private void testEntityInsert() throws EntityException {
        List<EntityData> entityDataList = new LinkedList<EntityData>();
        EntityDataUtils.createEntityDataList(entityDataList, TEST_COUNT);
        Assert.assertEquals(TEST_COUNT, entityDataList.size());

        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(entityDataList.toArray());
        EntityData entityData = entityDataList.get(0);
        Assert.assertEquals(2, entityData.getBeforeInsertCount());
        Assert.assertEquals(2, entityData.getAfterInsertCount());

        // 检查数据存储正确否
        List<EntityData> saveEntityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(entityDataList.size(), saveEntityDataList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList, saveEntityDataList));
        entityData = saveEntityDataList.get(0);
        Assert.assertEquals(2, entityData.getAfterLoadCount());
    }

    private void testEntityModify() throws EntityException {
        EntityManager entityManager = this.getEntityManager();
        // 检查数据存储正确否
        List<EntityData> entityDataList = entityManager.load(EntityData.class);
        EntityDataUtils.modifyEntityDataList(entityDataList);
        Assert.assertEquals(TEST_COUNT, entityDataList.size());

        // 存储数据表。
        entityManager.save(entityDataList.toArray());
        EntityData entityData = entityDataList.get(0);
        Assert.assertEquals(2, entityData.getBeforeSaveCount());
        Assert.assertEquals(2, entityData.getAfterSaveCount());
        Assert.assertEquals(2, entityData.getAfterCommitCount());

        // 检查数据存储正确否
        List<EntityData> saveEntityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(entityDataList.size(), saveEntityDataList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList, saveEntityDataList));

        // 测试全部删除功能。
        entityManager.delete(saveEntityDataList.toArray());
        entityDataList = entityManager.load(EntityData.class);
        Assert.assertNull(entityDataList);

        // 测试修改表为插入状态，重新插入的功能。
        EntityUtils.resetAutoIncrement(saveEntityDataList);
        entityManager.save(saveEntityDataList.toArray());

        // 判断重新插入成功否。
        entityDataList = entityManager.load(EntityData.class);
        Assert.assertEquals(saveEntityDataList.size(), entityDataList.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(saveEntityDataList, entityDataList));
    }

    private void testEntityRange() throws EntityException {
        OrderBy orderBy = OrderBy.parse("id");
        // 存储数据表。
        EntityManager entityManager = this.getEntityManager();
        Range range = new Range(1);
        range.setCalculateTotalSize(true);
        List<EntityData> entityDataList = entityManager.load(EntityData.class, null, range, orderBy);
        Assert.assertEquals(1, entityDataList.size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(entityDataList, 0, 0, 1);

        range = new Range(1, 20);
        range.setCalculateTotalSize(true);
        entityDataList = entityManager.load(EntityData.class, null, range, orderBy);
        Assert.assertEquals(20, entityDataList.size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(entityDataList, 0, 19, 20);

        range = new Range(24, 22);
        range.setCalculateTotalSize(true);
        entityDataList = entityManager.load(EntityData.class, null, range, orderBy);
        Assert.assertEquals(22, entityDataList.size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(entityDataList, 23 * 22, 24 * 22 - 1, 22);

        range = new Range(TEST_COUNT / 25, 25);
        range.setCalculateTotalSize(true);
        entityDataList = entityManager.load(EntityData.class, null, range, orderBy);
        Assert.assertEquals(25, entityDataList.size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(entityDataList, TEST_COUNT - 25, TEST_COUNT - 1, 25);
    }

    private void testEntityRefresh() throws EntityException {
        int index = TEST_COUNT + 1;
        EntityData srcEntityData = EntityDataUtils.createEntityData(index);

        // 测试保存数据。
        EntityManager entityManager = this.getEntityManager();
        entityManager.save(srcEntityData);
        EntityData dstEntityData = entityManager.load(EntityData.class, srcEntityData.getId());
        Assert.assertNotNull(dstEntityData);

        // 修改数据。
        EntityDataUtils.modifyEntityData(dstEntityData, index);
        entityManager.save(dstEntityData);
        EntityData modifyEntityData = entityManager.load(EntityData.class, srcEntityData.getId());

        // 刷新数据。
        entityManager.refresh(srcEntityData);

        Assert.assertTrue(SimulateUtils.compareData(modifyEntityData, srcEntityData));

        // 测试删除数据
        entityManager.delete(modifyEntityData);
        Assert.assertNull(entityManager.refresh(srcEntityData));
    }

    private void testTableCreate() throws TableException {
        Table table = EntityDataUtils.getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    private void testTableDrop() throws TableException {
        Table table = EntityDataUtils.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }
}
