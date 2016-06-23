package org.dangcat.examples.stock;

import org.dangcat.framework.service.MainServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.JndiName;

@JndiName
public class StockMainService extends MainServiceBase
{
    public StockMainService(ServiceProvider parent)
    {
        super(parent);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        // 用户信息服务。
        UserInfoServiceImpl userInfoService = new UserInfoServiceImpl(this);
        this.addService(UserInfoService.class, userInfoService);
        userInfoService.initialize();

        // 记账服务
        AccountingServiceImpl accountingService = new AccountingServiceImpl(this);
        this.addService(AccountingService.class, accountingService);
        accountingService.initialize();
    }
}
