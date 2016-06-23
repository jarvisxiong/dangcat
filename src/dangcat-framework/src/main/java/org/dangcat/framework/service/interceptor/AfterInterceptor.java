package org.dangcat.framework.service.interceptor;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.service.ServiceContext;

/**
 * 执行后拦截器。
 * @author dangcat
 * 
 */
public interface AfterInterceptor
{
    /**
     * 服务执行方法前拦截器。
     * @param service 服务对象。
     * @param serviceContext 上下文。
     * @param methodInfo 执行方法。
     * @param args 执行参数。
     * @param result 执行结果。
     */
    public void afterInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args, Object result);
}
