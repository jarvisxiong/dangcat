package org.dangcat.persistence.entity;

import org.dangcat.persistence.event.EntityEventAdapter;
import org.dangcat.persistence.tablename.TableName;

import java.util.HashMap;
import java.util.Map;

/**
 * 实体上下文。
 * @author dangcat
 * 
 */
public class EntityContext
{
    private EntityEventAdapter entityEventAdapter = null;
    private EntityManagerImpl entityManager;
    private Map<String, Object> params = new HashMap<String, Object>();
    private String sqlName = null;
    private TableName tableName = null;

    public EntityEventAdapter getEntityEventAdapter()
    {
        return this.entityEventAdapter;
    }

    public void setEntityEventAdapter(EntityEventAdapter entityEventAdapter) {
        this.entityEventAdapter = entityEventAdapter;
    }

    /**
     * 实体管理器。
     * @return
     */
    public EntityManager getEntityManager()
    {
        return this.entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = (EntityManagerImpl) entityManager;
    }

    public Map<String, Object> getParams()
    {
        return this.params;
    }

    public String getSqlName()
    {
        return this.sqlName;
    }

    public void setSqlName(String sqlName)
    {
        this.sqlName = sqlName;
    }

    public TableName getTableName()
    {
        return this.tableName;
    }

    public void setTableName(TableName tableName)
    {
        this.tableName = tableName;
    }

    public boolean isCustom()
    {
        return this.params.size() > 0 || this.tableName != null;
    }

}