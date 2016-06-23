package org.dangcat.examples.stock;

import org.dangcat.framework.EntityResourceManager;
import org.dangcat.framework.service.annotation.JndiName;

@JndiName
public interface SettleService
{
    public EntityResourceManager getEntityManager();

    public EntityResourceManager getHsqldbEntityManager();

    public EntityResourceManager getMySqlEntityManager();

    public EntityResourceManager getOracleEntityManager();

    public EntityResourceManager getSqlServerEntityManager();

    public UserInfoService getUserInfoService();
}
