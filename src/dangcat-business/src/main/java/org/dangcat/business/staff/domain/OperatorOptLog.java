package org.dangcat.business.staff.domain;

import org.dangcat.business.staff.service.impl.OperatorOptLogCalculator;
import org.dangcat.persistence.annotation.Calculator;
import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.OrderBy;

@OrderBy("DateTime DESC")
@Calculator(OperatorOptLogCalculator.class)
public class OperatorOptLog extends OperateLog
{
    public static final String Error = "Error";
    public static final String MethodName = "MethodName";
    public static final String ModuleName = "ModuleName";
    public static final String Result = "Result";
    public static final String ServiceName = "ServiceName";
    private static final long serialVersionUID = 1L;
    @Column(index = 10, displaySize = 200, isCalculate = true)
    private String error = null;

    @Column(index = 7, displaySize = 10, isCalculate = true)
    private String methodName = null;

    @Column(index = 5, displaySize = 15, isCalculate = true)
    private String moduleName = null;

    @Column(index = 8, displaySize = 10, isCalculate = true)
    private String result = null;

    @Column(index = 6, displaySize = 20, isCalculate = true)
    private String serviceName = null;

    public String getError()
    {
        return error;
    }

    public void setError(String error)
    {
        this.error = error;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public void setMethodName(String methodName)
    {
        this.methodName = methodName;
    }

    public String getModuleName()
    {
        return moduleName;
    }

    public void setModuleName(String moduleName)
    {
        this.moduleName = moduleName;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getServiceName()
    {
        return serviceName;
    }

    public void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }
}
