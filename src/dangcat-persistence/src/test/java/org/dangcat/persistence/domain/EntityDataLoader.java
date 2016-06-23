package org.dangcat.persistence.domain;

import org.dangcat.persistence.annotation.Cache;
import org.dangcat.persistence.annotation.Database;
import org.dangcat.persistence.cache.EntityCache;
import org.dangcat.persistence.entity.EntityLoader;
import org.dangcat.persistence.entity.EntityManager;

import java.util.List;

public class EntityDataLoader implements EntityLoader<EntityData>
{
    @Cache(AccountInfo.class)
    private EntityCache<AccountInfo> accountInfoCache = null;
    @Database
    private EntityManager defaultEntityManager = null;

    @Override
    public List<EntityData> load(EntityManager entityManager)
    {
        if (this.defaultEntityManager == null)
            throw new RuntimeException("The inject entityManager is null.");
        if (this.accountInfoCache == null)
            throw new RuntimeException("The inject accountInfoCache is null.");
        return entityManager.load(EntityData.class);
    }
}
