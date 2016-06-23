package org.dangcat.framework.service;

import org.dangcat.commons.utils.DateUtils;

import java.util.Collection;
import java.util.Date;

/**
 * 服务委托信息。
 * @author Administrator
 * 
 */
public class ServicePrincipal extends ServiceParams
{
    private static final long serialVersionUID = 1L;
    private String clientIp = null;
    private boolean isValid = true;
    private Date loginTime = DateUtils.now();
    private String name = null;
    private String no = null;
    private Collection<Integer> permissions = null;
    private String roleName = null;
    private String type;

    public ServicePrincipal(String no, String roleName, String type)
    {
        this(no, no, roleName, null, type, null);
    }

    public ServicePrincipal(String no, String name, String roleName, String clientIp, String type, Collection<Integer> permissions)
    {
        this.no = no;
        this.name = name;
        this.roleName = roleName;
        this.clientIp = clientIp;
        this.type = type;
        this.permissions = permissions;
    }

    public String getClientIp()
    {
        return this.clientIp;
    }

    public Date getLoginTime()
    {
        return this.loginTime;
    }

    public String getName()
    {
        return this.name;
    }

    public String getNo()
    {
        return this.no;
    }

    public String getRoleName()
    {
        return this.roleName;
    }

    public String getType()
    {
        return this.type;
    }

    public boolean hasPermission(Integer permission)
    {
        if (this.permissions == null)
            return true;
        return this.permissions.contains(permission);
    }

    public boolean isValid()
    {
        return this.isValid;
    }

    public void setValid(boolean isValid)
    {
        this.isValid = isValid;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("No: ");
        info.append(this.getNo());
        info.append(", Name: ");
        info.append(this.getName());
        info.append(", Role: ");
        info.append(this.getRoleName());
        info.append(", ClientIp: ");
        info.append(this.getClientIp());
        info.append(", Valid: ");
        info.append(this.isValid);
        info.append(", Type: ");
        info.append(this.getType());
        return info.toString();
    }
}
