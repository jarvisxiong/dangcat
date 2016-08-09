package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.sql.Sql;
import org.junit.Assert;

public class TestModelNameBatch extends TestEntityBase {
    private static final int TEST_COUNT = 1000;

    private Table createTableData() {
        Table table = TableDataUtils.getTable();
        table.getSqls().read(TestModelNameBatch.class);
        return table;
    }

    private void testBatchCreate() throws TableException {
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

    private void testBatchDelete() throws TableException {
        Table table1 = this.createTableData();
        table1.load();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        table1.setSqlName(Sql.DELETE);
        table1.execute();

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(DataState.Browse, table2.getDataState());
        Assert.assertEquals(0, table2.getRows().size());
    }

    private void testBatchDrop() throws TableException {
        Table table = TableDataUtils.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }

    private void testBatchInsert() throws TableException {
        Table table1 = this.createTableData();
        TableDataUtils.createTableData(table1, TEST_COUNT);
        int index = 0;
        for (Row row : table1.getRows())
            row.getField("Id").setInteger(++index);
        table1.setTableState(TableState.Normal);
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        // 存储数据表。
        table1.setSqlName(Sql.INSERT);
        table1.execute();

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table1.getRows().size(), table2.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));
    }

    private void testBatchModify() throws TableException {
        Table table1 = this.createTableData();
        table1.load();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());
        TableDataUtils.modifyTableData(table1);
        table1.setTableState(TableState.Normal);

        // 存储数据表。
        table1.setSqlName(Sql.UPDATE);
        table1.execute();

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table2.getRows().size(), table1.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));
    }

    private void testBatchName(boolean useName) throws TableException {
        Table table1 = this.createTableData();

        if (table1.exists())
            table1.drop();

        // 产生新的数据表
        table1.create();

        // 存储数据表。
        table1.setSqlName(useName ? Sql.EXECUTE : null);
        table1.execute();

        Table table2 = TableDataUtils.getTable();
        Assert.assertTrue(table2.exists());
        table2.load();
        Assert.assertEquals(0, table2.getRows().size());
        table1.drop();
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testBatchCreate();
        this.testBatchInsert();
        this.testBatchModify();
        this.testBatchDelete();
        this.testBatchDrop();
        this.testBatchName(false);
        this.testBatchName(true);

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
