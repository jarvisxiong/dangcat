package org.dangcat.business.web.servlet;

import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.boot.security.SecurityUtils;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.utils.LocaleUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServicePrincipal;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.servlet.ResponseUtils;
import org.dangcat.web.servlet.ServiceServletBase;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Locale;

/**
 * 系统登陆入口。
 *
 * @author dangcat
 */
public class LoginServlet extends ServiceServletBase {
    private static final String METHOD_LOGIN = "login";
    private static final String METHOD_SIGNIN = "signin";
    private static final long serialVersionUID = 1L;
    private static final String SIGNID = "SIGNID";

    private ServicePrincipal createServicePrincipal(String methodName, Object... params) throws Exception {
        ServiceInfo serviceInfo = ServiceFactory.getInstance().getServiceInfo(SecurityLoginService.class);
        MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(methodName);
        ServiceContext.getInstance().addParam(MethodInfo.class, methodInfo);
        return (ServicePrincipal) serviceInfo.invoke(methodInfo.getName(), params);
    }

    @Override
    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ResponseUtils.createServiceContext(request, response, null);
        String signId = request.getParameter(SIGNID);
        ServicePrincipal servicePrincipal = null;
        if (!ValueUtils.isEmpty(signId))
            servicePrincipal = this.createServicePrincipal(METHOD_SIGNIN, signId);
        else {
            String no = request.getParameter(SecurityLoginException.FIELDNAME_NO);
            if (!ValueUtils.isEmpty(no))
                no = SecurityUtils.decryptContent(no);

            String password = request.getParameter(SecurityLoginException.FIELDNAME_PASSWORD);
            if (!ValueUtils.isEmpty(password))
                password = SecurityUtils.decryptContent(password);
            servicePrincipal = this.createServicePrincipal(METHOD_LOGIN, no, password);
        }

        HttpSession httpSession = request.getSession();
        httpSession.setAttribute(ServicePrincipal.class.getSimpleName(), servicePrincipal);
        Locale locale = LocaleUtils.parse(request.getParameter(ResponseUtils.LOGIN_LOCALE));
        httpSession.setAttribute(Locale.class.getSimpleName(), locale);

        ResponseUtils.responseResult(response, servicePrincipal);
    }
}
