package org.dangcat.web.listeners;

import org.apache.log4j.Logger;
import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Locale;

public class ServiceSessionListener implements HttpSessionListener
{
    protected static final Logger logger = Logger.getLogger(ServiceSessionListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent)
    {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent)
    {
        try
        {
            HttpSession httpSession = httpSessionEvent.getSession();
            ServicePrincipal servicePrincipal = (ServicePrincipal) httpSession.getAttribute(ServicePrincipal.class.getSimpleName());
            if (servicePrincipal != null && servicePrincipal.isValid())
            {
                ServiceInfo serviceInfo = ServiceFactory.getInstance().getServiceInfo(SecurityLoginService.class);
                MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo("logout");
                Locale locale = (Locale) httpSession.getAttribute(Locale.class.getSimpleName());
                ServiceContext serviceContext = new ServiceContext(httpSession.getId(), servicePrincipal, locale);
                serviceContext.addParam(MethodInfo.class, methodInfo);
                ServiceContext.set(serviceContext);

                SecurityLoginService securityLoginService = ServiceFactory.getInstance().getService(SecurityLoginService.class);
                securityLoginService.logout(servicePrincipal);
            }
        }
        catch (Exception e)
        {
            logger.error(this, e);
        }
    }
}
