package org.dangcat.persistence.model;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

public class TestModelBatch extends TestEntityBase {
    protected static final Logger logger = Logger.getLogger(TestModelBatch.class);
    private static final int TEST_COUNT = 1000;

    private Table createTableData() {
        Table table = TableDataUtils.getTable();
        TableDataUtils.createTableData(table, TEST_COUNT);
        int index = 0;
        for (Row row : table.getRows())
            row.getField("Id").setInteger(++index);
        return table;
    }

    private void testBatchAll() throws TableException {
        Table table1 = this.createTableData();

        if (table1.exists())
            table1.drop();

        // 产生新的数据表
        table1.create();

        table1.getSql().clear();
        table1.getSql().append("INSERT INTO " + table1.getTableName().getName() + "(Id, FieldA, FieldB, FieldC, FieldD, FieldE, FieldF, FieldG, FieldH)\r\n");
        table1.getSql().append("VALUES(:Id, :FieldA, :FieldB, :FieldC, :FieldD, :FieldE, :FieldF, :FieldG, :FieldH)");
        table1.getSql().addBatch();
        table1.getSql().append("UPDATE " + table1.getTableName().getName() + " ");
        table1.getSql().append("SET FieldA=:FieldA, FieldB=:FieldB, FieldC=:FieldC, FieldD=:FieldD, FieldE=:FieldE, FieldF=:FieldF, FieldG=:FieldG, FieldH=:FieldH ");
        table1.getSql().append("WHERE Id=:Id");
        table1.getSql().addBatch();
        table1.getSql().append("DELETE FROM " + table1.getTableName().getName() + " WHERE Id=:Id");
        // 存储数据表。
        table1.execute();

        Table table2 = TableDataUtils.getTable();
        Assert.assertTrue(table2.exists());
        table2.load();
        Assert.assertEquals(0, table2.getRows().size());
        table1.drop();
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
        Table table1 = TableDataUtils.getTable();
        table1.load();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        table1.getSql().clear();
        table1.getSql().append("DELETE FROM " + table1.getTableName().getName() + " WHERE Id=:Id");
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
        table1.setTableState(TableState.Normal);
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());

        table1.getSql().clear();
        table1.getSql().append("INSERT INTO " + table1.getTableName().getName() + "(Id, FieldA, FieldB, FieldC, FieldD, FieldE, FieldF, FieldG, FieldH)\r\n");
        table1.getSql().append("VALUES(:Id, :FieldA, :FieldB, :FieldC, :FieldD, :FieldE, :FieldF, :FieldG, :FieldH)");
        // 存储数据表。
        table1.execute();

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table1.getRows().size(), table2.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));
    }

    private void testBatchModify() throws TableException {
        Table table1 = TableDataUtils.getTable();
        table1.load();
        Assert.assertEquals(DataState.Browse, table1.getDataState());
        Assert.assertEquals(TEST_COUNT, table1.getRows().size());
        TableDataUtils.modifyTableData(table1);
        table1.setTableState(TableState.Normal);

        table1.getSql().clear();
        table1.getSql().append("UPDATE " + table1.getTableName().getName() + " ");
        table1.getSql().append("SET FieldA=:FieldA, FieldB=:FieldB, FieldC=:FieldC, FieldD=:FieldD, FieldE=:FieldE, FieldF=:FieldF, FieldG=:FieldG, FieldH=:FieldH ");
        table1.getSql().append("WHERE Id=:Id");
        // 存储数据表。
        table1.execute();

        // 检查数据存储正确否
        Table table2 = TableDataUtils.getTable();
        table2.load();
        Assert.assertEquals(table2.getRows().size(), table1.getRows().size());
        Assert.assertTrue(TableUtils.equalsContent(table1, table2));
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
        this.testBatchAll();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
