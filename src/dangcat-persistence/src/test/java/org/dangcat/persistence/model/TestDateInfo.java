package org.dangcat.persistence.model;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.domain.DateInfo;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.TestEntityBase;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

import java.util.Date;

public class TestDateInfo extends TestEntityBase {
    private void assertSave(Table table, Date date) {
        table.save();
        table.getRows().clear();
        table.load();
        Row row = table.getRows().get(0);
        Assert.assertEquals(0, ValueUtils.compare(date, row.getField(DateInfo.Full).getDate()));
        date = DateUtils.clear(DateType.Second, date);
        Assert.assertEquals(0, ValueUtils.compare(date, row.getField(DateInfo.Second).getDate()));
        date = DateUtils.clear(DateType.Minute, date);
        Assert.assertEquals(0, ValueUtils.compare(date, row.getField(DateInfo.Minute).getDate()));
        date = DateUtils.clear(DateType.Day, date);
        Assert.assertEquals(0, ValueUtils.compare(date, row.getField(DateInfo.Day).getDate()));
    }

    private Table getTable() {
        Table table = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(DateInfo.class);
        if (entityMetaData != null)
            table = entityMetaData.getTable();
        return table;
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        this.testTableCreate();
        this.testInsert();
        this.testModify();
        this.testTableDrop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testInsert() throws EntityException {
        Table table = this.getTable();
        Row row = table.getRows().createNewRow();
        row.getField(DateInfo.Full).setDate(DateUtils.now());
        row.getField(DateInfo.Second).setDate(DateUtils.now());
        row.getField(DateInfo.Minute).setDate(DateUtils.now());
        row.getField(DateInfo.Day).setDate(DateUtils.now());
        table.getRows().add(row);
        this.assertSave(table, row.getField(DateInfo.Full).getDate());
    }

    private void testModify() throws EntityException {
        Table table = this.getTable();
        table.getRows().clear();
        table.load();

        Row row = table.getRows().get(0);
        Assert.assertNotNull(row);

        Date date = DateUtils.add(DateUtils.DAY, row.getField(DateInfo.Full).getDate(), 1);
        row.getField(DateInfo.Full).setDate(date);
        row.getField(DateInfo.Second).setDate(date);
        row.getField(DateInfo.Minute).setDate(date);
        row.getField(DateInfo.Day).setDate(date);
        this.assertSave(table, date);
    }

    private void testTableCreate() throws TableException {
        Table table = this.getTable();

        if (table.exists())
            table.drop();

        // 产生新的数据表
        table.create();
    }

    private void testTableDrop() throws TableException {
        Table table = this.getTable();
        table.drop();
        Assert.assertFalse(table.exists());
    }
}
