package org.dangcat.examples.settle;

import junit.framework.Assert;

import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Interceptors;
import org.dangcat.framework.service.annotation.Service;

@Interceptors( { Interceptor4.class })
public class SettleServiceImpl2 extends ServiceBase implements SettleService2
{
    @Service
    private SettleService1 settleService1;
    private Integer value = 0;
    private Integer value1 = 0;
    private Integer value2 = 0;
    private Integer value3 = 0;

    public SettleServiceImpl2(ServiceProvider parent)
    {
        super(parent);
    }

    public SettleService1 getSettleService1()
    {
        return settleService1;
    }

    public Integer getValue()
    {
        return value;
    }

    public Integer getValue1()
    {
        return value1;
    }

    public Integer getValue2()
    {
        return value2;
    }

    public Integer getValue3()
    {
        return value3;
    }

    public void setValue(Integer value)
    {
        this.value = value;
    }

    public void setValue1(Integer value1)
    {
        this.value1 = value1;
    }

    public void setValue2(Integer value2)
    {
        Assert.assertNotNull(this.getSettleService1());
        this.value2 = value2;
    }

    public void setValue3(Integer value3)
    {
        this.value3 = value3;
    }
}
