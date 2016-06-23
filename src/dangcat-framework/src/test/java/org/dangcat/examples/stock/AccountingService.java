package org.dangcat.examples.stock;

import org.dangcat.framework.service.annotation.JndiName;

@JndiName(name = "AccountingService", module = "User")
public interface AccountingService
{
    AuthenticationService getAuthenticationService();

    SettleService getSettleService();

    UserInfoService getUserInfoService();
}
