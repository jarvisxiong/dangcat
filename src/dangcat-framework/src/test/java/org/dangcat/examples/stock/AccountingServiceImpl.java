package org.dangcat.examples.stock;

import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Service;
import org.dangcat.framework.service.annotation.ServiceXml;

@ServiceXml("stock.accounting.xml")
class AccountingServiceImpl extends ServiceBase implements AccountingService
{
    @Service
    private AuthenticationService authenticationService;
    @Service
    private SettleService settleService;

    public AccountingServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    public AuthenticationService getAuthenticationService()
    {
        return authenticationService;
    }

    public SettleService getSettleService()
    {
        return settleService;
    }

    public UserInfoService getUserInfoService()
    {
        return this.getService(UserInfoService.class);
    }
}
