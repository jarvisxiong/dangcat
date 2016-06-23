package org.dangcat.business.staff.domain.simulate;

import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.domain.RolePermission;
import org.dangcat.persistence.simulate.data.ValueSimulator;
import org.dangcat.persistence.simulate.table.EntityData;

public class RolePermissionSimulator extends EntityData<RolePermission> {
    public RolePermissionSimulator() {
        super(RolePermission.class);
    }

    @Override
    public void create() {
        ValueSimulator roleIdSimulator = this.getDataSimulator().findValueSimulator(RolePermission.RoleId);
        roleIdSimulator.bind(RoleInfo.class, RoleInfo.Id);

        super.create();
    }
}