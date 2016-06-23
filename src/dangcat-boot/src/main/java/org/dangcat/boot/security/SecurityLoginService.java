package org.dangcat.boot.security;

import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.JndiName;
import org.dangcat.framework.service.annotation.MethodId;

/**
 * 安全服务接口。
 * @author dangcat
 * 
 */
@JndiName(module = "System", name = "Security")
public interface SecurityLoginService extends ServiceProvider
{
    /**
     * 以指定用户和密码登陆。
     * @param no 用户名。
     * @param password 密码。
     * @return 登陆信息。
     * @throws SecurityLoginException 登陆异常。
     */
    @MethodId(1)
    public ServicePrincipal login(String no, String password) throws SecurityLoginException;

    /**
     * 登出指定用户。
     * @param servicePrincipal 用户信息。
     * @return 登出结果。
     * @throws SecurityLoginException 登陆异常。
     */
    @MethodId(2)
    public boolean logout(ServicePrincipal servicePrincipal) throws SecurityLoginException;

    /**
     * 以签名方式登录。
     * @param signId 登录签名。
     * @return 登陆信息。
     * @throws SecurityLoginException 登陆异常。
     */
    @MethodId(3)
    public ServicePrincipal signin(String signId) throws SecurityLoginException;
}
