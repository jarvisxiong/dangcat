package org.dangcat.persistence.model;

import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.event.TableEventAdapter;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.orm.BatchExecutHelper;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.orm.TableSqlBuilder;

import java.sql.SQLException;
import java.util.List;

/**
 * 表管理器。
 * @author dangcat
 * 
 */
public class TableManagerImpl extends TableManagerBase implements TableManager
{
    /**
     * 在当前的数据源中构建数据表。
     * @param table 表对象。
     * @return 建表结果。
     * @throws TableException 运行异常。
     */
    public int create(Table table) throws TableException
    {
        int result = 0;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin create :");

            // 触发建表前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeCreate(table))
                    return result;
            }

            // 构建表。
            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.getDatabaseName(table));
            sqlBuilder = tableSqlBuilder.buildCreateStatement();

            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            session.beginTransaction();
            result = this.executeBatch(session, table, sqlBuilder.getBatchSqlList());
            session.commit();
            result = 1;

            // 触发事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterCreate(table);

            if (logger.isDebugEnabled())
                logger.debug("End create, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            if (session != null)
                session.rollback();

            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onCreateError(table, e);

            throw new TableException(message, e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }

    /**
     * 删除指定表的数据。
     * @param table 表对象。
     * @return 删除数量。
     * @throws TableException 运行异常。
     */
    public int delete(Table table) throws TableException
    {
        int result = 0;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin delete table: " + table.getTableName().getName());

            // 删除前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeDelete(table))
                    return result;
            }

            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.getDatabaseName(table));
            sqlBuilder = tableSqlBuilder.buildDeleteStatement();

            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            session.beginTransaction();
            result = this.executeBatch(session, table, sqlBuilder.getBatchSqlList());
            session.commit();

            // 删除后事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterDelete(table);

            if (logger.isDebugEnabled())
                logger.debug("End delete table, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            if (session != null)
                session.rollback();

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onDeleteError(table, e);

            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);
            throw new TableException(message, e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }

    /**
     * 删除指定的表。
     * @param tableName 表名。
     * @return 删除结果。
     * @throws TableException 运行异常。
     */
    public int drop(String tableName) throws TableException
    {
        return this.drop(new Table(tableName));
    }

    /**
     * 删除指定的表。
     * @param table 表对象。
     * @return 删除结果。
     * @throws TableException 运行异常。
     */
    public int drop(Table table) throws TableException
    {
        int result = 0;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin drop :");

            // 删表后事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeDrop(table))
                    return result;
            }

            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.getDatabaseName(table));
            sqlBuilder = tableSqlBuilder.buildDropStatement();

            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            result = this.executeBatch(session, table, sqlBuilder.getBatchSqlList());

            // 删表前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterDrop(table);

            if (logger.isDebugEnabled())
                logger.debug("End drop, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onDropError(table, e);

            throw new TableException(message, e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }

    /**
     * 执行表的SQL语句。
     * @param table 表对象。
     * @param name SQL命名。
     * @return 执行结果。
     * @throws TableException 运行异常。
     */
    public int execute(Table table) throws TableException
    {
        int result = 0;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.getDatabaseName(table));
            sqlBuilder = tableSqlBuilder.buildExecuteStatement();
            List<String> sqlBatchList = sqlBuilder.getBatchSqlList();
            if (sqlBatchList.size() == 0)
                return result;

            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin execute :");

            // 执行前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeExecute(table))
                    return result;
            }

            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            session.beginTransaction();
            this.executeBatch(session, table, sqlBatchList);
            session.commit();

            // 执行后事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterExecute(table);

            if (logger.isDebugEnabled())
                logger.debug("End execute, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            if (session != null)
                session.rollback();

            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onExecuteError(table, e);

            throw new TableException(message, e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }

    private int executeBatch(Session session, Table table, List<String> sqlBatchList) throws SQLException
    {
        int result = 0;
        if (BatchExecutHelper.isContainsParams(sqlBatchList))
            result = session.executeBatch(new TableDataReader(table), sqlBatchList);
        else
        {
            if (sqlBatchList.size() > 1)
                result = session.executeBatch(sqlBatchList);
            else
                result = session.execute(sqlBatchList.get(0));
        }
        return result;
    }

    /**
     * 判断表是否存在。
     * @param tableName 表名。
     * @return 是否存在。
     */
    public boolean exists(String tableName)
    {
        return this.exists(new Table(tableName));
    }

    /**
     * 判断表是否存在。
     * @param table 表对象。
     * @return 是否存在。
     */
    public boolean exists(Table table)
    {
        boolean result = true;
        Session session = null;
        try
        {
            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            result = TableUtils.exists(table, session);
        }
        catch (Exception e)
        {
            result = false;
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }

    /**
     * 数据库名称。
     */
    protected String getDatabaseName()
    {
        return null;
    }

    /**
     * 数据库名称。
     */
    private String getDatabaseName(Table table)
    {
        String databaseName = this.getDatabaseName();
        return ValueUtils.isEmpty(databaseName) ? table.getDatabaseName() : databaseName;
    }

    /**
     * 载入指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    public void load(Table table) throws TableException
    {
        String databaseName = this.getDatabaseName(table);
        TableLoadManagerImpl tableLoadManager = new TableLoadManagerImpl(databaseName);
        tableLoadManager.load(table);
    }

    /**
     * 载入元数据内容。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    public void loadMetaData(Table table) throws TableException
    {
        String databaseName = this.getDatabaseName(table);
        TableLoadManagerImpl tableLoadManager = new TableLoadManagerImpl(databaseName);
        tableLoadManager.loadMetaData(table);
    }

    /**
     * 存储指定表的数据。
     * @param table 表对象。
     * @throws TableException 运行异常。
     */
    public void save(Table table) throws TableException
    {
        String databaseName = this.getDatabaseName(table);
        TableSaveManagerImpl tableSaveManager = new TableSaveManagerImpl(databaseName);
        tableSaveManager.save(table);
    }

    /**
     * 清除指定表数据。
     * @param tableName 表名称。
     * @return 清除结果。
     * @throws TableException 运行异常。
     */
    public int truncate(String tableName) throws TableException
    {
        return this.truncate(new Table(tableName));
    }

    /**
     * 清除指定表数据。
     * @param table 表对象。
     * @return 清除结果。
     * @throws TableException 运行异常。
     */
    public int truncate(Table table) throws TableException
    {
        int result = 0;
        SqlBuilder sqlBuilder = null;
        Session session = null;
        try
        {
            long beginTime = DateUtils.currentTimeMillis();
            if (logger.isDebugEnabled())
                logger.debug("Begin truncate table: " + table.getTableName().getName());

            // 清除前事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
            {
                if (!tableEventAdapter.beforeTruncate(table))
                    return result;
            }

            TableSqlBuilder tableSqlBuilder = new TableSqlBuilder(table, this.getDatabaseName(table));
            sqlBuilder = tableSqlBuilder.buildTruncateStatement();

            // 获取会话对象。
            session = this.openSession(this.getDatabaseName(table));
            result = session.executeBatch(sqlBuilder.getBatchSqlList());

            // 清除后事件。
            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.afterTruncate(table);

            if (logger.isDebugEnabled())
                logger.debug("End truncate table, cost " + (DateUtils.currentTimeMillis() - beginTime) + " (ms)");
        }
        catch (Exception e)
        {
            String message = sqlBuilder == null ? e.toString() : sqlBuilder.toString();
            if (logger.isDebugEnabled())
                logger.error(message, e);

            for (TableEventAdapter tableEventAdapter : table.getTableEventAdapterList())
                tableEventAdapter.onTruncateError(table, e);

            throw new TableException(message, e);
        }
        finally
        {
            if (session != null)
                session.release();
        }
        return result;
    }
}
