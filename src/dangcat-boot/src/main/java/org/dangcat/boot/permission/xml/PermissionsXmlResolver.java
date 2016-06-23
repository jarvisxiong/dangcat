package org.dangcat.boot.permission.xml;

import java.util.Collection;

import org.dangcat.commons.serialize.xml.XmlResolver;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 权限对象解析器。
 * @author dangcat
 * 
 */
public class PermissionsXmlResolver extends XmlResolver
{
    private static final String RESOLVER_NAME = "Permissions";

    /**
     * 构建解析器。
     */
    public PermissionsXmlResolver()
    {
        super(RESOLVER_NAME);
    }

    @SuppressWarnings("unchecked")
    public Collection<String> getPermissions()
    {
        return (Collection<String>) super.getResolveObject();
    }

    @Override
    protected void resolveElementText(String value)
    {
        if (!ValueUtils.isEmpty(value))
            this.getPermissions().add(value);
        super.resolveElementText(value);
    }
}
