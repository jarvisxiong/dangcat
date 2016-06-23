package org.dangcat.boot.security.impl;

import org.apache.log4j.Logger;
import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.framework.service.ServicePrincipal;

import java.util.HashMap;
import java.util.Map;

/**
 * 登陆服务基类。
 * @author dangcat
 * 
 */
public abstract class LoginServiceBase
{
    public final Logger logger = Logger.getLogger(this.getClass());
    private String name = null;
    private Map<String, ServicePrincipal> userOnlineMap = new HashMap<String, ServicePrincipal>();

    public LoginServiceBase(String name)
    {
        this.name = name;
    }

    private void beforeLogin(String onlineKey)
    {
        ServicePrincipal servicePrincipal = null;
        synchronized (this.userOnlineMap)
        {
            servicePrincipal = this.userOnlineMap.get(onlineKey);
        }
        if (servicePrincipal != null)
            this.logout(servicePrincipal);
    }

    public String getName()
    {
        return this.name;
    }

    protected String getOnlineKey(ServicePrincipal servicePrincipal)
    {
        return servicePrincipal.getNo();
    }

    protected SignResolveProvider getSignResolveProvider()
    {
        return null;
    }

    public int getSize()
    {
        return this.userOnlineMap.size();
    }

    public abstract void initialize();

    public boolean isOnline(String no)
    {
        synchronized (this.userOnlineMap)
        {
            return this.userOnlineMap.containsKey(no);
        }
    }

    public abstract LoginUser load(String no) throws SecurityLoginException;

    public ServicePrincipal login(LoginUser loginUser)
    {
        String remoteHost = SecurityUtils.getRemoteHost();
        ServicePrincipal servicePrincipal = new ServicePrincipal(loginUser.getNo(), loginUser.getName(), loginUser.getRole(), remoteHost, loginUser.getType(), loginUser.getPermissions());
        servicePrincipal.addParam(LoginUser.class, loginUser);
        String onlineKey = this.getOnlineKey(servicePrincipal);
        this.beforeLogin(onlineKey);
        synchronized (this.userOnlineMap)
        {
            this.userOnlineMap.put(onlineKey, servicePrincipal);
        }
        this.logger.info("login: " + servicePrincipal);
        return servicePrincipal;
    }

    public boolean logout(ServicePrincipal servicePrincipal)
    {
        boolean result = false;
        String onlineKey = this.getOnlineKey(servicePrincipal);
        synchronized (this.userOnlineMap)
        {
            if (this.userOnlineMap.containsKey(onlineKey))
            {
                this.userOnlineMap.get(onlineKey).setValid(false);
                this.userOnlineMap.remove(onlineKey);
                result = true;
            }
        }
        if (result)
        {
            this.logger.info("logout: " + servicePrincipal);
            servicePrincipal.setValid(false);
        }
        return result;
    }

    protected void onError(LoginUser loginUser, SecurityLoginException securityLoginException)
    {
    }

    public LoginUser signin(String signId) throws SecurityLoginException
    {
        LoginUser loginUser = null;
        SignResolveProvider signResolveProvider = this.getSignResolveProvider();
        if (signResolveProvider != null)
            loginUser = signResolveProvider.parseLoginUser(signId);
        return loginUser;
    }
}
