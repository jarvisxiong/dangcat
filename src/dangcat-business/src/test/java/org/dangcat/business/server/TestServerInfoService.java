package org.dangcat.business.server;

import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.junit.Before;
import org.junit.Test;

/**
 * The service test for ServerInfo
 *
 * @author
 */
public class TestServerInfoService extends BusinessServiceTestBase<ServerInfoService, ServerInfoQuery, ServerInfoQuery, ServerInfoFilter> {
    private static final int TEST_COUNT = 100;

    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator) {
        databaseSimulator.add(new ServerInfoSimulator(), TEST_COUNT);
    }

    @Before
    @Override
    public void initialize() {
        // 添加要测试的服务。
        this.addService(ServerInfoService.class, ServerInfoServiceImpl.class);
        super.initialize();
    }

    @Test
    public void testFilter() throws ServiceException {
        TestServiceQuery<ServerInfoQuery, ServerInfoQuery, ServerInfoFilter> testServiceQuery = new TestServiceQuery<ServerInfoQuery, ServerInfoQuery, ServerInfoFilter>(this.getBusinessService());
        QueryAssert<ServerInfoFilter> queryAssert = new QueryAssert<ServerInfoFilter>(ServerInfoQuery.class);
        ServerInfoFilter serverInfoFilter = new ServerInfoFilter();
        queryAssert.setDataFilter(serverInfoFilter);

        this.testFilter_Ip(testServiceQuery, queryAssert, serverInfoFilter);
    }

    private void testFilter_Ip(TestServiceQuery<ServerInfoQuery, ServerInfoQuery, ServerInfoFilter> testServiceQuery, QueryAssert<ServerInfoFilter> queryAssert, ServerInfoFilter serverInfoFilter)
            throws ServiceException {
        for (Object entity : this.loadSamples(ServerInfo.class)) {
            ServerInfo serverInfo = (ServerInfo) entity;
            serverInfoFilter.setIp(serverInfo.getIp());
            FilterGroup filterGroup = new FilterGroup();
            filterGroup.add(new FilterUnit(ServerInfoQuery.Ip, FilterType.like, serverInfo.getIp()));
            queryAssert.setExpectFilterExpress(filterGroup);
            this.testFilter(testServiceQuery, queryAssert);
        }
        serverInfoFilter.setIp(null);
    }
}
