package org.dangcat.web.filter;

import org.apache.log4j.Logger;
import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.servlet.ResponseUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ServiceContextFilter implements Filter
{
    private static final Logger logger = Logger.getLogger(ServiceContextFilter.class);
    private static final String METHOD_NAME = "signin";
    private static final String SIGNID = "SIGNID";

    private ServicePrincipal createServicePrincipal(HttpServletRequest httpServletRequest)
    {
        ServicePrincipal servicePrincipal = null;
        String signId = httpServletRequest.getParameter(SIGNID);
        try
        {
            ServiceInfo serviceInfo = ServiceFactory.getInstance().getServiceInfo(SecurityLoginService.class);
            MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(METHOD_NAME);
            ServiceContext.getInstance().addParam(MethodInfo.class, methodInfo);
            servicePrincipal = (ServicePrincipal) serviceInfo.invoke(methodInfo.getName(), signId);
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error(e);
        }
        return servicePrincipal;
    }

    @Override
    public void destroy()
    {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException
    {
        if (request instanceof HttpServletRequest)
        {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpSession httpSession = httpServletRequest.getSession();

            ServicePrincipal servicePrincipal = (ServicePrincipal) httpSession.getAttribute(ServicePrincipal.class.getSimpleName());
            if (servicePrincipal == null)
            {
                ResponseUtils.createServiceContext(httpServletRequest, (HttpServletResponse) response, null);
                servicePrincipal = this.createServicePrincipal(httpServletRequest);
            }

            if (servicePrincipal == null || !servicePrincipal.isValid())
            {
                SecurityLoginException securityLoginException = new SecurityLoginException(SecurityLoginException.INVALID_LOGIN);
                ResponseUtils.responseException((HttpServletResponse) response, securityLoginException);
                return;
            }

            HttpServletResponse httpServletResponse = (HttpServletResponse) response;
            ResponseUtils.createServiceContext(httpServletRequest, httpServletResponse, servicePrincipal);
        }
        try
        {
            filterChain.doFilter(request, response);
        }
        finally
        {
            ServiceContext.remove();
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException
    {
    }
}
