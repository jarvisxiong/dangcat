package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.*;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.List;

public class TestEntityJoin extends TestEntityBase
{
    private void checkEntityJoin() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();

        List<AccountInfo> entityDataList1 = entityManager.load(AccountInfo.class);
        Assert.assertEquals(2, entityDataList1.size());

        List<AccountInfoSqlXml> entityDataList2 = entityManager.load(AccountInfoSqlXml.class);
        Assert.assertEquals(2, entityDataList2.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList2));

        List<AccountInfoSql> entityDataList3 = entityManager.load(AccountInfoSql.class);
        Assert.assertEquals(2, entityDataList3.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList3));

        List<AccountInfoSqls> entityDataList4 = entityManager.load(AccountInfoSqls.class);
        Assert.assertEquals(2, entityDataList4.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList4));

        List<AccountInfoAlias> entityDataList5 = entityManager.load(AccountInfoAlias.class);
        Assert.assertEquals(2, entityDataList5.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList5));

        List<AccountInfoSqlFile> entityDataList6 = entityManager.load(AccountInfoSqlFile.class);
        Assert.assertEquals(2, entityDataList6.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList6));

        List<AccountInfoJoin> entityDataList7 = entityManager.load(AccountInfoJoin.class);
        Assert.assertEquals(2, entityDataList7.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList7));

        List<AccountInfoJoinAlias> entityDataList8 = entityManager.load(AccountInfoJoinAlias.class);
        Assert.assertEquals(2, entityDataList8.size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(entityDataList1, entityDataList8));
    }

    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        List<Table> tableList = EntityJoinUtils.getTable();
        for (Table table : tableList)
        {
            if (table.exists())
                table.drop();
            // 产生新的数据表
            table.create();
            // 存储数据表。
            table.save();
        }

        this.checkEntityJoin();

        for (Table table : tableList)
            table.drop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }
}
