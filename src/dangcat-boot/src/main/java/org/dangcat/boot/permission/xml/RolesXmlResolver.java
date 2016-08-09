package org.dangcat.boot.permission.xml;

import org.dangcat.commons.serialize.xml.XmlResolver;

import java.util.Collection;
import java.util.HashSet;

/**
 * 角色对象解析器。
 *
 * @author dangcat
 */
public class RolesXmlResolver extends XmlResolver {
    protected static final String RESOLVER_NAME = "Roles";

    /**
     * 角色权限。
     */
    private Collection<RolePermission> rolePermissions = new HashSet<RolePermission>();

    /**
     * 构建解析器。
     */
    public RolesXmlResolver() {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new RoleXmlResolver());
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    protected void afterChildCreate(String elementName, Object child) {
        if (child instanceof RolePermission)
            this.rolePermissions.add((RolePermission) child);
    }

    @Override
    public Object getResolveObject() {
        return this.rolePermissions;
    }
}
