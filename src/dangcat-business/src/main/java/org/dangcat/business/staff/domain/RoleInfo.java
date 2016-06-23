package org.dangcat.business.staff.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import org.dangcat.business.staff.service.impl.RoleInfoCalculator;
import org.dangcat.business.systeminfo.PermissionInfo;
import org.dangcat.commons.serialize.annotation.Serialize;
import org.dangcat.persistence.annotation.AfterLoad;
import org.dangcat.persistence.annotation.Calculator;
import org.dangcat.persistence.annotation.Relation;
import org.dangcat.persistence.annotation.Table;

@Table
@Calculator(RoleInfoCalculator.class)
public class RoleInfo extends RoleBasic
{
    public static final String Permissions = "Permissions";
    private static final long serialVersionUID = 1L;

    @Relation
    private List<PermissionInfo> permissions = new ArrayList<PermissionInfo>();

    @Relation(parentFieldNames = { Id }, childFieldNames = { RolePermission.RoleId })
    private Collection<RolePermission> rolePermissions = new LinkedHashSet<RolePermission>();

    public RoleInfo()
    {
    }

    @AfterLoad
    public void afterLoad()
    {
        this.permissions.clear();
        Collection<Integer> permissions = new HashSet<Integer>();
        for (RolePermission rolePermission : this.rolePermissions)
        {
            Integer permissionId = rolePermission.getPermissionId();
            if (permissionId != null && !permissions.contains(permissionId))
                this.permissions.add(new PermissionInfo(permissionId));
        }
        Collections.sort(this.permissions);
    }

    public Collection<PermissionInfo> getPermissions()
    {
        return this.permissions;
    }

    @Serialize(ignore = true)
    public Collection<RolePermission> getRolePermissions()
    {
        return this.rolePermissions;
    }
}