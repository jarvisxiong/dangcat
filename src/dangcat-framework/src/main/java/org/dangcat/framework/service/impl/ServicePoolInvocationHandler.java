package org.dangcat.framework.service.impl;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 服务对象池代理。
 * @author dangcat
 * 
 */
public class ServicePoolInvocationHandler implements InvocationHandler
{
    private ServicePool servicePool = null;

    public ServicePoolInvocationHandler(ServicePool servicePool)
    {
        this.servicePool = servicePool;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        Object result = null;
        ServiceLiver serviceLiver = this.servicePool.poll();
        if (serviceLiver != null)
        {
            Object instance = serviceLiver.getService();
            ServiceContext serviceContext = ServiceContext.getInstance();
            ServiceInfo preServiceInfo = null;
            ServiceInfo serviceInfo = null;
            MethodInfo methodInfo = null;

            Throwable throwable = null;
            try
            {
                if (serviceContext != null)
                {
                    preServiceInfo = serviceContext.getServiceInfo();
                    serviceInfo = this.servicePool.getServiceInfo();
                    methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(method.getName());
                }
                if (methodInfo != null)
                {
                    ServiceUtils.injectContext(instance, serviceContext);
                    serviceInfo.beforeInvoke(instance, serviceContext, methodInfo, args);
                }
                result = method.invoke(instance, args);
            }
            catch (InvocationTargetException e)
            {
                throwable = e.getTargetException();
            }
            catch (Exception e)
            {
                throwable = e;
            }
            finally
            {
                if (methodInfo != null)
                {
                    serviceInfo.afterInvoke(instance, serviceContext, methodInfo, args, throwable != null ? throwable : result);
                    ServiceUtils.injectContext(instance, null);
                }
                this.servicePool.release(serviceLiver);
                if (serviceContext != null)
                    serviceContext.addParam(ServiceInfo.class, preServiceInfo);
                if (throwable != null)
                    throw throwable;
            }
        }
        return result;
    }

    public boolean isValid()
    {
        boolean result = false;
        ServiceLiver serviceLiver = this.servicePool.poll();
        if (serviceLiver != null)
        {
            try
            {
                result = serviceLiver.getService() != null;
            }
            finally
            {
                this.servicePool.release(serviceLiver);
            }
        }
        return result;
    }
}
