package org.dangcat.boot.permission.xml;

import org.dangcat.boot.permission.Module;
import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.commons.serialize.xml.XmlResolver;

import java.util.Collection;

/**
 * 权限管理解析器。
 *
 * @author dangcat
 */
public class PermissionManagerXmlResolver extends XmlResolver {
    private static final String RESOLVER_NAME = "Permissions";
    /**
     * 权限管理。
     */
    private PermissionManager permissionManager;

    /**
     * 构建解析器。
     */
    public PermissionManagerXmlResolver(PermissionManager permissionManager) {
        super(RESOLVER_NAME);
        this.addChildXmlResolver(new ModuleXmlResolver());
        this.addChildXmlResolver(new RolesXmlResolver());
        this.permissionManager = permissionManager;
    }

    /**
     * 产生子元素对象。
     *
     * @param elementName 子元素名称。
     * @param child       子元素对象。
     */
    @SuppressWarnings("unchecked")
    protected void afterChildCreate(String elementName, Object child) {
        if (child instanceof Module)
            this.permissionManager.addModule((Module) child);
        else if (elementName.equalsIgnoreCase(RolesXmlResolver.RESOLVER_NAME))
            this.permissionManager.setRolePermissions((Collection<RolePermission>) child);
    }
}
