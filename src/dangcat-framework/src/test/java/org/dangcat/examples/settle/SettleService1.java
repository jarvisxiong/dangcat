package org.dangcat.examples.settle;

import org.dangcat.framework.service.ServiceContext;

public interface SettleService1 {
    ServiceContext getServiceContext();

    Integer getValue();

    void setValue(Integer value);

    Integer getValue1();

    void setValue1(Integer value);

    Integer getValue2();

    void setValue2(Integer value);
}
