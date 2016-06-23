package org.dangcat.examples.stock;

import org.dangcat.framework.EntityResourceManager;
import org.dangcat.framework.service.annotation.JndiName;

@JndiName
public interface SettleService {
    EntityResourceManager getEntityManager();

    EntityResourceManager getHsqldbEntityManager();

    EntityResourceManager getMySqlEntityManager();

    EntityResourceManager getOracleEntityManager();

    EntityResourceManager getSqlServerEntityManager();

    UserInfoService getUserInfoService();
}
