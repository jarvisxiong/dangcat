package org.dangcat.business.account.service;

import junit.framework.Assert;
import org.dangcat.business.account.service.impl.AccountServiceImpl;
import org.dangcat.business.domain.AccountBasic;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.test.BusinessServiceTestBase;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.persistence.validator.exception.DataValidateException;
import org.junit.Before;
import org.junit.Test;

/**
 * 账户管理服务测试。
 *
 * @author dangcat
 */
public class TestServiceValidator extends BusinessServiceTestBase<AccountService, AccountBasic, AccountInfo, AccountFilter> {
    @Before
    @Override
    public void initialize() {
        // 添加要测试的服务。
        this.addService(AccountService.class, AccountServiceImpl.class);
        super.initialize();
    }

    @Test
    public void testMaxLengthValidator() throws ServiceException {
        AccountInfo accountInfo = this.getService().view(null);
        Assert.assertNotNull(accountInfo);

        accountInfo.setEmail("01234567890123456789012345678901234567890");
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MAXLENTH));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MAXLENTH));
    }

    @Test
    public void testNotNullValidator() throws ServiceException {
        AccountInfo accountInfo = this.getService().view(null);
        Assert.assertNotNull(accountInfo);

        accountInfo.setGroupId(null);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_NOTNULL));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_NOTNULL));
    }

    @Test
    public void testRangeValidator() throws ServiceException {
        AccountInfo accountInfo = this.getService().view(null);
        Assert.assertNotNull(accountInfo);

        accountInfo.setBalance(null);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));

        accountInfo.setBalance(49.99);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));

        accountInfo.setBalance(100.01);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_RANGE));

        accountInfo.setBalance(80.0);

        accountInfo.setMinBalance(9.99);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MINVALUE));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MINVALUE));

        accountInfo.setMinBalance(10.0);

        accountInfo.setMaxBalance(200.01);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MAXVALUE));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_MAXVALUE));
    }

    @Test
    public void testValueMapValidator() throws ServiceException {
        AccountInfo accountInfo = this.getService().view(null);
        Assert.assertNotNull(accountInfo);

        accountInfo.setSex(3);
        this.getService().create(accountInfo);
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_OPTIONS));
        Assert.assertNotNull(accountInfo.findServiceException(DataValidateException.INVALIDATE_OPTIONS));
    }
}
