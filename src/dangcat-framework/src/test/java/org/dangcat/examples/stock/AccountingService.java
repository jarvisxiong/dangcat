package org.dangcat.examples.stock;

import org.dangcat.framework.service.annotation.JndiName;

@JndiName(name = "AccountingService", module = "User")
public interface AccountingService
{
    public AuthenticationService getAuthenticationService();

    public SettleService getSettleService();

    public UserInfoService getUserInfoService();
}
