package org.dangcat.framework.service;

import junit.framework.Assert;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.examples.stock.AccountingService;
import org.dangcat.examples.stock.SettleService;
import org.dangcat.examples.stock.SettleServiceImpl;
import org.dangcat.examples.stock.StockMainService;
import org.dangcat.examples.stock.UserInfoService;
import org.dangcat.framework.EntityResourceInjectProvider;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestServiceBasic
{
    private static StockMainService stockMainService = null;

    @BeforeClass
    public static void initialize()
    {
        ServiceHelper.addInjectProvider(new EntityResourceInjectProvider());

        ServiceFactory serviceFactory = ServiceFactory.createInstance(null);
        stockMainService = new StockMainService(serviceFactory);
        serviceFactory.addService(StockMainService.class, stockMainService);
        stockMainService.initialize();
    }

    private SettleService getSettleService()
    {
        AccountingService accountingService = stockMainService.getService(AccountingService.class);
        return accountingService.getSettleService();
    }

    @Test
    public void testDatabaseInject()
    {
        SettleService settleService = this.getSettleService();
        // 测试数据库配置注入
        Assert.assertNotNull(settleService.getEntityManager());
        Assert.assertNotNull(settleService.getMySqlEntityManager());
        Assert.assertNotNull(settleService.getOracleEntityManager());
        Assert.assertNotNull(settleService.getHsqldbEntityManager());
        Assert.assertNotNull(settleService.getSqlServerEntityManager());
    }

    @Test
    public void testGetService()
    {
        // 测试自身加入的服务
        UserInfoService userInfoService = stockMainService.getService(UserInfoService.class);
        Assert.assertNotNull(userInfoService);

        AccountingService accountingService = stockMainService.getService(AccountingService.class);
        Assert.assertNotNull(accountingService);
        // 测试向上寻找服务
        Assert.assertNotNull(accountingService.getUserInfoService());
    }

    @Test
    public void testJndiName()
    {
        ServiceLocator serviceLocator = ServiceFactory.getServiceLocator();
        Object accountingService = serviceLocator.lookup("User/AccountingService");
        Assert.assertNotNull(accountingService);
        Assert.assertTrue(accountingService instanceof AccountingService);
        Object stockMainService = serviceLocator.lookup("Default/StockMainService");
        Assert.assertNotNull(stockMainService);
        Assert.assertTrue(stockMainService == TestServiceBasic.stockMainService);
        Object userInfoService = serviceLocator.lookup("Default/UserInfoService");
        Assert.assertNotNull(userInfoService);
        Assert.assertTrue(userInfoService instanceof UserInfoService);
        Object settleService = serviceLocator.lookup("Default/SettleService");
        Assert.assertNotNull(settleService);
        Assert.assertTrue(settleService instanceof SettleService);
    }

    @Test
    public void testPropertiesConfig()
    {
        // 测试属性注入
        SettleServiceImpl settleServiceImpl = (SettleServiceImpl) this.getSettleService();
        Assert.assertNotNull(settleServiceImpl);
        Assert.assertEquals("Name A", settleServiceImpl.getName());
        Assert.assertEquals(ValueUtils.parseDate("2012-05-10 08:30:00"), settleServiceImpl.getDateTime());
        Assert.assertEquals(999.99, settleServiceImpl.getTotal());
        Assert.assertEquals(true, settleServiceImpl.isResult());
        Assert.assertEquals(100, settleServiceImpl.getCount().intValue());
    }

    @Test
    public void testXmlInject()
    {
        AccountingService accountingService = stockMainService.getService(AccountingService.class);
        // 测试XML配置注入的服务
        Assert.assertNotNull(accountingService.getSettleService());
    }
}
