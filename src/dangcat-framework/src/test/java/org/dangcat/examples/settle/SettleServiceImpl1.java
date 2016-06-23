package org.dangcat.examples.settle;

import junit.framework.Assert;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceContext;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Context;

public class SettleServiceImpl1 extends ServiceBase implements SettleService1 {
    @Context
    private ServiceContext serviceContext;
    private int value = 0;
    private int value1 = 0;
    private int value2 = 0;

    public SettleServiceImpl1(ServiceProvider parent) {
        super(parent);
    }

    public ServiceContext getServiceContext() {
        return serviceContext;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue1() {
        return value1;
    }

    public void setValue1(Integer value1) {
        Assert.assertNotNull(this.getServiceContext());
        this.value1 = value1;
    }

    public Integer getValue2() {
        return value2;
    }

    public void setValue2(Integer value2) {
        this.value2 = value2;
    }
}
