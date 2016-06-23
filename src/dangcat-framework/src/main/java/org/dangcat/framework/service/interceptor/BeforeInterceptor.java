package org.dangcat.framework.service.interceptor;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;

/**
 * 执行前拦截器。
 * @author dangcat
 * 
 */
public interface BeforeInterceptor
{
    /**
     * 服务执行方法前拦截器。
     * @param service 服务对象。
     * @param serviceContext 上下文。
     * @param methodInfo 执行方法。
     * @param args 执行参数。
     * @throws ServiceException 执行异常。
     */
    void beforeInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args) throws ServiceException;
}
