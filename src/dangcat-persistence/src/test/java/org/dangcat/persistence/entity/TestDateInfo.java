package org.dangcat.persistence.entity;

import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.domain.DateInfo;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.junit.Assert;

import java.util.Date;
import java.util.List;

public class TestDateInfo extends TestEntityBase {
    private void assertSave(DateInfo dateInfo) {
        this.getEntityManager().save(dateInfo);

        DateInfo saveDateInfo = this.getEntityManager().load(DateInfo.class, dateInfo.getId());
        Assert.assertNotNull(saveDateInfo);

        Date date = dateInfo.getFull();
        Assert.assertEquals(0, ValueUtils.compare(date, saveDateInfo.getFull()));
        date = DateUtils.clear(DateType.Second, date);
        Assert.assertEquals(0, ValueUtils.compare(date, saveDateInfo.getSecond()));
        date = DateUtils.clear(DateType.Minute, date);
        Assert.assertEquals(0, ValueUtils.compare(date, saveDateInfo.getMinute()));
        date = DateUtils.clear(DateType.Day, date);
        Assert.assertEquals(0, ValueUtils.compare(date, saveDateInfo.getDay()));
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
        DateInfo dateInfo = new DateInfo();
        dateInfo.setFull(DateUtils.now());
        dateInfo.setDay(DateUtils.now());
        dateInfo.setMinute(DateUtils.now());
        dateInfo.setSecond(DateUtils.now());
        this.assertSave(dateInfo);
    }

    private void testModify() throws EntityException {
        List<DateInfo> dateInfos = this.getEntityManager().load(DateInfo.class);
        Assert.assertNotNull(dateInfos);

        DateInfo dateInfo = dateInfos.get(0);
        Assert.assertNotNull(dateInfo);

        Date date = DateUtils.add(DateUtils.DAY, dateInfo.getFull(), 1);
        dateInfo.setFull(date);
        dateInfo.setDay(date);
        dateInfo.setMinute(date);
        dateInfo.setSecond(date);

        this.assertSave(dateInfo);
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
