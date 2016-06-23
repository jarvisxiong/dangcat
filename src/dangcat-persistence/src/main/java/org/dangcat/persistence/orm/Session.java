package org.dangcat.persistence.orm;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;
import org.dangcat.persistence.model.Table;

/**
 * 会话对象。
 * @author dangcat
 * 
 */
public class Session
{
    /** 批量处理的数量。 */
    protected int batchCount = 0;
    /** 连接对象。 */
    private Connection connection = null;
    /** 数据库连接对象。 */
    private DatabaseConnectionPool databaseConnectionPool;
    /** 查询结果 */
    protected ResultSet resultSet = null;
    /** 执行器 */
    private SessionExecutor sessionExecutor = new SessionExecutor(this);
    /** SQL跟踪器。 */
    private SqlProfile sqlProfile = new SqlProfile();
    /** 查询表达式。 */
    protected Statement statement = null;
    /** 使用事务。 */
    private boolean useTransaction = false;

    /**
     * 构造会话对象。
     * @param connection 连接对象。
     */
    public Session(DatabaseConnectionPool connectionPool)
    {
        this.databaseConnectionPool = connectionPool;
    }

    /**
     * 开启事务。
     * @throws SQLException
     */
    public void beginTransaction() throws SQLException
    {
        if (!this.databaseConnectionPool.isUseExtendTransaction())
            this.getConnection().setAutoCommit(false);
        this.useTransaction = true;
    }

    /**
     * 提交事务。
     * @throws SQLException
     */
    public void commit() throws SQLException
    {
        this.useTransaction = false;
        this.sqlProfile.end();
        Connection connection = this.getConnection();
        this.getAutoIncrementManager().update(connection);
        if (!this.databaseConnectionPool.isUseExtendTransaction())
            connection.commit();
    }

    public void destroy()
    {
        this.releaseStatement();
        this.databaseConnectionPool.destroy(this.connection);
        this.connection = null;
    }

    /**
     * 执行命令。
     * @param sql 表达语句。
     * @throws SQLException
     */
    public int execute(String sql) throws SQLException
    {
        if (ValueUtils.isEmpty(sql))
            return 0;
        if (!this.useTransaction)
        {
            this.sqlProfile.begin();
            this.sqlProfile.appendSql(sql);
        }
        SessionExecutor sessionExecutor = new SessionExecutor(this);
        int result = sessionExecutor.execute(SessionExecutor.EXECUTE_SQL, sql);
        if (!this.useTransaction)
            this.sqlProfile.end();
        return result;
    }

    /**
     * 以数据源为参数批量执行SQL命令。
     * @param dataReader 数据来源。
     * @param sqlBatchList 批量语句。
     * @throws SQLException 运行异常。
     */
    public int executeBatch(DataReader dataReader, List<String> sqlBatchList) throws SQLException
    {
        if (!this.useTransaction)
        {
            this.sqlProfile.begin();
            this.sqlProfile.appendSql(sqlBatchList);
        }

        BatchExecutSession batchExecutSession = new BatchExecutSession(this, dataReader, sqlBatchList);
        try
        {
            SessionExecutor sessionExecutor = new SessionExecutor(this);
            batchExecutSession.prepare();
            for (int index = 0; index < dataReader.size(); index++)
            {
                boolean submit = this.batchCount >= this.databaseConnectionPool.getBatchSize();
                for (BatchExecutor batchExecutor : batchExecutSession.getBatchExecutorList())
                {
                    if (this.statement != null)
                    {
                        sessionExecutor.execute(SessionExecutor.EXECUTE_BATCHUPDATE, submit);
                        submit = false;
                    }
                    batchExecutor.prepare(index);
                    this.statement = batchExecutor.getPreparedStatement();
                }
                this.batchCount++;
            }
            if (this.batchCount > 0)
                sessionExecutor.execute(SessionExecutor.EXECUTE_BATCHUPDATE, true);
        }
        finally
        {
            batchExecutSession.release();
            this.statement = null;
            if (!this.useTransaction)
                this.sqlProfile.end();
        }
        return this.batchCount;
    }

    /**
     * 批量执行存储命令。
     * @param rows 数据行对象。
     * @throws SQLException
     */
    public int executeBatch(List<String> sqlBatchList) throws SQLException
    {
        if (!this.useTransaction)
        {
            this.sqlProfile.begin();
            this.sqlProfile.appendSql(sqlBatchList);
        }
        this.sessionExecutor.execute(SessionExecutor.EXECUTE_BATCH, sqlBatchList);
        if (!this.useTransaction)
            this.sqlProfile.end();
        return 1;
    }

    /**
     * 执行更新命令。
     * @param values 参数对象。
     * @throws SQLException
     */
    public int executeBatchUpdate(boolean submit) throws SQLException
    {
        this.batchCount++;
        if (!submit)
            submit = this.batchCount >= this.databaseConnectionPool.getBatchSize();
        return this.sessionExecutor.execute(SessionExecutor.EXECUTE_BATCHUPDATE, submit);
    }

    /**
     * 执行查询命令
     * @param sql
     * @return ResultSet 查询结果
     * @throws SQLException
     */
    public ResultSet executeQuery(String sql) throws SQLException
    {
        if (!this.useTransaction)
        {
            this.sqlProfile.begin();
            this.sqlProfile.appendSql(sql);
        }
        this.sessionExecutor.execute(SessionExecutor.EXECUTE_QUERY, sql);
        if (!this.useTransaction)
            this.sqlProfile.end();
        return this.resultSet;
    }

    /**
     * 执行更新命令。
     * @param values 参数对象。
     * @throws SQLException
     */
    public void executeUpdate() throws SQLException
    {
        this.sessionExecutor.execute(SessionExecutor.EXECUTE_UPDATE);
    }

    private AutoIncrementManager getAutoIncrementManager()
    {
        return this.databaseConnectionPool.getAutoIncrementManager();
    }

    public Connection getConnection()
    {
        if (this.connection == null)
            this.connection = this.databaseConnectionPool.poll();
        return this.connection;
    }

    protected DatabaseConnectionPool getDatabaseConnectionPool()
    {
        return this.databaseConnectionPool;
    }

    /**
     * 返回产生的主键值。。
     * @throws SQLException
     */
    public ResultSet getGeneratedKeys() throws SQLException
    {
        return this.statement.getGeneratedKeys();
    }

    public String getName()
    {
        return this.databaseConnectionPool.getName();
    }

    private ResultSet getResultSet(Table table, ResultSet resultSet) throws SQLException
    {
        ResultSet metaDataResultSet = resultSet;
        if (metaDataResultSet == null)
        {
            String sql = null;
            SqlBuilder sqlBuilder = table.getSql();
            if (sqlBuilder.length() == 0 && !ValueUtils.isEmpty(table.getTableName().getName()))
                sql = "SELECT * FROM " + table.getTableName().getName() + " WHERE 1=2 ";
            else
                sql = sqlBuilder.toString();
            if (!ValueUtils.isEmpty(sql))
                metaDataResultSet = this.executeQuery(sql);
        }
        return metaDataResultSet;
    }

    /**
     * 在指定结果集中还原元数据。
     * @param table 数据表对象。
     * @param resultSet 结果集。
     * @throws SQLException 执行异常。
     */
    public void loadMetaData(Table table, ResultSet resultSet) throws SQLException
    {
        ResultSet metaDataResultSet = this.getResultSet(table, resultSet);
        if (metaDataResultSet == null)
            return;
        // 获取主键信息。
        Collection<String> primayKeys = this.loadPrimayKeyMetaData(table.getTableName().getName());
        // 获取元数据。
        ResultSetMetaData resultSetMetaData = metaDataResultSet.getMetaData();
        // 还原栏位的信息。
        Columns columns = table.getColumns();
        int columnCount = resultSetMetaData.getColumnCount();
        for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++)
        {
            String fieldName = resultSetMetaData.getColumnLabel(columnIndex);
            Class<?> fieldClass = JdbcValueUtils.getFieldType(resultSetMetaData, columnIndex);
            int displaySize = resultSetMetaData.getColumnDisplaySize(columnIndex);
            boolean isPrimaryKey = primayKeys.contains(fieldName);
            Column column = columns.find(fieldName);
            if (column == null)
                column = columns.add(fieldName, fieldClass, displaySize, isPrimaryKey);
            else
            {
                column.setFieldClass(fieldClass);
                column.setDisplaySize(displaySize);
                column.setPrimaryKey(isPrimaryKey);
            }
            column.setNullable(resultSetMetaData.isNullable(columnIndex) != ResultSetMetaData.columnNoNulls);
            column.setScale(resultSetMetaData.getScale(columnIndex));
            column.setAutoIncrement(resultSetMetaData.isAutoIncrement(columnIndex));
            if (column.getSqlType() == 0)
                column.setSqlType(resultSetMetaData.getColumnType(columnIndex));
        }
    }

    /**
     * 载入主键元数据。
     * @param table 数据表对象。
     */
    private Collection<String> loadPrimayKeyMetaData(String tableName)
    {
        Collection<String> primayKeys = new HashSet<String>();
        try
        {
            DatabaseMetaData databaseMetaData = this.getConnection().getMetaData();
            ResultSet resultSet = databaseMetaData.getPrimaryKeys(null, null, tableName);
            while (resultSet.next())
                primayKeys.add(resultSet.getString("COLUMN_NAME"));
            resultSet.close();
        }
        catch (Exception e)
        {
        }
        return primayKeys;
    }

    /**
     * 根据表名字段名读取下一个主键序列。
     * @param tableName 表名。
     * @param fieldName 字段名。
     * @param classType 字段类型。
     * @param tableGenerator 序号策略。
     * @return 主键序列。
     * @throws SQLException
     */
    public Object nextSequence(String tableName, String fieldName, Class<?> classType, TableGenerator tableGenerator) throws SQLException
    {
        return this.getAutoIncrementManager().nextSequence(this.getConnection(), tableName, fieldName, classType, tableGenerator);
    }

    /**
     * 预查询。
     * @param sql 查询语句。
     * @param fieldNames 返回字段名。
     * @throws SQLException
     */
    public void prepare(String sql, String... fieldNames) throws SQLException
    {
        if (!this.useTransaction)
        {
            this.sqlProfile.begin();
            this.sqlProfile.appendSql(sql);
        }
        this.releaseStatement();
        if (fieldNames != null && fieldNames.length > 0)
            this.statement = this.getConnection().prepareStatement(sql, fieldNames);
        else
            this.statement = this.getConnection().prepareStatement(sql);
    }

    /**
     * 释放会话对象。
     */
    public void release()
    {
        this.releaseStatement();
        this.databaseConnectionPool.release(this.connection);
        this.connection = null;
        this.sqlProfile.end();
        this.useTransaction = false;
    }

    /**
     * 释放查询对象对象。
     */
    private void releaseStatement()
    {
        try
        {
            if (this.resultSet != null)
            {
                this.resultSet.close();
                this.resultSet = null;
            }
        }
        catch (SQLException e)
        {
        }

        try
        {
            if (this.statement != null)
            {
                this.statement.close();
                this.statement = null;
            }
        }
        catch (SQLException e)
        {
        }
    }

    /**
     * 回滚事务。
     */
    public void rollback()
    {
        this.sqlProfile.end();
        this.useTransaction = false;
        if (!this.databaseConnectionPool.isUseExtendTransaction())
        {
            try
            {
                this.getConnection().rollback();
            }
            catch (SQLException e)
            {
            }
        }
    }

    /**
     * 设置参数。
     * @param parameterIndex 参数索引位置。
     * @param value 参数值。
     * @param column 数据库类型。
     * @throws SQLException
     */
    public void setParam(int parameterIndex, Object value, Column column) throws SQLException
    {
        JdbcValueUtils.write((PreparedStatement) this.statement, parameterIndex + 1, value, column);
    }
}
