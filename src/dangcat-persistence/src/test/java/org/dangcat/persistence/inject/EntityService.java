package org.dangcat.persistence.inject;

import org.dangcat.persistence.annotation.Cache;
import org.dangcat.persistence.annotation.Database;
import org.dangcat.persistence.cache.EntityCache;
import org.dangcat.persistence.cache.MemCache;
import org.dangcat.persistence.domain.AccountInfo;
import org.dangcat.persistence.domain.AccountInfoAlias;
import org.dangcat.persistence.domain.EntityData;
import org.dangcat.persistence.entity.EntityManager;

public class EntityService {
    @Cache(AccountInfoAlias.class)
    private MemCache<AccountInfoAlias> accountInfoAliasCache = null;

    @Cache(AccountInfo.class)
    private EntityCache<AccountInfo> accountInfoCache = null;

    @Database
    private EntityManager defaultEntityManager = null;

    @Cache(EntityData.class)
    private EntityCache<EntityData> entityDataCache = null;

    @Database("Hsqldb")
    private EntityManager hsqldbEntityManager = null;

    @Database("MySql")
    private EntityManager mySqlEntityManager = null;

    @Database("Oracle")
    private EntityManager oracleEntityManager = null;

    @Database("SqlServer")
    private EntityManager sqlServerEntityManager = null;

    public MemCache<AccountInfoAlias> getAccountInfoAliasCache() {
        return this.accountInfoAliasCache;
    }

    public EntityCache<AccountInfo> getAccountInfoCache() {
        return this.accountInfoCache;
    }

    protected EntityManager getDefaultEntityManager() {
        return this.defaultEntityManager;
    }

    public EntityCache<EntityData> getEntityDataCache() {
        return this.entityDataCache;
    }

    public EntityManager getHsqldbEntityManager() {
        return this.hsqldbEntityManager;
    }

    public EntityManager getMySqlEntityManager() {
        return this.mySqlEntityManager;
    }

    public EntityManager getOracleEntityManager() {
        return this.oracleEntityManager;
    }

    public EntityManager getSqlServerEntityManager() {
        return this.sqlServerEntityManager;
    }
}
