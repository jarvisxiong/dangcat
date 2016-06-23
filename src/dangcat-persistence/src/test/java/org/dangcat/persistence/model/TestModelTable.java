package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orderby.OrderByType;
import org.dangcat.persistence.orderby.OrderByUnit;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

public class TestModelTable extends TestEntityBase
{
    private static final int TEST_COUNT = 1000;

    private void compareTableFieldB(Table table, int beginValue, int endValue, int count)
    {
        int index = 0;
        for (Row row : table.getRows())
        {
            Assert.assertEquals((Integer) (beginValue + index), row.getField("FieldB").getInteger());
            index++;
        }
        Assert.assertEquals(count, index);
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();
        this.testTableInsert();
        this.testTableRange();
        this.testTableModify();
        this.testTableDelete();
        this.testTableDrop();
        this.testTableBatchExecute();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testTableBatchExecute() throws TableException
    {
        Table table = new Table("TestTable");
        if (table.exists())
            table.drop();

        table.getSql().clear();
        table.getSql().addBatch("CREATE TABLE TestTable(Id INTEGER)");
        table.getSql().addBatch("INSERT INTO TestTable VALUES(1)");
        table.getSql().addBatch("INSERT INTO TestTable VALUES(2)");
        table.getSql().addBatch("DELETE FROM TestTable WHERE Id = 1");
        table.getSql().addBatch("TRUNCATE TABLE TestTable");

        table.execute();
        table.drop();
    }

    private void testTableCreate() throws TableException
    {
        Table table1 = TableDataUtils.getTable();

        if (table1.exists())
            table1.drop();

        // 产生新的数据表
        table1.create();

        // 产生新的数据表
        Table table2 = TableDataUtils.getTable();
        Assert.assertTrue(table2.exists());

        table2.load();
        Assert.assertEquals(table1.getColumns().size(), table2.getColumns().size());
        Assert.assertEquals(0, table2.getRows().size());
    }

    private void testTableDelete() throws TableException
    {
        Table table1 = TableDataUtils.getTable();
        table1.load();
        Rows rows1 = table1.getRows();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        // 删除第四行数据。
        Row deletedRow = rows1.remove(4);
        Assert.assertEquals(DataState.Modified, table1.getDataState());
        Assert.assertEquals(TEST_COUNT - 1, table1.getRows().size());
        Assert.assertEquals(1, table1.getRows().getDeletedRows().size());

        // 删除第五行数据。
        rows1.remove(5);
        Assert.assertEquals(DataState.Modified, table1.getDataState());
        Assert.assertEquals(TEST_COUNT - 2, table1.getRows().size());
        Assert.assertEquals(2, table1.getRows().getDeletedRows().size());

        // 删除第十行数据。
        Assert.assertFalse(rows1.remove(deletedRow));
        Assert.assertEquals(DataState.Modified, table1.getDataState());
        Assert.assertEquals(TEST_COUNT - 2, table1.getRows().size());
        Assert.assertEquals(2, table1.getRows().getDeletedRows().size());
        // 存储数据表。
        table1.save();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT - 2, table1.getRows().size());
        Assert.assertEquals(0, table1.getRows().getDeletedRows().size());

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(DataState.Browse, table2.getDataState());
        Assert.assertEquals(TEST_COUNT - 2, table2.getRows().size());
        Assert.assertEquals(0, table2.getRows().getDeletedRows().size());

        // 检查数据存储正确否
        Table table3 = TableDataUtils.getTable();
        table3.setRange(new Range(2));
        table3.setOrderBy(new OrderBy(new OrderByUnit("FieldC", OrderByType.Desc), new OrderByUnit("FieldD")));
        table3.load();
        Assert.assertEquals(2, table3.getRows().size());
    }

    private void testTableDrop() throws TableException
    {
        Table table = TableDataUtils.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }

    private void testTableInsert() throws TableException
    {
        Table table1 = TableDataUtils.getTable();
        Rows rows1 = table1.getRows();
        table1.load();
        TableDataUtils.createTableData(table1, TEST_COUNT);
        Assert.assertEquals(DataState.Modified, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        // 存储数据表。
        table1.save();
        // 检查状态
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        for (int i = 0; i < TEST_COUNT; i++)
        {
            Row row = rows1.get(i);
            Assert.assertEquals(DataState.Browse, row.getDataState());
            for (Column column : table1.getColumns())
            {
                Assert.assertEquals(DataState.Browse, row.getField(column.getName()).getDataState());
            }
        }

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table1.getRows().size(), table2.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));
    }

    private void testTableModify() throws TableException
    {
        Table table1 = TableDataUtils.getTable();
        table1.load();
        Rows rows1 = table1.getRows();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        TableDataUtils.modifyTableData(table1);
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());
        Assert.assertEquals(DataState.Modified, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());
        Assert.assertEquals(TEST_COUNT, table1.getRows().getModifiedRows().size());

        // 存储数据表。
        table1.save();

        // 检查状态
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());
        Assert.assertEquals(0, table1.getRows().getModifiedRows().size());
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        for (int i = 0; i < TEST_COUNT; i++)
        {
            Row row = rows1.get(i);
            Assert.assertEquals(DataState.Browse, row.getDataState());
            for (Column column : table1.getColumns())
            {
                Assert.assertEquals(DataState.Browse, row.getField(column.getName()).getDataState());
            }
        }

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table2.getRows().size(), table1.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));

        // 测试全部删除功能。
        Table table3 = TableDataUtils.getTable();
        table3.load();
        table3.getRows().removeAll();
        table3.save();
        Table table4 = TableDataUtils.getTable();
        table4.load();
        Assert.assertEquals(0, table4.getRows().size());

        // 测试修改表为插入状态，重新插入的功能。
        table2.setTableState(TableState.Insert);
        for (int i = 0; i < TEST_COUNT; i++)
        {
            Row row = table2.getRows().get(i);
            Assert.assertEquals(DataState.Insert, row.getDataState());
            for (Column column : table2.getColumns())
            {
                Assert.assertEquals(DataState.Insert, row.getField(column.getName()).getDataState());
            }
        }
        table2.save();

        // 判断重新插入成功否。
        Table table5 = TableDataUtils.getTable();
        table5.load();
        Assert.assertEquals(table1.getRows().size(), table5.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table5));
    }

    private void testTableRange() throws TableException
    {
        Table table = TableDataUtils.getTable();
        Range range = new Range(1);
        range.setCalculateTotalSize(true);
        table.setRange(range);
        table.load();
        Assert.assertEquals(1, table.getRows().size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(table, 0, 0, 1);

        range = new Range(1, 20);
        range.setCalculateTotalSize(true);
        table.setRange(range);
        table.load();
        Assert.assertEquals(20, table.getRows().size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(table, 0, 19, 20);

        range = new Range(24, 22);
        range.setCalculateTotalSize(true);
        table.setRange(range);
        table.load();
        Assert.assertEquals(22, table.getRows().size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(table, 23 * 22, 24 * 22 - 1, 22);

        range = new Range(TEST_COUNT / 25, 25);
        range.setCalculateTotalSize(true);
        table.setRange(range);
        table.load();
        Assert.assertEquals(25, table.getRows().size());
        Assert.assertEquals(TEST_COUNT, range.getTotalSize());
        this.compareTableFieldB(table, TEST_COUNT - 25, TEST_COUNT - 1, 25);
    }
}
