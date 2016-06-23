package org.dangcat.examples.settle;

import org.dangcat.framework.service.ServiceContext;

public interface SettleService1
{
    public ServiceContext getServiceContext();

    public Integer getValue();

    public Integer getValue1();

    public Integer getValue2();

    public void setValue(Integer value);

    public void setValue1(Integer value);

    public void setValue2(Integer value);
}
