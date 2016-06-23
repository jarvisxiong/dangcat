package org.dangcat.framework.service;

import java.io.File;
import java.util.Collection;

import junit.framework.Assert;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.framework.service.xml.ServicesXmlResolver;
import org.dom4j.DocumentException;
import org.junit.Test;

public class TestServiceXmlResolver
{
    private void testInterceptors(Collection<Class<?>> interceptors, int exceptValue)
    {
        Assert.assertEquals(3, interceptors.size());
        Class<?>[] classTypes = interceptors.toArray(new Class<?>[0]);
        for (int i = 0; i < classTypes.length; i++)
            Assert.assertEquals("org.dangcat.examples.settle.Interceptor" + (i + 1), classTypes[i].getName());
    }

    private void testServiceInfo(ServiceInfo serviceInfo, int exceptValue)
    {
        Assert.assertEquals("org.dangcat.examples.settle.SettleService" + exceptValue, serviceInfo.getAccessType());
        Assert.assertEquals("org.dangcat.examples.settle.SettleServiceImpl" + exceptValue, serviceInfo.getServiceType());
        this.testInterceptors(serviceInfo.getInterceptors(), exceptValue);
    }

    @Test
    public void testXmlResolver() throws DocumentException
    {
        String path = Environment.getHomePath() + "/test-classes/META-INF/stock.servicebeans.xml";
        File file = new File(FileUtils.decodePath(path));
        ServicesXmlResolver serviceBeansXmlResolver = new ServicesXmlResolver();
        serviceBeansXmlResolver.open(file);
        serviceBeansXmlResolver.resolve();
        Assert.assertEquals(3, serviceBeansXmlResolver.getServiceInfos().size());
        Assert.assertEquals(3, serviceBeansXmlResolver.getInterceptors().size());

        for (int i = 0; i < serviceBeansXmlResolver.getServiceInfos().size(); i++)
            this.testServiceInfo(serviceBeansXmlResolver.getServiceInfos().get(i), i + 1);
        this.testInterceptors(serviceBeansXmlResolver.getInterceptors(), 4);
    }
}
