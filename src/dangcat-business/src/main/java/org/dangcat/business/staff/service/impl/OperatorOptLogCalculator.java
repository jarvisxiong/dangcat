package org.dangcat.business.staff.service.impl;

import java.util.Collection;
import java.util.Locale;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.boot.service.impl.ServiceCalculator;
import org.dangcat.business.staff.domain.OperatorOptLog;
import org.dangcat.commons.reflect.MethodInfo;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.impl.ServiceInfo;
import org.dangcat.persistence.calculate.Calculator;
import org.dangcat.persistence.entity.EntityHelper;

public class OperatorOptLogCalculator implements Calculator
{
    private static final String FAILURE = "Failure";
    private static final String SUCCESS = "Success";
    private ResourceReader entityResourceReader = null;

    @Override
    public void calculate(Collection<?> entityCollection)
    {
        for (Object entity : entityCollection)
            calculate(entity);
    }

    @Override
    public void calculate(Object entity)
    {
        OperatorOptLog operatorOptLog = (OperatorOptLog) entity;
        Integer serviceId = ServiceCalculator.getParentId(operatorOptLog.getMethodId());
        if (serviceId != null)
        {
            ServiceInfo serviceInfo = PermissionManager.getInstance().getServiceInfo(serviceId);
            // 模块名
            operatorOptLog.setModuleName(this.getModuleName(serviceInfo.getModuleName()));
            // 服务名
            operatorOptLog.setServiceName(serviceInfo.getTitle(this.getLocale()));
            // 调用方法名
            MethodInfo methodInfo = serviceInfo.getServiceMethodInfo().getMethodInfo(operatorOptLog.getMethodId());
            if (methodInfo != null)
            {
                String methodName = serviceInfo.getMethodTitle(this.getLocale(), methodInfo.getName());
                operatorOptLog.setMethodName(methodName);
            }
            // 调用结果
            if (operatorOptLog.getErrorCode() == 0)
                operatorOptLog.setResult(this.getFieldTitle(SUCCESS));
            else
            {
                operatorOptLog.setResult(this.getFieldTitle(FAILURE));
                // 异常信息。
                String error = serviceInfo.getException(this.getLocale(), operatorOptLog.getErrorCode());
                operatorOptLog.setError(error);
            }
        }
    }

    private String getFieldTitle(String fieldName)
    {
        if (this.entityResourceReader == null)
            this.entityResourceReader = EntityHelper.getEntityMetaData(OperatorOptLog.class).getResourceReader();
        return this.entityResourceReader.getText(this.getLocale(), fieldName);
    }

    private Locale getLocale()
    {
        return ServiceContext.getInstance().getLocale();
    }

    private String getModuleName(String name)
    {
        String title = this.getText("Module." + name + ".title");
        if (ValueUtils.isEmpty(title))
            title = name;
        return title;
    }

    private String getText(String key)
    {
        ResourceReader resourceReader = ApplicationContext.getInstance().getResourceReader();
        return resourceReader.getText(this.getLocale(), key);
    }
}
