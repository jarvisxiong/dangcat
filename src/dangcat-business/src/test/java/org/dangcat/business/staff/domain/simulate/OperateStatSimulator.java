package org.dangcat.business.staff.domain.simulate;

import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.OperateStat;
import org.dangcat.business.staff.domain.OperatorInfo;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class OperateStatSimulator extends EntityData<OperateStat> {
    public OperateStatSimulator() {
        super(OperateStat.class);
    }

    @Override
    public void create() {
        ValueSimulator operatorIdSimulator = this.getDataSimulator().findValueSimulator(OperateLog.OperatorId);
        operatorIdSimulator.bind(OperatorInfo.class, OperatorInfo.Id);

        super.create();
    }
}