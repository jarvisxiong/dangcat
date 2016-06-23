package org.dangcat.persistence.entity;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SessionFactory;

import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * 实体管理器。
 * @author dangcat
 * 
 */
public class EntityManagerImpl implements EntityManager
{
    protected static final Logger logger = Logger.getLogger(EntityManager.class);
    /** 数据库名。 */
    private String databaseName = null;
    private ThreadLocal<Collection<SaveEntityContext>> saveEntityContextesThreadLocal = new ThreadLocal<Collection<SaveEntityContext>>();
    private ThreadLocal<Session> sessionThreadLocal = new ThreadLocal<Session>();

    public EntityManagerImpl()
    {
    }

    public EntityManagerImpl(String databaseName)
    {
        this.databaseName = databaseName;
    }

    protected void addSaveEntityContext(SaveEntityContext saveEntityContext)
    {
        Collection<SaveEntityContext> saveEntityContextes = this.saveEntityContextesThreadLocal.get();
        if (saveEntityContextes == null)
        {
            saveEntityContextes = new LinkedList<SaveEntityContext>();
            this.saveEntityContextesThreadLocal.set(saveEntityContextes);
        }
        saveEntityContextes.add(saveEntityContext);
    }

    @Override
    public Session beginTransaction()
    {
        Session session = this.sessionThreadLocal.get();
        if (session == null)
        {
            try
            {
                session = this.openSession();
                session.beginTransaction();
                this.sessionThreadLocal.set(session);
            }
            catch (Exception e)
            {
                logger.error(this, e);
            }
        }
        return session;
    }

    @Override
    public void commit()
    {
        try
        {
            Session session = this.sessionThreadLocal.get();
            if (session != null)
            {
                session.commit();
                Collection<SaveEntityContext> saveEntityContextes = this.saveEntityContextesThreadLocal.get();
                if (saveEntityContextes != null)
                {
                    for (SaveEntityContext saveEntityContext : saveEntityContextes)
                        saveEntityContext.afterCommit();
                }
            }
        }
        catch (SQLException e)
        {
            logger.error(this, e);
        }
        finally
        {
            this.release();
        }
    }

    /**
     * 删除指定过滤条件的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @return 是否删除。
     * @throws EntityException
     */
    @Override
    public <T> int delete(Class<T> entityClass, FilterExpress filterExpress) throws EntityException
    {
        return this.getEntityManagerDeleteImpl().delete(new DeleteEntityContext(entityClass, filterExpress));
    }

    /**
     * 删除指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeyValues 主键值。
     * @return 是否删除。
     * @throws EntityException
     */
    @Override
    public <T> int delete(Class<T> entityClass, Object... primaryKeyValues) throws EntityException
    {
        return this.getEntityManagerDeleteImpl().delete(entityClass, primaryKeyValues);
    }

    /**
     * 删除指定属性的的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名列表。
     * @param values 属性值。
     * @return 是否删除。
     * @throws EntityException
     */
    @Override
    public <T> int delete(Class<T> entityClass, String[] fieldNames, Object... values) throws EntityException
    {
        return this.getEntityManagerDeleteImpl().delete(entityClass, fieldNames, values);
    }

    /**
     * 按照上下文删除指定的实体对象。
     * @param <T> 实体类型。
     * @param deleteEntityContext 删除实体上下文。
     * @return 是否删除。
     * @throws TableException
     */
    @Override
    public int delete(DeleteEntityContext deleteEntityContext) throws EntityException
    {
        return this.getEntityManagerDeleteImpl().delete(deleteEntityContext);
    }

    /**
     * 删除指定的实体对象。
     * @param <T> 实体类型。
     * @param entites 删除实体对象。
     * @return 是否删除。
     * @throws TableException
     */
    @Override
    public int delete(Object... entites) throws EntityException
    {
        return this.getEntityManagerDeleteImpl().delete(entites);
    }

    /**
     * 数据库名称。
     */
    protected String getDatabaseName()
    {
        return this.databaseName;
    }

    private EntityManagerDeleteImpl getEntityManagerDeleteImpl()
    {
        return new EntityManagerDeleteImpl(this);
    }

    protected EntityManagerSaveImpl getEntityManagerSaveImpl()
    {
        return new EntityManagerSaveImpl(this);
    }

    protected Session getSession()
    {
        return this.sessionThreadLocal.get();
    }

    /**
     * 载入指定类型的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @return 查询结果。
     */
    @Override
    public <T> List<T> load(Class<T> entityClass) throws EntityException
    {
        return this.load(new LoadEntityContext(entityClass));
    }

    /**
     * 按照过滤条件载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     */
    @Override
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress) throws EntityException
    {
        return this.load(new LoadEntityContext(entityClass, filterExpress));
    }

    /**
     * 按照过滤条件、范围载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress, Range range) throws EntityException
    {
        return this.load(new LoadEntityContext(entityClass, filterExpress, range));
    }

    /**
     * 按照过滤条件、范围、排序条件载入指定类型的实体。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     * @param orderBy 排序条件。
     * @return 查询结果。
     */
    public <T> List<T> load(Class<T> entityClass, FilterExpress filterExpress, Range range, OrderBy orderBy) throws EntityException
    {
        return this.load(new LoadEntityContext(entityClass, filterExpress, range, orderBy));
    }

    /**
     * 找到指定主键的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param primaryKeys 主键参数。
     * @return 找到的实体对象。
     */
    @Override
    public <T> T load(Class<T> entityClass, Object... primaryKeyValues) throws EntityException
    {
        EntityManagerLoadImpl<T> entityManagerLoad = new EntityManagerLoadImpl<T>(this);
        return entityManagerLoad.load(entityClass, primaryKeyValues);
    }

    /**
     * 找到指定属性的实体对象。
     * @param <T> 实体类型。
     * @param entityClass 实体类型。
     * @param fieldNames 字段名列表。
     * @param values 属性值。
     * @return 找到的实体对象。
     */
    @Override
    public <T> List<T> load(Class<T> entityClass, String[] fieldNames, Object... values) throws EntityException
    {
        EntityManagerLoadImpl<T> entityManagerLoad = new EntityManagerLoadImpl<T>(this);
        return entityManagerLoad.load(entityClass, fieldNames, values);
    }

    /**
     * 载入指定类型的实体对象。
     * @param <T> 实体类型。
     * @param loadEntityContext 载入实体上下文。
     * @return 查询结果。
     */
    @Override
    public <T> List<T> load(LoadEntityContext loadEntityContext) throws EntityException
    {
        EntityManagerLoadImpl<T> entityManagerLoad = new EntityManagerLoadImpl<T>(this);
        return entityManagerLoad.load(loadEntityContext);
    }

    /**
     * 开启会话。
     * @return 会话对象。
     * @throws SessionException 数据库异常。
     */
    protected Session openSession() throws SessionException
    {
        return SessionFactory.getInstance().openSession(this.getDatabaseName());
    }

    /**
     * 由数据库刷新实体实例内容。
     * @param <T>
     * @param entity 实体对象。
     * @throws EntityException 操作异常。
     */
    public <T> T refresh(T entity) throws EntityException
    {
        EntityManagerLoadImpl<T> entityManagerLoad = new EntityManagerLoadImpl<T>(this);
        return entityManagerLoad.load(entity);
    }

    private void release()
    {
        Session session = this.sessionThreadLocal.get();
        if (session != null)
            session.release();
        this.sessionThreadLocal.remove();
        this.saveEntityContextesThreadLocal.remove();
    }

    @Override
    public void rollback()
    {
        try
        {
            Session session = this.sessionThreadLocal.get();
            if (session != null)
                session.rollback();
        }
        finally
        {
            this.release();
        }
    }

    /**
     * 保存指定实体对象。
     * @param <T> 实体类型。
     * @param entities 保存实体对象。
     * @return 保存结果。
     */
    @Override
    public void save(Object... entities) throws EntityException
    {
        this.save(new SaveEntityContext(), entities);
    }

    /**
     * 保存指定实体对象。
     * @param <T> 实体类型。
     * @param saveEntityContext 保存实体上下文。
     * @param entities 保存实体对象。
     * @return 保存结果。
     */
    @Override
    public void save(SaveEntityContext saveEntityContext, Object... entities) throws EntityException
    {
        if (entities != null && entities.length > 0 && saveEntityContext != null)
        {
            saveEntityContext.setEntityManager(this);
            for (Object entity : entities)
                saveEntityContext.save(entity);
            if (saveEntityContext.size() > 0)
            {
                EntityManagerSaveImpl entityManagerSave = this.getEntityManagerSaveImpl();
                entityManagerSave.setSaveEntityContext(saveEntityContext);
                entityManagerSave.execute();
            }
        }
    }
}
