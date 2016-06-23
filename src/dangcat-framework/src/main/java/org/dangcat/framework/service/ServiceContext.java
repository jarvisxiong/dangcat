package org.dangcat.framework.service;

import org.dangcat.commons.reflect.Permission;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.service.impl.ServiceInfo;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

public class ServiceContext extends ServiceParams
{
    private static final long serialVersionUID = 1L;
    private static ThreadLocal<ServiceContext> ThreadLocal = new ThreadLocal<ServiceContext>();
    private Date beginTime = DateUtils.now();
    private ServicePrincipal servicePrincipal = null;
    private String sessionId = null;

    public ServiceContext(String sessionId)
    {
        this.sessionId = sessionId;
    }
    public ServiceContext(String sessionId, Locale locale)
    {
        this(sessionId, null, locale);
    }
    public ServiceContext(String sessionId, ServicePrincipal servicePrincipal, Locale locale)
    {
        this.sessionId = sessionId;
        this.servicePrincipal = servicePrincipal;
        Environment.setLocale(locale);
    }

    public static ServiceContext getInstance() {
        return ThreadLocal.get();
    }

    public static void remove() {
        ThreadLocal.remove();
        Environment.removeLocale();
    }

    public static void set(ServiceContext serviceContext) {
        ThreadLocal.set(serviceContext);
    }

    public <T> void addSession(Class<T> classType, T value)
    {
        HttpSession httpSession = this.getParam(HttpSession.class);
        if (httpSession != null)
            httpSession.setAttribute(this.getServiceInfo().getJndiName() + "/" + classType.getSimpleName(), value);
    }

    public Date getBeginTime()
    {
        return this.beginTime;
    }

    public Locale getLocale()
    {
        return Environment.getCurrentLocale();
    }

    public ServiceInfo getServiceInfo()
    {
        return this.getParam(ServiceInfo.class);
    }

    public ServicePrincipal getServicePrincipal()
    {
        return this.servicePrincipal;
    }

    @SuppressWarnings("unchecked")
    public <T> T getSession(Class<T> classType)
    {
        T value = null;
        HttpSession httpSession = this.getParam(HttpSession.class);
        if (httpSession != null)
            value = (T) httpSession.getAttribute(this.getServiceInfo().getJndiName() + "/" + classType.getSimpleName());
        return value;
    }

    public String getSessionId()
    {
        return this.sessionId;
    }

    public boolean hasPermission(Integer permissionId)
    {
        ServiceInfo serviceInfo = this.getParam(ServiceInfo.class);
        if (serviceInfo != null && serviceInfo.getPermissionProvider() != null)
        {
            Map<Integer, Permission> permiisionMap = serviceInfo.getPermissionProvider().getPermissionMap();
            if (permiisionMap != null)
            {
                Permission permission = permiisionMap.get(permissionId);
                if (permission != null)
                    return this.getServicePrincipal().hasPermission(permission.getValue());
            }
        }
        return true;
    }

    public void removeSession(Class<?> classType)
    {
        HttpSession httpSession = this.getParam(HttpSession.class);
        if (httpSession != null)
            httpSession.removeAttribute(this.getServiceInfo().getJndiName() + "/" + classType.getSimpleName());
    }
}
