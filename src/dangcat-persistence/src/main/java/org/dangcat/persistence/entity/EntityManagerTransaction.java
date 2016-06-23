package org.dangcat.persistence.entity;

import java.sql.SQLException;

import org.dangcat.framework.pool.SessionException;
import org.dangcat.persistence.orm.Session;

class EntityManagerTransaction
{
    /** 实体管理器。 */
    protected EntityManagerImpl entityManager = null;
    private SaveEntityContext saveEntityContext = null;
    /** 数据库会话。 */
    private Session session = null;

    EntityManagerTransaction(EntityManager entityManager)
    {
        this.entityManager = (EntityManagerImpl) entityManager;
    }

    protected Session beginTransaction() throws SessionException, SQLException
    {
        if (this.entityManager.getSession() != null)
            return this.entityManager.getSession();

        this.session = this.entityManager.openSession();
        this.session.beginTransaction();
        return this.session;
    }

    protected void commit() throws SQLException
    {
        if (this.session != null)
        {
            this.session.commit();
            this.saveEntityContext.afterCommit();
        }
        this.release();
    }

    protected String getDatabaseName()
    {
        return this.entityManager.getDatabaseName();
    }

    protected Session openSession() throws SessionException
    {
        if (this.entityManager.getSession() != null)
            return this.entityManager.getSession();

        this.session = this.entityManager.openSession();
        return this.session;
    }

    protected void prepare(SaveEntityContext saveEntityContext)
    {
        if (this.entityManager.getSession() != null)
            this.entityManager.addSaveEntityContext(saveEntityContext);

        saveEntityContext.setEntityManager(this.entityManager);
        saveEntityContext.initialize();
        this.saveEntityContext = saveEntityContext;
    }

    protected void release()
    {
        if (this.session != null)
            this.session.release();
    }

    protected void rollback()
    {
        if (this.session != null)
            this.session.rollback();
        this.release();
    }
}
