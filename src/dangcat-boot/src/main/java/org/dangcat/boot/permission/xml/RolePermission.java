package org.dangcat.boot.permission.xml;

import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.boot.service.impl.ServiceCalculator;

import java.util.Collection;
import java.util.HashSet;

public class RolePermission {
    private String name = null;
    private Collection<String> permissions = new HashSet<String>();

    public RolePermission(String name) {
        this.name = name;
    }

    public void createPermissions(PermissionManager permissionManager) {
        Collection<Integer> permissions = new HashSet<Integer>();
        for (String permissionText : this.getPermissions()) {
            if (permissionText.equals("*")) {
                permissions.clear();
                permissions.addAll(permissionManager.getMethodPermissions());
                break;
            }

            Integer permission = ServiceCalculator.parse(permissionText);
            if (permission != null) {
                Integer beginNumber = ServiceCalculator.getSortNumber(permission);
                Integer endNumber = ServiceCalculator.getSortNumber(permission + 1);
                for (Integer methodPermission : permissionManager.getMethodPermissions()) {
                    if (methodPermission >= beginNumber && methodPermission < endNumber)
                        permissions.add(methodPermission);
                }
            }
        }
        permissionManager.addPermissions(this.getName(), permissions);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RolePermission other = (RolePermission) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getName() {
        return this.name;
    }

    public Collection<String> getPermissions() {
        return this.permissions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }
}
