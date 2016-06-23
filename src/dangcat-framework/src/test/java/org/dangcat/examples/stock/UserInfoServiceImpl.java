package org.dangcat.examples.stock;

import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.JndiName;

@JndiName(name = "UserInfoService")
public class UserInfoServiceImpl extends ServiceBase implements UserInfoService
{
    UserInfoServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }
}
