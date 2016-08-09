package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterGroupType;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.orm.TableSqlBuilder;
import org.junit.Assert;

import java.util.Date;

public class TestModelFilter extends TestEntityBase {
    private static final int TEST_COUNT = 10;

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        Table table = TableDataUtils.getTable();
        if (table.exists())
            table.drop();
        // 产生新的数据表
        table.create();
        TableDataUtils.createTableData(table, TEST_COUNT);
        // 存储数据表。
        table.save();

        this.testTableFilter1();
        this.testTableFilter2();
        this.testTableFilter3();

        table.drop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testTableFilter1() throws TableException {
        Table table = TableDataUtils.getTable();
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006"));
        table.setFilter(filterGroup);
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(1, table.getRows().size());
        Row row1 = table.getRows().get(0);
        Assert.assertEquals("A-00000000000000000000000000000000000006", row1.getField(EntityData.FieldA).getString());
        Assert.assertTrue(filterGroup.isValid(row1));

        // 测试BETWEEN
        filterGroup.clear();
        table.setFilter(filterGroup);
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009"));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(9, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 2, 9));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(8, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldC的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, 3 * 3.14, 9 * 3.14 + 0.01));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(7, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.between, (long) 4 * 100000000, (long) 9 * 100000000));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(6, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.DAY, DateUtils.add(DateUtils.DAY, DateUtils.now(), 5));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 9);
        filterGroup.add(new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay));
        // 输出载入语句
        TableSqlBuilder sql = new TableSqlBuilder(table, table.getDatabaseName());
        logger.info(sql.buildLoadStatement());
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(5, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 输出载入语句
        logger.info(sql.buildLoadStatement());
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(9, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));
    }

    private void testTableFilter2() throws TableException {
        Table table = TableDataUtils.getTable();
        // 构建最简单的过滤条件
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        table.setFilter(filterGroup);
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(2, table.getRows().size());
        Row row1 = table.getRows().get(0);
        Assert.assertEquals("A-00000000000000000000000000000000000006", row1.getField(EntityData.FieldA).getString());
        Assert.assertTrue(filterGroup.isValid(row1));
        row1 = table.getRows().get(1);
        Assert.assertEquals("A-00000000000000000000000000000000000009", row1.getField(EntityData.FieldA).getString());
        Assert.assertTrue(filterGroup.isValid(row1));

        // 测试BETWEEN
        filterGroup.clear();
        table.setFilter(filterGroup);
        // 加入字段FieldA的过滤条件
        FilterUnit filterUnit = new FilterUnit(EntityData.FieldA, FilterType.between, "A-00000000000000000000000000000000000001", "A-00000000000000000000000000000000000009");
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(1, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 测试BETWEEN
        filterGroup.clear();
        table.setFilter(filterGroup);
        // 加入字段FieldA的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldA, FilterType.lt, "A-00000000000000000000000000000000000002"));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(2, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldB的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldB, FilterType.between, 1, null));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(1, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldC的过滤条件
        filterGroup.clear();
        filterGroup.add(new FilterUnit(EntityData.FieldC, FilterType.between, null, 7 * 3.14 + 0.01));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(8, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldD的过滤条件
        filterGroup.add(new FilterUnit(EntityData.FieldD, FilterType.eq, (long) 4 * 100000000, (long) 5 * 100000000));
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(2, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 加入字段FieldE的过滤条件
        Date fromDay = DateUtils.clear(DateUtils.DAY, DateUtils.add(DateUtils.DAY, DateUtils.now(), 6));
        Date toDay = DateUtils.add(DateUtils.DAY, DateUtils.now(), 8);
        filterUnit = new FilterUnit(EntityData.FieldE, FilterType.between, fromDay, toDay);
        filterUnit.setNot(true);
        filterGroup.add(filterUnit);
        // 输出载入语句
        TableSqlBuilder sql = new TableSqlBuilder(table, table.getDatabaseName());
        logger.info(sql.buildLoadStatement());
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(1, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));

        // 测试或的运算。
        filterGroup.setGroupType(FilterGroupType.or);
        // 输出载入语句
        logger.info(sql.buildLoadStatement());
        // 载入数据
        table.load();
        // 判断过滤内容
        Assert.assertEquals(10, table.getRows().size());
        for (Row row : table.getRows())
            Assert.assertTrue(filterGroup.isValid(row));
    }

    private void testTableFilter3() throws TableException {
        FilterGroup objectGroup = new FilterGroup();
        objectGroup.setGroupType(FilterGroupType.or);

        FilterGroup objectInstance1 = new FilterGroup();
        objectInstance1.setValue("'AAA'");
        objectInstance1.add(new FilterUnit(EntityData.FieldA, FilterType.eq, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        objectInstance1.add(new FilterUnit(EntityData.FieldB, FilterType.between, 15, 20));
        objectGroup.add(objectInstance1);

        FilterGroup objectInstance2 = new FilterGroup();
        objectInstance2.setValue("'BBBB'");
        objectInstance2.add(new FilterUnit(EntityData.FieldC, FilterType.ignore, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        FilterUnit filterUnit = new FilterUnit(EntityData.FieldB, FilterType.between, 150, 35);
        filterUnit.setNot(true);
        objectInstance2.add(filterUnit);
        objectGroup.add(objectInstance2);

        FilterGroup objectInstance3 = new FilterGroup();
        objectInstance3.setValue("'CCC'");
        objectInstance3.add(new FilterUnit(EntityData.FieldC, FilterType.ignore, "A-00000000000000000000000000000000000006", null, "A-00000000000000000000000000000000000009"));
        objectInstance3.add(new FilterUnit(EntityData.FieldB, FilterType.ignore, 150, 35));
        objectGroup.add(objectInstance3);

        logger.info(objectGroup.toCaseExpress());
    }
}
