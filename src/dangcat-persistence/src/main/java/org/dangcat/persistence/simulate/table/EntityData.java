package org.dangcat.persistence.simulate.table;

import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.index.IndexManager;
import org.dangcat.persistence.simulate.DatabaseSimulator;
import org.dangcat.persistence.simulate.EntitySimulator;

import java.util.ArrayList;
import java.util.Collection;

public abstract class EntityData<T> extends SimulateData
{
    private Class<T> classType = null;
    private Collection<T> dataCollectoin = null;
    private IndexManager<T> indexManager = null;

    public EntityData(Class<T> classType)
    {
        this.classType = classType;
        this.dataCollectoin = new ArrayList<T>();
        this.indexManager = new IndexManager<T>(this.dataCollectoin);
    }

    @Override
    public void clear()
    {
        this.indexManager.clear();
    }

    @Override
    public void create()
    {
        EntitySimulator entitySimulator = (EntitySimulator) this.getDataSimulator();
        entitySimulator.create(this.getDataCollection(), this.getSize());
    }

    @SuppressWarnings("unchecked")
    public T create(int index)
    {
        EntitySimulator entitySimulator = (EntitySimulator) this.getDataSimulator();
        return (T) entitySimulator.create(index);
    }

    @Override
    public void createDataSimulator(DatabaseSimulator databaseSimulator)
    {
        EntitySimulator entitySimulator = new EntitySimulator(this.getClassType());
        entitySimulator.setDatabaseSimulator(databaseSimulator);
        entitySimulator.initialize();
        this.initEntitySimulator(entitySimulator);
        this.dataSimulator = entitySimulator;
    }

    public Class<T> getClassType()
    {
        return classType;
    }

    public Collection<T> getDataCollection()
    {
        return this.indexManager.getDataCollection();
    }

    public IndexManager<T> getIndexManager()
    {
        return indexManager;
    }

    protected void initEntitySimulator(EntitySimulator entitySimulator)
    {
    }

    @Override
    public void save()
    {
        if (this.getDataCollection().size() > 0)
        {
            EntityManager entityManager = EntityManagerFactory.getInstance().open();
            entityManager.save(this.getDataCollection().toArray());
        }
    }
}
