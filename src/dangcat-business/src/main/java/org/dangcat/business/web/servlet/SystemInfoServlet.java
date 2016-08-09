package org.dangcat.business.web.servlet;

import org.dangcat.business.systeminfo.SystemInfoService;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.web.servlet.ResponseUtils;
import org.dangcat.web.servlet.ServiceServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 系统信息。
 *
 * @author dangcat
 */
public class SystemInfoServlet extends ServiceServletBase {
    private static final long serialVersionUID = 1L;

    @Override
    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = request;
        HttpSession httpSession = httpServletRequest.getSession();

        ServicePrincipal servicePrincipal = (ServicePrincipal) httpSession.getAttribute(ServicePrincipal.class.getSimpleName());
        if (servicePrincipal != null && !servicePrincipal.isValid())
            servicePrincipal = null;

        ResponseUtils.createServiceContext(request, response, servicePrincipal);

        SystemInfoService systemInfoService = ServiceFactory.getServiceLocator().getService(SystemInfoService.class);
        ResponseUtils.responseResult(response, systemInfoService.loadSystemInfo());
    }
}
