package org.dangcat.boot.security.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.boot.security.annotation.ExtendSecurities;
import org.dangcat.boot.security.annotation.ExtendSecurity;
import org.dangcat.boot.security.annotation.LocalSecurity;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.resource.Resources;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.ServiceProvider;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 安全服务。
 *
 * @author dangcat
 */
@Resources({SecurityLoginException.class})
public class SecurityLoginServiceImpl extends ServiceBase implements SecurityLoginService {
    private Collection<LoginServiceBase> loginServiceCollection = new LinkedList<LoginServiceBase>();

    public SecurityLoginServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    @Override
    public void addService(Class<?> classType, Object service) {
        if (service != this) {
            if (LoginServiceBase.class.isAssignableFrom(service.getClass())) {
                LoginServiceBase loginServiceBase = (LoginServiceBase) service;
                loginServiceBase.initialize();
                this.loginServiceCollection.add(loginServiceBase);
            }
        }
        super.addService(classType, service);
    }

    @Override
    public void initialize() {
        super.initialize();

        Object mainService = ApplicationContext.getInstance().getMainService();
        if (mainService != null) {
            // 扩展验证。
            ExtendSecurity extendSecurity = mainService.getClass().getAnnotation(ExtendSecurity.class);
            if (extendSecurity != null)
                this.loadExtendSecurity(extendSecurity.value());
            ExtendSecurities extendSecurities = mainService.getClass().getAnnotation(ExtendSecurities.class);
            if (extendSecurities != null && extendSecurities.value() != null) {
                for (Class<?> classType : extendSecurities.value())
                    this.loadExtendSecurity(classType);
            }

            // 本地验证。
            LocalSecurity localSecurity = mainService.getClass().getAnnotation(LocalSecurity.class);
            if (localSecurity != null) {
                String configName = localSecurity.value();
                if (ValueUtils.isEmpty(configName))
                    configName = ApplicationContext.getInstance().getName() + ".users.properties";
                this.addService(LocalSecurityServiceImpl.class, new LocalSecurityServiceImpl(configName));
            }
        }
    }

    private void loadExtendSecurity(Class<?> classType) {
        if (classType != null && LoginServiceBase.class.isAssignableFrom(classType)) {
            LoginServiceBase loginServiceBase = (LoginServiceBase) ReflectUtils.newInstance(classType);
            if (loginServiceBase != null)
                this.addService(loginServiceBase.getClass(), loginServiceBase);
        }
    }

    /**
     * 以指定用户和密码登陆。
     *
     * @param no       账户名。
     * @param password 密码。
     * @return 登陆信息。
     * @throws SecurityLoginException 登陆异常。
     */
    public ServicePrincipal login(String no, String password) throws SecurityLoginException {
        if (ValueUtils.isEmpty(no))
            throw new SecurityLoginException(SecurityLoginException.FIELDNAME_NO, SecurityLoginException.NO_EMPTY);

        if (ValueUtils.isEmpty(password))
            throw new SecurityLoginException(SecurityLoginException.FIELDNAME_PASSWORD, SecurityLoginException.PASSWORD_EMPTY);

        // 账号是否存在
        LoginServiceBase loginService = null;
        LoginUser loginUser = null;
        for (LoginServiceBase loginServiceBase : this.loginServiceCollection) {
            loginUser = loginServiceBase.load(no);
            if (loginUser != null) {
                loginService = loginServiceBase;
                break;
            }
        }
        if (loginService == null || loginUser == null)
            throw new SecurityLoginException(SecurityLoginException.FIELDNAME_NO, SecurityLoginException.NO_NOT_EXISTS);

        try {
            // 角色是否存在
            if (loginUser.getRole() == null)
                throw new SecurityLoginException(SecurityLoginException.INVALID_ROLE);

            // 密码是否正确
            if (!loginUser.checkPassword(password))
                throw new SecurityLoginException(SecurityLoginException.FIELDNAME_PASSWORD, SecurityLoginException.INVALID_PASSWORD);
        } catch (SecurityLoginException e) {
            loginService.onError(loginUser, e);
            throw e;
        }
        return loginService.login(loginUser);
    }

    /**
     * 登出指定用户。
     *
     * @param servicePrincipal 用户信息。
     * @return 登出结果。
     * @throws SecurityLoginException 登陆异常。
     */
    public boolean logout(ServicePrincipal servicePrincipal) throws SecurityLoginException {
        for (LoginServiceBase loginServiceBase : this.loginServiceCollection) {
            if (loginServiceBase.logout(servicePrincipal))
                return true;
        }
        return false;
    }

    @Override
    public ServicePrincipal signin(String signId) throws SecurityLoginException {
        ServicePrincipal servicePrincipal = null;
        for (LoginServiceBase loginServiceBase : this.loginServiceCollection) {
            LoginUser loginUser = loginServiceBase.signin(signId);
            if (loginUser != null) {
                servicePrincipal = loginServiceBase.login(loginUser);
                break;
            }
        }
        return servicePrincipal;
    }
}
