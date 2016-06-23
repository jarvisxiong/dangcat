package org.dangcat.persistence.entity;

import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.persistence.inject.DatabaseInjectProvider;

/**
 * 会话工厂。
 *
 * @author dangcat
 */
public class EntityManagerFactory {
    private static EntityManagerFactory instance = new EntityManagerFactory();

    static {
        ServiceHelper.addInjectProvider(new DatabaseInjectProvider());
    }

    public static EntityManagerFactory getInstance() {
        return instance;
    }

    public EntityManager open() {
        return this.open(null);
    }

    public EntityManager open(String databaseName) {
        return new EntityManagerImpl(databaseName);
    }
}
