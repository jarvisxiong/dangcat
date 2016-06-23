package org.dangcat.web.servlet;

import org.dangcat.boot.menus.Menus;
import org.dangcat.boot.menus.MenusManager;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.serialize.json.MethodInfoSerializer;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.web.invoke.InvokeProcess;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Collection;

/**
 * RESTFull Web Service 执行入口。
 *
 * @author dangcat
 */
public class RESTServiceServlet extends ServiceServletBase {
    private static final long serialVersionUID = 1L;

    private void createInvokeProcess(MethodInfo methodInfo) {
        ServiceContext serviceContext = ServiceContext.getInstance();
        serviceContext.addParam(MethodInfo.class, methodInfo);
        org.dangcat.web.annotation.InvokeProcess invokeProcessAnnotation = methodInfo.getMethod().getAnnotation(org.dangcat.web.annotation.InvokeProcess.class);
        if (invokeProcessAnnotation != null) {
            InvokeProcess invokeProcess = (InvokeProcess) ReflectUtils.newInstance(invokeProcessAnnotation.value());
            if (invokeProcess != null) {
                ServiceInfo serviceInfo = serviceContext.getServiceInfo();
                invokeProcess.setServiceInfo(serviceInfo);
                serviceContext.addParam(InvokeProcess.class, invokeProcess);
                String invokeProcessName = serviceContext.getServiceInfo().getJndiName() + "/" + methodInfo.getName() + "/" + InvokeProcess.class.getSimpleName();
                serviceContext.getParam(HttpSession.class).setAttribute(invokeProcessName, invokeProcess);
            }
        }
    }

    private void executeForm(HttpServletRequest request, HttpServletResponse response, ServiceCaller serviceCaller, ServiceInfo serviceInfo) throws Exception {
        Menus menus = MenusManager.getInstance().getMenus();
        StringBuilder path = new StringBuilder();
        path.append("/");
        path.append(menus.getDir());
        path.append("/");
        path.append(serviceCaller.getJndiName());
        path.append("/");
        path.append(serviceCaller.getMethod());
        request.getRequestDispatcher(path.toString()).forward(request, response);
    }

    private void executeMethod(HttpServletRequest request, HttpServletResponse response, ServiceCaller serviceCaller, ServiceInfo serviceInfo) throws Exception {
        // 找到执行的方法。
        MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(serviceCaller.getMethod());
        if (methodInfo == null)
            throw new Exception("The request " + serviceCaller + " can't find destination.");

        this.createInvokeProcess(methodInfo);

        Object result = this.invoke(serviceCaller, serviceInfo, methodInfo);
        // 反馈执行结果。
        if (result != null)
            ResponseUtils.responseResult(response, result);
    }

    /**
     * 查询服务接口。
     *
     * @param request       请求对象。
     * @param response      响应对象。
     * @param serviceCaller 服务调用对象。
     * @throws Exception 执行异常。
     */
    private void executeQuery(HttpServletRequest request, HttpServletResponse response, ServiceCaller serviceCaller, ServiceInfo serviceInfo) throws Exception {
        Collection<MethodInfo> methodInfoCollection = serviceInfo.getServiceMethodInfo().getMethodInfos();

        if (methodInfoCollection == null)
            throw new Exception("The request " + request.getRequestURI() + " and method " + request.getMethod() + " is error.");

        if (methodInfoCollection.size() == 0)
            throw new Exception("The request " + request.getRequestURI() + " and method " + request.getMethod() + " not found public method.");

        response.setContentType(ContentType.getType(ContentType.json, ResponseUtils.DEFAULT_CHARSET));
        response.setStatus(HttpServletResponse.SC_OK);
        MethodInfoSerializer.serialize(methodInfoCollection, response.getWriter());
    }

    private boolean executeQueryJndiNames(HttpServletRequest request, HttpServletResponse response, ServiceCaller serviceCaller) throws Exception {
        boolean result = false;
        if (ValueUtils.isEmpty(serviceCaller.getJndiName()) && RequestParser.isQueryRequest(request)) {
            Collection<String> jndiNames = ServiceFactory.getInstance().getJndiNames(false);
            ResponseUtils.responseResult(response, jndiNames);
            result = true;
        }
        return result;
    }

    @Override
    protected void executeService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServiceCaller serviceCaller = RequestParser.createServiceCaller(request);
        if (this.logger.isDebugEnabled())
            this.logger.debug(serviceCaller);

        if (this.executeQueryJndiNames(request, response, serviceCaller))
            return;

        if (ValueUtils.isEmpty(serviceCaller.getJndiName()) || ValueUtils.isEmpty(serviceCaller.getMethod()))
            super.executeService(request, response);

        // 定位目标服务。
        ServiceInfo serviceInfo = this.locateServiceInfo(request, serviceCaller);
        if (RequestParser.isQueryRequest(request)) // 执行查询服务接口。
            this.executeQuery(request, response, serviceCaller, serviceInfo);
        else if (RequestParser.isFormRequest(request)) // 执行载入页面操作。
            this.executeForm(request, response, serviceCaller, serviceInfo);
        else
            this.executeMethod(request, response, serviceCaller, serviceInfo); // 调用服务方法。
    }

    protected Object invoke(ServiceCaller serviceCaller, ServiceInfo serviceInfo, MethodInfo methodInfo) throws Exception {
        return serviceCaller.invoke(serviceInfo.getInstance(), methodInfo);
    }

    private ServiceInfo locateServiceInfo(HttpServletRequest request, ServiceCaller serviceCaller) throws Exception {
        // 定位目标服务。
        ServiceInfo serviceInfo = this.locateServiceInfo(serviceCaller);
        if (serviceInfo == null || serviceInfo.getInstance() == null)
            throw new Exception("The request " + request.getRequestURI() + " and method " + request.getMethod() + " can't find serviceInfo.");
        ServiceContext.getInstance().addParam(ServiceInfo.class, serviceInfo);
        return serviceInfo;
    }

    protected ServiceInfo locateServiceInfo(ServiceCaller serviceCaller) {
        return ServiceFactory.getServiceLocator().getServiceInfo(serviceCaller.getJndiName());
    }
}
