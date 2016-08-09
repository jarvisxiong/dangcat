package org.dangcat.business.staff.security;

import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.boot.security.impl.LoginServiceBase;
import org.dangcat.boot.security.impl.LoginUser;
import org.dangcat.boot.service.impl.EntityBatchServiceImpl;
import org.dangcat.business.staff.domain.OperateLog;
import org.dangcat.business.staff.domain.RoleBasic;
import org.dangcat.business.staff.domain.RoleInfo;
import org.dangcat.business.staff.domain.RolePermission;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.persistence.batch.EntityBatchStorer;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.entity.EntityUtils;

import java.util.Collection;
import java.util.List;

/**
 * 操作员业务安全服务。
 *
 * @author dangcat
 */
public class OperatorSecurityServiceImpl extends LoginServiceBase {
    private static final String SECURITY_NAME = "OperatorSecurity";

    public OperatorSecurityServiceImpl() {
        super(SECURITY_NAME);
    }

    /**
     * 建立默认的角色对象。
     *
     * @param name 角色名称。
     */
    private void createDefaultRole(String name) {
        if (!EntityUtils.exists(RoleInfo.class))
            return;

        List<RoleBasic> roleList = this.getEntityManager().load(RoleBasic.class, new String[]{RoleBasic.Name}, name);
        if (roleList == null || roleList.size() == 0) {
            RoleInfo roleInfo = new RoleInfo();
            roleInfo.setName(name);
            this.restoreDefaultPermissions(roleInfo);
            this.getEntityManager().save(roleInfo);
        }
    }

    protected LoginUser createLoginUser(LoginOperator loginOperator) {
        LoginUser loginUser = new LoginUser(loginOperator.getId(), loginOperator.getNo(), loginOperator.getName(), loginOperator.getRoleName(), loginOperator.getPassword(), SECURITY_NAME);
        Collection<RolePermission> rolePermissions = loginOperator.getRolePermissions();
        if (rolePermissions != null && rolePermissions.size() > 0) {
            for (RolePermission rolePermission : rolePermissions)
                loginUser.getPermissions().add(rolePermission.getPermissionId());
        }
        return loginUser;
    }

    private void createOperateLog(LoginUser loginUser, Object result) {
        ServiceContext serviceContext = ServiceContext.getInstance();
        MethodInfo methodInfo = serviceContext.getParam(MethodInfo.class);

        OperateLog operateLog = new OperateLog();
        operateLog.setOperatorId(loginUser.getId());
        operateLog.setMethodId(methodInfo.getValue());
        String clientIp = SecurityUtils.getRemoteHost();
        if (ValueUtils.isEmpty(clientIp))
            operateLog.setIpAddress("127.0.0.1");
        else
            operateLog.setIpAddress(clientIp);
        if (result instanceof ServiceException) {
            ServiceException serviceException = (ServiceException) result;
            operateLog.setErrorCode(serviceException.getMessageId());
        } else
            operateLog.setErrorCode(OperateLog.SUCCESS);
        operateLog.setRemark("No=" + loginUser.getNo());

        EntityBatchStorer entityBatchStorer = EntityBatchServiceImpl.getInstance().getEntityBatchStorer();
        entityBatchStorer.save(operateLog);
    }

    protected EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open();
    }

    @Override
    public void initialize() {
        Collection<String> roles = PermissionManager.getInstance().getRoles();
        if (roles != null && roles.size() > 0) {
            for (String role : roles)
                this.createDefaultRole(role);
        }
    }

    /**
     * 载入指定账号的登录用户。
     *
     * @throws SecurityLoginException
     */
    @Override
    public LoginUser load(String no) throws SecurityLoginException {
        LoginUser loginUser = null;
        if (!ValueUtils.isEmpty(no)) {
            LoginOperator loginOperator = this.loadLoginOperator(no);
            if (loginOperator != null) {
                if (!loginOperator.getUseAble())
                    throw new SecurityLoginException(SecurityLoginException.FIELDNAME_NO, SecurityLoginException.NO_USEABLE_FALSE);

                if (loginOperator.getExpiryTime() != null && DateUtils.compare(DateUtils.now(), loginOperator.getExpiryTime()) > 0)
                    throw new SecurityLoginException(SecurityLoginException.FIELDNAME_NO, SecurityLoginException.NO_EXPIRYTIME);

                loginUser = this.createLoginUser(loginOperator);
            }
        }
        return loginUser;
    }

    protected LoginOperator loadLoginOperator(String no) {
        LoginOperator found = null;
        try {
            List<LoginOperator> loginOperatorList = this.getEntityManager().load(LoginOperator.class, new String[]{LoginOperator.No}, no);
            if (loginOperatorList != null && !loginOperatorList.isEmpty())
                found = loginOperatorList.get(0);
        } catch (Exception e) {
        }
        return found;
    }

    @Override
    public ServicePrincipal login(LoginUser loginUser) {
        ServicePrincipal servicePrincipal = super.login(loginUser);
        if (servicePrincipal != null)
            this.createOperateLog(loginUser, servicePrincipal);
        return servicePrincipal;
    }

    @Override
    public boolean logout(ServicePrincipal servicePrincipal) {
        if (super.logout(servicePrincipal)) {
            LoginUser loginUser = servicePrincipal.getParam(LoginUser.class);
            this.createOperateLog(loginUser, servicePrincipal);
            return true;
        }
        return false;
    }

    @Override
    protected void onError(LoginUser loginUser, SecurityLoginException securityLoginException) {
        this.createOperateLog(loginUser, securityLoginException);
    }

    /**
     * 回复默认的角色权限。
     *
     * @param roleInfo 角色对象。
     * @return
     */
    private int restoreDefaultPermissions(RoleInfo roleInfo) {
        int count = 0;
        Collection<Integer> permissions = PermissionManager.getInstance().getPermissions(roleInfo.getName());
        if (permissions != null && permissions.size() > 0) {
            roleInfo.getRolePermissions().clear();
            for (Integer permissionId : permissions)
                roleInfo.getRolePermissions().add(new RolePermission(roleInfo.getId(), permissionId));
            count = permissions.size();
        }
        return count;
    }
}
