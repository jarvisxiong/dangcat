package org.dangcat.boot.permission.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

/**
 * 角色对象解析器。
 *
 * @author dangcat
 */
public class RoleXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Role";
    private static final String ROLE_PERMISSION_NAME = "name";
    /**
     * 角色权限。
     */
    private RolePermission rolePermission = null;

    /**
     * 构建解析器。
     */
    public RoleXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new PermissionsXmlResolver());
    }

    @Override
    protected void resolveAttribute(String name, String value) {
        if (ROLE_PERMISSION_NAME.equalsIgnoreCase(name)) {
            this.rolePermission = new RolePermission(value);
            this.setResolveObject(this.rolePermission);
        }
        super.resolveAttribute(name, value);
    }
}
