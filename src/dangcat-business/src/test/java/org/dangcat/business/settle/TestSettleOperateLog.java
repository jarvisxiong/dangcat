package org.dangcat.business.settle;

import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.web.test.TestWebServerBase;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.service.annotation.Service;
import org.dangcat.framework.service.impl.ServiceControlBase;
import org.dangcat.persistence.simulate.SimulateUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.Map.Entry;

public class TestSettleOperateLog extends TestWebServerBase
{
    private static final int TEST_COUNT = 100;
    private Date[] dateTimes = null;
    @Service
    private SettleService settleService = null;

    private void assetResult(Collection<OperateStat> operateStatCollection)
    {
        Collection<OperateStat> saveOperateStatCollection = this.getEntityManager().load(OperateStat.class);
        Assert.assertNotNull(saveOperateStatCollection);
        Assert.assertEquals(operateStatCollection.size(), saveOperateStatCollection.size());
        SimulateUtils.resetDataCollection(saveOperateStatCollection, OperateStat.GroupName, OperateStat.OperatorNo, OperateStat.OperatorName);
        Assert.assertTrue(SimulateUtils.compareDataCollection(operateStatCollection, saveOperateStatCollection));
    }

    private void createDateTimes()
    {
        Date now = DateUtils.now();
        Date priorMonth = DateUtils.add(DateUtils.MONTH, now, -1);
        Date nextMonth = DateUtils.add(DateUtils.MONTH, now, 1);
        this.dateTimes = new Date[] { priorMonth, now, nextMonth };
    }

    private Collection<OperateStat> createOperateStats(Collection<OperateLog> operateLogCollection, int count)
    {
        OperatorGroup operatorGroup = new OperatorGroup();
        operatorGroup.setName("Name");
        operatorGroup.setDescription("Description");
        this.getEntityManager().save(operatorGroup);
        OperatorInfo operatorInfo = new OperatorInfo();
        operatorInfo.setName("Name");
        operatorInfo.setNo("No000000000");
        operatorInfo.setDescription("Description");
        operatorInfo.setGroupId(operatorGroup.getId());
        operatorInfo.setRoleId(0);
        operatorInfo.setUseAble(Boolean.TRUE);
        this.getEntityManager().save(operatorInfo);

        Map<Integer, OperateStat> operateStatMap = new HashMap<Integer, OperateStat>();
        for (int i = 0; i < count; i++)
        {
            OperateLog operateLog = new OperateLog();
            operateLog.setErrorCode(i % 5);
            operateLog.setIpAddress("127.0.0.1");
            operateLog.setOperatorId(operatorInfo.getId());
            operateLog.setMethodId(i);
            operateLog.setRemark("Remark " + i);
            operateLogCollection.add(operateLog);

            OperateStat operateStat = operateStatMap.get(operateLog.getOperatorId());
            if (operateStat == null)
            {
                operateStat = new OperateStat();
                operateStat.setOperatorId(operateLog.getOperatorId());
                operateStatMap.put(operateLog.getOperatorId(), operateStat);
            }

            if (OperateLog.SUCCESS.equals(operateLog.getErrorCode()))
                operateStat.setSuccess(operateStat.getSuccess() + 1);
            else
                operateStat.setFailure(operateStat.getFailure() + 1);
        }
        return operateStatMap.values();
    }

    @After
    @Before
    public void dropTables() throws Exception
    {
        this.initEntityTable(OperatorInfo.class, OperatorGroup.class);
        this.dropEntityTable(OperateLog.class, OperateStat.class, SettleRecord.class);
        if (this.dateTimes != null)
        {
            for (Date date : this.dateTimes)
            {
                DateUtils.setCurrentDate(date);
                this.dropEntityTable(OperateLog.class, OperateStat.class, SettleRecord.class);
            }
        }
        DateUtils.setCurrentDate(null);
    }

    @After
    public void restart() throws Exception
    {
        ((ServiceControlBase) this.settleService).restart();
    }

    /**
     * 测试当日结算的基本功能。
     */
    @Test
    public void testSettleCurrent()
    {
        Collection<OperateLog> operateLogCollection = new ArrayList<OperateLog>();
        Collection<OperateStat> operateStatCollection = this.createOperateStats(operateLogCollection, TEST_COUNT);
        this.getEntityManager().save(operateLogCollection.toArray());
        this.settleService.execute();
        this.assetResult(operateStatCollection);
    }

    /**
     * 测试跨月结算的功能。
     */
    @Test
    public void testSettleDiffMonth()
    {
        this.createDateTimes();

        Map<Date, Collection<OperateStat>> operateStatCollectionMap = new HashMap<Date, Collection<OperateStat>>();
        Object[] entities = null;
        for (Date date : this.dateTimes)
        {
            DateUtils.setCurrentDate(date);

            if (entities != null)
                this.getEntityManager().save(entities);

            Collection<OperateLog> operateLogCollection = new ArrayList<OperateLog>();
            Collection<OperateStat> operateStatCollection = this.createOperateStats(operateLogCollection, TEST_COUNT);
            OperateLog[] operateLogs = operateLogCollection.toArray(new OperateLog[0]);
            this.getEntityManager().save((Object[]) Arrays.copyOfRange(operateLogs, 0, operateLogs.length / 2));
            this.settleService.execute();
            entities = Arrays.copyOfRange(operateLogs, operateLogs.length / 2, operateLogs.length);

            operateStatCollectionMap.put(date, operateStatCollection);
        }
        if (entities != null)
        {
            this.getEntityManager().save(entities);
            this.settleService.execute();
        }
        for (Entry<Date, Collection<OperateStat>> entry : operateStatCollectionMap.entrySet())
        {
            DateUtils.setCurrentDate(entry.getKey());
            this.assetResult(entry.getValue());
        }
    }
}
