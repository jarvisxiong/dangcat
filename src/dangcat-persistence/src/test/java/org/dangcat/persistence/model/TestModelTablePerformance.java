package org.dangcat.persistence.model;

import junit.framework.Assert;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;

public class TestModelTablePerformance extends TestEntityBase
{
    private static final int TEST_COUNT = 10000;
    private Table table = null;

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        if (this.table == null)
        {
            this.table = TableDataUtils.getTable();
            TableDataUtils.createTableData(this.table, TEST_COUNT);
        }
        SessionFactory.getInstance().setDefaultName(databaseName);

        if (this.table.exists())
            this.table.drop();
        this.table.create();

        logger.info("Begin test database: " + databaseName);

        // ´æ´¢Êý¾Ý±í¡£
        this.table.setTableState(TableState.Insert);
        long beginTime = DateUtils.currentTimeMillis();
        this.table.save();
        logger.info("\t save data cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms). count = " + TEST_COUNT + ",  velocity = "
                + (TEST_COUNT * 1000 / (DateUtils.currentTimeMillis() - beginTime)));

        beginTime = DateUtils.currentTimeMillis();
        Table table2 = TableDataUtils.getTable();
        table2.load();
        logger.info("\t load data cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
        Assert.assertEquals(TEST_COUNT, table2.getRows().size());

        beginTime = DateUtils.currentTimeMillis();
        Assert.assertTrue(TableUtils.equalsContent(this.table, table2));
        logger.info("\t check data cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");

        logger.info("End test database: " + databaseName);

        this.table.drop();
    }
}
