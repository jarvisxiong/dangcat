package org.dangcat.business.staff.security;

import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.boot.security.impl.LoginUser;
import org.dangcat.boot.security.impl.SignResolveProvider;
import org.dangcat.boot.test.ServiceUnitTestBase;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.persistence.entity.EntityManager;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;

public class TestOperatorSignResolveProvider extends ServiceUnitTestBase {
    private EntityManager entityManager = null;
    private TestHttpServletRequest httpServletRequest = new TestHttpServletRequest("192.168.5.6");
    private OperatorSecurityServiceImpl operatorSecurityService = null;
    private SignResolveProvider signResolveProvider = null;

    @Override
    @Before
    public void initialize() {
        super.initialize();

        try {
            this.initEntityTable(RoleBasic.class, OperatorInfoCreate.class);
        } catch (Exception e) {
            this.logger.error(this, e);
        }

        OperatorSecurityServiceImpl operatorSecurityService = new OperatorSecurityServiceImpl();
        this.entityManager = operatorSecurityService.getEntityManager();

        RoleBasic roleInfo = new RoleBasic();
        roleInfo.setName("Name");
        roleInfo.setDescription("Description");
        this.entityManager.save(roleInfo);

        for (int i = 0; i < 10; i++) {
            OperatorInfoCreate operatorInfo = new OperatorInfoCreate();
            operatorInfo.setName("Name" + i);
            operatorInfo.setNo("No" + i);
            operatorInfo.setPassword("Password" + i);
            operatorInfo.setUseAble(Boolean.TRUE);
            operatorInfo.setGroupId(0);
            operatorInfo.setRoleId(roleInfo.getId());
            this.entityManager.delete(OperatorInfo.class, new String[]{OperatorInfo.Name}, operatorInfo.getName());
            this.entityManager.save(operatorInfo);
        }
        this.operatorSecurityService = operatorSecurityService;
        ServiceContext serviceContext = new ServiceContext(null);
        ServiceContext.set(serviceContext);
        serviceContext.addParam(HttpServletRequest.class, this.httpServletRequest);
        this.signResolveProvider = new OperatorSignResolveProvider(operatorSecurityService);
    }

    @Test
    public void testSignResolveProviderError() throws SecurityLoginException {
        Assert.assertNull(this.signResolveProvider.parseLoginUser(null));
        Assert.assertNull(this.signResolveProvider.parseLoginUser("adsfgdg"));

        LoginUser srcLoginUser = this.operatorSecurityService.load("No1");
        String signId = this.signResolveProvider.createSignId(srcLoginUser);
        this.httpServletRequest.setRemoteHost("192.168.5.7");
        Assert.assertNull(this.signResolveProvider.parseLoginUser(signId));
    }

    @Test
    public void testSignResolveProviderNormal() throws SecurityLoginException {
        LoginUser srcLoginUser = this.operatorSecurityService.load("No1");
        String signId = this.signResolveProvider.createSignId(srcLoginUser);
        Assert.assertNotNull(signId);
        LoginUser destLoginUser = this.signResolveProvider.parseLoginUser(signId);
        Assert.assertNotNull(destLoginUser);
        Assert.assertEquals(srcLoginUser, destLoginUser);
    }
}
