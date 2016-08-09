package org.dangcat.persistence.batch;

import org.apache.log4j.Logger;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.entity.EntityManagerSaveImpl;
import org.dangcat.persistence.entity.SaveEntityContext;
import org.dangcat.persistence.event.EntityEventAdapter;

/**
 * 数据批量管理。
 *
 * @author dangcat
 */
public class EntityBatchStorer {
    public static final Logger logger = Logger.getLogger(EntityBatchStorer.class);
    private String databaseName = null;
    private EntityBatchCollection entityBatchCollection = new EntityBatchCollection();
    private EntityEventAdapter entityEventAdapter = null;
    private EntityBatchCollection processEntityBatchCollection = null;

    public EntityBatchStorer(String databaseName) {
        this.databaseName = databaseName;
    }

    public void clear() {
        EntityBatchCollection entityBatchCollection = this.entityBatchCollection;
        synchronized (entityBatchCollection) {
            entityBatchCollection.clear();
        }
    }

    public void delete(Object entity) {
        if (entity != null) {
            EntityBatchCollection entityBatchCollection = this.entityBatchCollection;
            synchronized (entityBatchCollection) {
                entityBatchCollection.delete(entity);
            }
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public EntityEventAdapter getEntityEventAdapter() {
        return entityEventAdapter;
    }

    public void setEntityEventAdapter(EntityEventAdapter entityEventAdapter) {
        this.entityEventAdapter = entityEventAdapter;
    }

    private EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open(this.getDatabaseName());
    }

    public void save() {
        if (this.size() > 0) {
            SaveEntityContext saveEntityContext = new SaveEntityContext();
            saveEntityContext.setEntityManager(this.getEntityManager());
            saveEntityContext.setEntityEventAdapter(this.entityEventAdapter);
            this.processEntityBatchCollection = this.entityBatchCollection;
            synchronized (this.processEntityBatchCollection) {
                this.entityBatchCollection = new EntityBatchCollection();
            }
            this.processEntityBatchCollection.save(saveEntityContext);
            new EntityManagerSaveImpl(this.getEntityManager(), saveEntityContext).execute();
        }
    }

    public void save(Object entity) {
        if (entity != null) {
            EntityBatchCollection entityBatchCollection = this.entityBatchCollection;
            synchronized (entityBatchCollection) {
                entityBatchCollection.save(entity);
            }
        }
    }

    public int size() {
        int size = 0;
        EntityBatchCollection entityBatchCollection = this.entityBatchCollection;
        synchronized (entityBatchCollection) {
            size = entityBatchCollection.size();
        }
        return size;
    }
}
