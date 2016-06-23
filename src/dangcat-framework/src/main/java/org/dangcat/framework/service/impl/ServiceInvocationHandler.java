package org.dangcat.framework.service.impl;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.ServiceProvider;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务执行代理。
 *
 * @author dangcat
 */
public class ServiceInvocationHandler implements InvocationHandler {
    private ServiceProvider parent = null;
    private Object service = null;
    private ServiceInfo serviceInfo = null;

    public ServiceInvocationHandler(ServiceProvider parent, ServiceInfo serviceInfo) {
        this.parent = parent;
        this.serviceInfo = serviceInfo;
    }

    private Object getService() {
        if (this.service == null) {
            Object service = this.serviceInfo.createInstance(this.parent);
            if (service instanceof ServiceBase)
                ((ServiceBase) service).initialize();
            if (service != null)
                ServiceHelper.inject(this.parent, service);
            this.service = service;
        }
        return this.service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object service = this.getService();
        ServiceContext serviceContext = ServiceContext.getInstance();
        ServiceInfo preServiceInfo = null;
        MethodInfo methodInfo = null;

        Object result = null;
        Throwable throwable = null;
        try {
            if (serviceContext != null) {
                preServiceInfo = serviceContext.getServiceInfo();
                serviceContext.addParam(ServiceInfo.class, this.serviceInfo);
                methodInfo = this.serviceInfo.getServiceMethodInfo().getMethodInfo(method.getName());
            }
            if (methodInfo != null) {
                ServiceUtils.injectContext(service, serviceContext);
                this.serviceInfo.beforeInvoke(service, serviceContext, methodInfo, args);
            }
            result = method.invoke(service, args);
        } catch (InvocationTargetException e) {
            throwable = e.getTargetException();
        } catch (Exception e) {
            throwable = e;
        } finally {
            if (methodInfo != null) {
                this.serviceInfo.afterInvoke(service, serviceContext, methodInfo, args, throwable != null ? throwable : result);
                ServiceUtils.injectContext(service, null);
            }
            if (serviceContext != null)
                serviceContext.addParam(ServiceInfo.class, preServiceInfo);
            if (throwable != null)
                throw throwable;
        }
        return result;
    }

    public boolean isValid() {
        return this.getService() != null;
    }
}
