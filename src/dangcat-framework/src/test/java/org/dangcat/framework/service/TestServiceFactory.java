package org.dangcat.framework.service;

import junit.framework.Assert;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.examples.settle.SettleService1;
import org.dangcat.examples.settle.SettleService2;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Proxy;

public class TestServiceFactory {
    private static final String SESSIONID = "SESSIONID";

    @BeforeClass
    public static void initialize() {
        String path = Environment.getHomePath() + "/test-classes/META-INF/stock.testservice.xml";
        File file = new File(FileUtils.decodePath(path));
        ServiceFactory.createInstance(null).load(file);
    }

    @Before
    public void initServiceContext() {
        ServiceContext.set(new ServiceContext(SESSIONID));
    }

    @Test
    public void testSettleService1() {
        SettleService1 settleService1 = ServiceFactory.getServiceLocator().lookup("Default/SettleService1");
        Assert.assertNotNull(settleService1);
        Assert.assertTrue(Proxy.isProxyClass(settleService1.getClass()));

        settleService1.setValue1(100);
        Assert.assertEquals(30000, settleService1.getValue1().intValue());
        Assert.assertEquals(settleService1.getValue(), settleService1.getValue1());

        ReflectUtils.invoke(settleService1, "setValue1", 100);
        Object value = ReflectUtils.invoke(settleService1, "getValue");
        Object value1 = ReflectUtils.invoke(settleService1, "getValue1");
        Assert.assertEquals(30000, value1);
        Assert.assertEquals(value, value1);
    }

    @Test
    public void testSettleService2() {
        SettleService2 settleService2 = ServiceFactory.getServiceLocator().lookup("Default/SettleService2");
        Assert.assertNotNull(settleService2);
        Assert.assertTrue(Proxy.isProxyClass(settleService2.getClass()));

        settleService2.setValue2(10);
        Assert.assertEquals(6000, settleService2.getValue2().intValue());
        Assert.assertEquals(settleService2.getValue(), settleService2.getValue2());

        ReflectUtils.invoke(settleService2, "setValue2", 10);
        Object value = ReflectUtils.invoke(settleService2, "getValue");
        Object value2 = ReflectUtils.invoke(settleService2, "getValue2");
        Assert.assertEquals(6000, value2);
        Assert.assertEquals(value, value2);

        settleService2.setValue3(30);
        Assert.assertEquals(9000, settleService2.getValue3().intValue());
    }

    @Test
    public void testSettleServiceInfo() {
        ServiceInfo serviceInfo1 = ServiceFactory.getServiceLocator().getServiceInfo("Default/SettleService2");
        ServiceInfo serviceInfo2 = ServiceFactory.getServiceLocator().getServiceInfo("SettleService2");
        Assert.assertEquals(serviceInfo1, serviceInfo2);
    }
}
