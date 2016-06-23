package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.domain.BillDetail;
import org.dangcat.persistence.domain.BillInfo;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.Assert;

import java.util.List;

public class TestEntityRelation extends TestEntityBase
{
    @Override
    protected void testDatabase(String databaseName) throws TableException, EntityException
    {
        long beginTime = DateUtils.currentTimeMillis();
        logger.info("Begin to test " + databaseName);
        SessionFactory.getInstance().setDefaultName(databaseName);

        List<Table> tableList = EntityRelationUtils.getTable();
        for (Table table : tableList)
        {
            if (table.exists())
                table.drop();
            // 产生新的数据表
            table.create();
            // 存储数据表。
            table.save();
        }

        this.testInsert();
        this.testModify();
        this.testDelete();

        for (Table table : tableList)
            table.drop();

        logger.info("End test " + databaseName + ", cost " + (DateUtils.currentTimeMillis() - beginTime) + " ms.");
    }

    private void testDelete() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        BillInfo billInfo = entityManager.load(BillInfo.class, 0);
        BillDetail[] billDetailArray = billInfo.getBillDetails().toArray(new BillDetail[0]);
        billInfo.removeBillDetail(billDetailArray[0]);
        billInfo.removeBillDetail(billDetailArray[2]);
        billInfo.calculateTotal();
        entityManager.save(billInfo);

        BillInfo saveBillInfo = entityManager.load(BillInfo.class, billInfo.getId());
        Assert.assertTrue(SimulateUtils.compareData(billInfo, saveBillInfo));
        Assert.assertEquals(1, saveBillInfo.getBillDetails().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(billInfo.getBillDetails(), saveBillInfo.getBillDetails()));

        BillDetail billDetail1 = new BillDetail();
        billDetail1.setName("B2-D2");
        billDetail1.setAmount(20.11);
        billInfo.addBillDetail(billDetail1);
        entityManager.save(billInfo);

        BillInfo saveBillInfo2 = entityManager.load(BillInfo.class, billInfo.getId());
        Assert.assertTrue(SimulateUtils.compareData(billInfo, saveBillInfo2));
        Assert.assertEquals(2, saveBillInfo2.getBillDetails().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(billInfo.getBillDetails(), saveBillInfo2.getBillDetails()));
    }

    private void testInsert() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        BillInfo billInfo = new BillInfo();
        billInfo.setName("B1");
        BillDetail billDetail1 = new BillDetail();
        billDetail1.setName("B1-D1");
        billDetail1.setAmount(10.11);
        billInfo.addBillDetail(billDetail1);
        BillDetail billDetail2 = new BillDetail();
        billDetail2.setName("B1-D2");
        billDetail2.setAmount(50.49);
        billInfo.addBillDetail(billDetail2);
        BillDetail billDetail3 = new BillDetail();
        billDetail3.setName("B1-D3");
        billDetail3.setAmount(60.51);
        billInfo.addBillDetail(billDetail3);
        entityManager.save(billInfo);

        BillInfo saveBillInfo = entityManager.load(BillInfo.class, billInfo.getId());
        Assert.assertTrue(SimulateUtils.compareData(billInfo, saveBillInfo));
        Assert.assertEquals(3, saveBillInfo.getBillDetails().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(billInfo.getBillDetails(), saveBillInfo.getBillDetails()));

        billInfo.removeBillDetail(billDetail1);
        Assert.assertEquals(2, billInfo.getBillDetails().size());
        billInfo.removeBillDetail(billDetail2);
        Assert.assertEquals(1, billInfo.getBillDetails().size());
        billInfo.removeBillDetail(billDetail3);
        Assert.assertEquals(0, billInfo.getBillDetails().size());
    }

    private void testModify() throws EntityException
    {
        EntityManager entityManager = this.getEntityManager();
        BillInfo billInfo = entityManager.load(BillInfo.class, 0);
        billInfo.setName("B2");
        for (BillDetail billDetail : billInfo.getBillDetails())
            billDetail.setAmount(billDetail.getAmount() * 1.5);
        billInfo.calculateTotal();
        entityManager.save(billInfo);

        BillInfo saveBillInfo = entityManager.load(BillInfo.class, billInfo.getId());
        Assert.assertTrue(SimulateUtils.compareData(billInfo, saveBillInfo));
        Assert.assertEquals(3, saveBillInfo.getBillDetails().size());
        Assert.assertTrue(SimulateUtils.compareDataCollection(billInfo.getBillDetails(), saveBillInfo.getBillDetails()));
    }
}
