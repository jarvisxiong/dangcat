package org.dangcat.persistence.orm;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.pool.ConnectionFactory;
import org.dangcat.framework.pool.SessionException;

import javax.sql.DataSource;
import java.util.Map;

/**
 * 会话工厂。
 * @author dangcat
 * 
 */
public class SessionFactory extends ConnectionFactory<DatabaseConnectionPool, Session>
{
    public static final String RESOURCETYPE = "database";
    private static SessionFactory instance = null;

    /**
     * 私有构造函数。
     */
    private SessionFactory() {
    }

    /**
     * 采用单子模式获取会话工厂实例。
     */
    public static SessionFactory getInstance()
    {
        if (instance == null)
        {
            synchronized (SessionFactory.class)
            {
                try
                {
                    SessionFactory instance = new SessionFactory();
                    instance.initialize();
                    SessionFactory.instance = instance;
                }
                catch (SessionException e)
                {
                    logger.error("Create database connection error!", e);
                }
            }
        }
        return instance;
    }

    /**
     * 添加默认的数据源。
     * @param dataSource 数据源对象。
     */
    public void addDataSource(DataSource dataSource)
    {
        this.addDataSource(DEFAULT, dataSource, null);
    }

    /**
     * 添加新的数据源。
     * @param databaseName 数据库别名。
     * @param dataSource 数据源对象。
     */
    public void addDataSource(String databaseName, DataSource dataSource)
    {
        this.addDataSource(databaseName, dataSource, null);
    }

    /**
     * 添加新的数据源。
     * @param databaseName 数据库别名。
     * @param dataSource 数据源对象。
     * @param databaseConfig 数据库设置。
     */
    public void addDataSource(String databaseName, DataSource dataSource, Map<String, String> databaseParams)
    {
        if (dataSource != null && !ValueUtils.isEmpty(databaseName))
        {
            if (databaseName != null)
                databaseName = databaseName.toLowerCase();
            DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool(databaseName, dataSource, databaseParams);
            try
            {
                databaseConnectionPool.initialize();
            }
            catch (SessionException e)
            {
                logger.error(this, e);
            }
            this.put(databaseName, databaseConnectionPool);
        }
    }

    @Override
    protected void close(DatabaseConnectionPool connectionPool)
    {
        connectionPool.close();
    }

    @Override
    protected DatabaseConnectionPool createConnectionPool(String name, Map<String, String> params) throws SessionException
    {
        DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool(name, params);
        databaseConnectionPool.initialize();
        return databaseConnectionPool;
    }

    @Override
    protected Session createSession(DatabaseConnectionPool connectionPool)
    {
        return new Session(connectionPool);
    }

    /**
     * 得到指定数据源的驱动类型。
     * @param databaseName 数据库名称。
     * @return 驱动类型。
     */
    public DatabaseType getDatabaseType(String databaseName)
    {
        return this.get(databaseName).getDatabaseType();
    }

    public String getDefaultDatabase()
    {
        return this.get(DEFAULT).getName();
    }

    @Override
    public String getResourceType()
    {
        return RESOURCETYPE;
    }

    /**
     * 读取语法辅助工具。
     * @param databaseName 数据库。
     * @return
     */
    public SqlSyntaxHelper getSqlSyntaxHelper(String databaseName)
    {
        return this.get(databaseName).getSqlSyntaxHelper();
    }
}
