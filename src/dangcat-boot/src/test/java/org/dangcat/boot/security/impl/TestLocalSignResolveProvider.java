package org.dangcat.boot.security.impl;

import org.dangcat.framework.service.ServiceContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class TestLocalSignResolveProvider
{
    private TestHttpServletRequest httpServletRequest = new TestHttpServletRequest("192.168.5.6");
    private Map<String, LoginUser> localeUserMap = null;
    private SignResolveProvider signResolveProvider = null;

    @Before
    public void initialize()
    {
        Map<String, LoginUser> localeUserMap = new HashMap<String, LoginUser>();
        for (int i = 0; i < 10; i++)
        {
            LoginUser loginUser = new LoginUser("no" + i, "role" + i, "password" + i, "type" + i);
            localeUserMap.put(loginUser.getNo(), loginUser);
        }
        this.localeUserMap = localeUserMap;
        this.signResolveProvider = new LocalSignResolveProvider(localeUserMap);
        ServiceContext serviceContext = new ServiceContext(null);
        ServiceContext.set(serviceContext);
        serviceContext.addParam(HttpServletRequest.class, this.httpServletRequest);
    }

    @Test
    public void testSignResolveProviderError()
    {
        Assert.assertNull(this.signResolveProvider.parseLoginUser(null));
        Assert.assertNull(this.signResolveProvider.parseLoginUser("adsfgdg"));

        LoginUser srcLoginUser = this.localeUserMap.get("no1");
        String signId = this.signResolveProvider.createSignId(srcLoginUser);
        this.httpServletRequest.setRemoteHost("192.168.5.7");
        Assert.assertNull(this.signResolveProvider.parseLoginUser(signId));
    }

    @Test
    public void testSignResolveProviderNormal()
    {
        LoginUser srcLoginUser = this.localeUserMap.get("no1");
        String signId = this.signResolveProvider.createSignId(srcLoginUser);
        Assert.assertNotNull(signId);
        LoginUser destLoginUser = this.signResolveProvider.parseLoginUser(signId);
        Assert.assertNotNull(destLoginUser);
        Assert.assertEquals(srcLoginUser, destLoginUser);
    }
}
