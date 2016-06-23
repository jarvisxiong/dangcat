package org.dangcat.business.staff.domain.simulate;

import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.persistence.simulate.data.DateSimulator;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class OperateLogSimulator extends EntityData<OperateLog>
{
    public OperateLogSimulator()
    {
        super(OperateLog.class);
    }

    @Override
    public void create()
    {
        ValueSimulator operatorIdSimulator = this.getDataSimulator().findValueSimulator(OperateLog.OperatorId);
        operatorIdSimulator.bind(OperatorInfo.class, OperatorInfo.Id);

        DateSimulator dateTimeSimulator = this.getDataSimulator().findValueSimulator(OperateLog.DateTime);
        dateTimeSimulator.setStep(0);

        super.create();
    }
}