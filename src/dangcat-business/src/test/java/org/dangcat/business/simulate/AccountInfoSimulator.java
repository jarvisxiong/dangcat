package org.dangcat.business.simulate;

import org.dangcat.business.domain.AccountInfo;
import org.dangcat.business.domain.GroupInfo;
import org.dangcat.persistence.simulate.EntitySimulator;
import org.dangcat.persistence.simulate.data.NumberSimulator;
import org.dangcat.persistence.simulate.data.StringSimulator;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class AccountInfoSimulator extends EntityData<AccountInfo>
{
    public AccountInfoSimulator()
    {
        super(AccountInfo.class);
    }

    @Override
    public void create()
    {
        ValueSimulator groupIdSimulator = this.getDataSimulator().findValueSimulator(AccountInfo.GroupId);
        groupIdSimulator.bind(GroupInfo.class, GroupInfo.Id);

        StringSimulator groupNameSimulator = this.getDataSimulator().findValueSimulator(AccountInfo.GroupName);
        groupNameSimulator.bind(GroupInfo.class, GroupInfo.Name);

        super.create();
    }

    @Override
    protected void initEntitySimulator(EntitySimulator entitySimulator)
    {
        StringSimulator nameSimulator = entitySimulator.findValueSimulator(AccountInfo.Name);
        nameSimulator.setPrefix("Account Name ");

        NumberSimulator balanceSimulator = entitySimulator.findValueSimulator(AccountInfo.Balance);
        balanceSimulator.setInitValue(50.0);
        balanceSimulator.setStep(0.001);

        NumberSimulator minBalanceSimulator = entitySimulator.findValueSimulator(AccountInfo.MinBalance);
        minBalanceSimulator.setInitValue(10.0);
        minBalanceSimulator.setStep(0.001);

        NumberSimulator maxBalanceSimulator = entitySimulator.findValueSimulator(AccountInfo.MaxBalance);
        maxBalanceSimulator.setInitValue(10.0);
        maxBalanceSimulator.setStep(0.001);

        NumberSimulator sexSimulator = entitySimulator.findValueSimulator(AccountInfo.Sex);
        sexSimulator.setValues(new Object[] { 0, 1 });
    }
}
