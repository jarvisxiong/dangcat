package org.dangcat.business.simulate;

import org.dangcat.business.domain.AccountBill;
import org.dangcat.business.domain.AccountInfo;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class AccountBillSimulator extends EntityData<AccountBill>
{
    public AccountBillSimulator()
    {
        super(AccountBill.class);
    }

    @Override
    public void create()
    {
        ValueSimulator accountIdSimulator = this.getDataSimulator().findValueSimulator(AccountBill.AccountId);
        accountIdSimulator.bind(AccountInfo.class, AccountInfo.Id);

        super.create();
    }
}
