package org.dangcat.examples.settle;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.interceptor.BeforeInterceptor;

public class Interceptor4 implements BeforeInterceptor
{
    @Override
    public void beforeInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args) throws ServiceException
    {
        if (methodInfo.getName() == "setValue3")
            args[0] = (Integer) args[0] * 300;
    }
}
