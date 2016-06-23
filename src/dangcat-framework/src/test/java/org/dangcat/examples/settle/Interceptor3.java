package org.dangcat.examples.settle;

import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.framework.exception.ServiceException;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.interceptor.BeforeInterceptor;

public class Interceptor3 implements BeforeInterceptor {
    @Override
    public void beforeInvoke(Object service, ServiceContext serviceContext, MethodInfo methodInfo, Object[] args) throws ServiceException {
        if (methodInfo.getName() == "setValue1" || methodInfo.getName() == "setValue2")
            args[0] = (Integer) args[0] * 30;
    }
}
