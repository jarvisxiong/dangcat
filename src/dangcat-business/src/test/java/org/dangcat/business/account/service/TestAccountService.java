package org.dangcat.business.account.service;

import org.dangcat.business.account.service.impl.AccountServiceImpl;
import org.dangcat.business.domain.AccountBasic;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.simulate.AccountBillSimulator;
import org.dangcat.business.simulate.AccountInfoSimulator;
import org.dangcat.business.simulate.AccountServiceBindSimulator;
import org.dangcat.business.simulate.GroupInfoSimulator;
import org.dangcat.business.simulate.ServiceInfoSimulator;
import org.dangcat.business.test.QueryAssert;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.business.test.TestServiceQuery;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.junit.Before;
import org.junit.Test;

/**
 * 账户管理服务测试。
 * @author dangcat
 * 
 */
public class TestAccountService extends BusinessServiceTestBase<AccountService, AccountBasic, AccountInfo, AccountFilter>
{
    private static final int TEST_COUNT = 100;

    /**
     * 初始化需要使用的数据库配置。
     */
    @Override
    protected void initDatabaseSimulator(DatabaseSimulator databaseSimulator)
    {
        databaseSimulator.add(new GroupInfoSimulator(), 10);
        databaseSimulator.add(new ServiceInfoSimulator(), 10);
        databaseSimulator.add(new AccountInfoSimulator(), TEST_COUNT);
        databaseSimulator.add(new AccountServiceBindSimulator(), TEST_COUNT);
        databaseSimulator.add(new AccountBillSimulator(), TEST_COUNT * 2);
    }

    @Before
    @Override
    public void initialize()
    {
        // 添加要测试的服务。
        this.addService(AccountService.class, AccountServiceImpl.class);

        super.initialize();
    }

    @Test
    public void testDefaultNew() throws ServiceException
    {
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setName("Monkey hou");
        accountInfo.setAddress("中国深圳");
        accountInfo.setEmail("houxx@h3c.com.cn");
        accountInfo.setBalance(100.0);
        accountInfo.setGroupId(0);
        accountInfo.setRegisterTime(DateUtils.clear(DateUtils.SECOND, DateUtils.now()));
        accountInfo.setTel("0123456789");
        this.testDefaultNew(AccountInfo.class, accountInfo);
    }

    @Test
    public void testDelete() throws ServiceException
    {
        this.testDelete(AccountInfo.class, TEST_COUNT);
    }

    @Test
    public void testQuery() throws ServiceException
    {
        TestServiceQuery<AccountBasic, AccountInfo, AccountFilter> testServiceQuery = new TestServiceQuery<AccountBasic, AccountInfo, AccountFilter>(this.getBusinessService());
        QueryAssert<AccountFilter> queryAssert = new QueryAssert<AccountFilter>(AccountBasic.class);
        queryAssert.setDataFilter(new AccountFilter());
        queryAssert.setExpectTotaleSize(TEST_COUNT);
        this.testQuery(testServiceQuery, queryAssert);
    }

    @Test
    public void testSave() throws ServiceException
    {
        this.testSave(AccountInfo.class, TEST_COUNT);
    }

    @Test
    public void testView() throws ServiceException
    {
        this.testView(AccountInfo.class, TEST_COUNT);
    }
}
