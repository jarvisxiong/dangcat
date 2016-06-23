package org.dangcat.business.staff.domain;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataStatus;

@Table
public class RolePermission extends EntityBase implements DataStatus {
    public static final String PermissionId = "PermissionId";
    public static final String RoleId = "RoleId";
    private static final long serialVersionUID = 1L;

    @Column(isPrimaryKey = true, index = 1)
    private Integer permissionId = null;

    @Column(isPrimaryKey = true, isAssociate = true, index = 0)
    private Integer roleId = null;

    public RolePermission() {
    }

    public RolePermission(Integer roleId, Integer permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public Integer getPermissionId() {
        return this.permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

    public Integer getRoleId() {
        return this.roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }
}