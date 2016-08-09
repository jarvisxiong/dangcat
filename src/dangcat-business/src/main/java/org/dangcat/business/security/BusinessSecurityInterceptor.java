package org.dangcat.business.security;

import org.apache.log4j.Logger;
import org.dangcat.business.exceptions.BusinessException;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.Permission;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.interceptor.BeforeInterceptor;

/**
 * 业务安全控制器。
 *
 * @author dangcat
 */
public class BusinessSecurityInterceptor implements BeforeInterceptor {
    protected static final Logger logger = Logger.getLogger(BusinessSecurityInterceptor.class);

    @Override
    public void beforeInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args) throws ServiceException {
        ServicePrincipal servicePrincipal = serviceContext.getServicePrincipal();
        if (logger.isDebugEnabled())
            logger.debug(service.getClass().getSimpleName() + " before invoke " + methodInfo.getName() + " by " + servicePrincipal);

        Permission permission = methodInfo.getPermission();
        if (permission != null) {
            if (!servicePrincipal.hasPermission(permission.getValue())) {
                String methodTitle = serviceContext.getServiceInfo().getMethodTitle(serviceContext.getLocale(), methodInfo.getName());
                throw new BusinessException(BusinessException.PERMISSION_DENY, methodTitle);
            }
        }
    }
}
