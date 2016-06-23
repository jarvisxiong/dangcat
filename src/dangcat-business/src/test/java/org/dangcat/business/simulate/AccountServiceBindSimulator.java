package org.dangcat.business.simulate;

import org.dangcat.business.domain.AccountBill;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.domain.AccountServiceBind;
import org.dangcat.business.domain.ServiceInfo;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class AccountServiceBindSimulator extends EntityData<AccountServiceBind> {
    public AccountServiceBindSimulator() {
        super(AccountServiceBind.class);
    }

    @Override
    public void create() {
        ValueSimulator accountIdSimulator = this.getDataSimulator().findValueSimulator(AccountBill.AccountId);
        accountIdSimulator.bind(AccountInfo.class, AccountInfo.Id);

        ValueSimulator serviceIdSimulator = this.getDataSimulator().findValueSimulator(AccountServiceBind.ServiceId);
        serviceIdSimulator.bind(ServiceInfo.class, ServiceInfo.Id);

        super.create();
    }
}
