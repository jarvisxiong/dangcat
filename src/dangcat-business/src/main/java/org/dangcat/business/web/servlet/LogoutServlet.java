package org.dangcat.business.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.servlet.ServiceServletBase;
import org.dangcat.web.servlet.ResponseUtils;

/**
 * 系统登出入口。
 * @author dangcat
 * 
 */
public class LogoutServlet extends ServiceServletBase
{
    private static final String METHOD_NAME = "logout";
    private static final long serialVersionUID = 1L;

    @Override
    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        HttpSession httpSession = request.getSession();
        ServicePrincipal servicePrincipal = (ServicePrincipal) httpSession.getAttribute(ServicePrincipal.class.getSimpleName());
        if (servicePrincipal != null && servicePrincipal.isValid())
        {
            ResponseUtils.createServiceContext(request, response, null);
            ServiceInfo serviceInfo = ServiceFactory.getInstance().getServiceInfo(SecurityLoginService.class);
            MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(METHOD_NAME);
            ServiceContext.getInstance().addParam(MethodInfo.class, methodInfo);
            serviceInfo.invoke(methodInfo.getName(), servicePrincipal);
            httpSession.removeAttribute(ServicePrincipal.class.getSimpleName());
        }
        httpSession.invalidate();
    }
}
