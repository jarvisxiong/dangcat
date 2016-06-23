package org.dangcat.business.staff.domain.simulate;

import org.dangcat.business.staff.domain.OperatorGroup;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.business.staff.domain.OperatorInfoCreate;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class OperatorInfoSimulator extends EntityData<OperatorInfoCreate>
{
    public OperatorInfoSimulator()
    {
        super(OperatorInfoCreate.class);
    }

    @Override
    public void create()
    {
        ValueSimulator groupIdSimulator = this.getDataSimulator().findValueSimulator(OperatorInfo.GroupId);
        groupIdSimulator.bind(OperatorGroup.class, OperatorGroup.Id);

        ValueSimulator roleIdSimulator = this.getDataSimulator().findValueSimulator(OperatorInfo.RoleId);
        roleIdSimulator.bind(RoleInfo.class, RoleInfo.Id);

        super.create();
    }
}