package org.dangcat.business.staff.service;

import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.business.staff.domain.simulate.OperateLogSimulator;
import org.dangcat.business.staff.domain.simulate.OperateStatSimulator;
import org.dangcat.business.staff.domain.simulate.OperatorGroupSimulator;
import org.dangcat.business.staff.domain.simulate.OperatorInfoSimulator;
import org.dangcat.business.staff.filter.OperateLogFilter;
import org.dangcat.business.staff.service.impl.OperateLogServiceImpl;
import org.dangcat.business.staff.service.impl.OperatorGroupServiceImpl;
import org.dangcat.business.staff.service.impl.OperatorInfoServiceImpl;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.junit.Before;
import org.junit.Test;

/**
 * The service test for Operator
 *
 * @author dangcat
 */
public class TestOperateLogService extends BusinessServiceTestBase<OperateLogService, OperateStat, OperateStat, OperateLogFilter> {
    private static final int TEST_COUNT = 100;

    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator) {
        databaseSimulator.add(new OperatorGroupSimulator(), 10);
        databaseSimulator.add(new OperatorInfoSimulator(), TEST_COUNT);
        databaseSimulator.add(new OperateStatSimulator(), TEST_COUNT);
        databaseSimulator.add(new OperateLogSimulator(), TEST_COUNT);
    }

    @Before
    @Override
    public void initialize() {
        // 添加要测试的服务。
        this.addService(OperatorGroupService.class, OperatorGroupServiceImpl.class);
        this.addService(OperatorInfoService.class, OperatorInfoServiceImpl.class);
        this.addService(OperateLogService.class, OperateLogServiceImpl.class);

        super.initialize();
    }

    @Test
    public void testQuery() throws ServiceException {
        TestServiceQuery<OperateStat, OperateStat, OperateLogFilter> testServiceQuery = new TestServiceQuery<OperateStat, OperateStat, OperateLogFilter>(this.getBusinessService());
        QueryAssert<OperateLogFilter> queryAssert = new QueryAssert<OperateLogFilter>(OperateStat.class);
        queryAssert.setDataFilter(new OperateLogFilter());
        queryAssert.setExpectTotaleSize(TEST_COUNT);
        this.testQuery(testServiceQuery, OperateStat.OperatorId, queryAssert);
    }

    @Test
    public void testView() throws ServiceException {
        this.testView(OperateStat.class, TEST_COUNT);
    }
}
