package org.dangcat.persistence.orm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

class SessionExecutor
{
    protected static final int EXECUTE_BATCH = 1;
    protected static final int EXECUTE_BATCHUPDATE = 5;
    protected static final int EXECUTE_QUERY = 2;
    protected static final int EXECUTE_SQL = 0;
    protected static final int EXECUTE_UPDATE = 3;
    private Session session = null;

    SessionExecutor(Session session)
    {
        this.session = session;
    }

    /**
     * 执行查询命令
     * @param sql
     * @return ResultSet 查询结果
     * @throws SQLException
     */
    @SuppressWarnings("unchecked")
    protected int execute(int type, Object... params) throws SQLException
    {
        int result = 0;
        // 执行状态：0：未执行；1：已执行；-1：因为连接执行失败；
        int status = 0;
        SQLException exception = null;

        while (status != 1)
        {
            try
            {
                switch (type)
                {
                    case EXECUTE_SQL:
                        result = this.innerExecuteSql((String) params[0]);
                        break;
                    case EXECUTE_BATCH:
                        result = this.innerExecuteBatch((List<String>) params[0]);
                        break;
                    case EXECUTE_QUERY:
                        result = this.innerExecuteQuery((String) params[0]);
                        break;
                    case EXECUTE_UPDATE:
                        result = this.innerExecuteUpdate();
                        break;
                    case EXECUTE_BATCHUPDATE:
                        result = this.innerExecuteBatchUpdate((Boolean) params[0]);
                        break;
                }
                status = 1;
            }
            catch (SQLException e)
            {
                if (status == 0 && this.session.getDatabaseConnectionPool().isCommunicationsException(e))
                {
                    this.session.destroy();
                    this.session.getDatabaseConnectionPool().closePooled();
                    status = -1;
                }
                else
                {
                    status = 1;
                    exception = e;
                }
            }
        }

        if (exception != null)
            throw exception;
        return result;
    }

    private int innerExecuteBatch(List<String> sqlBatchList) throws SQLException
    {
        this.session.statement = this.session.getConnection().createStatement();
        int batchCount = 0;
        for (String sql : sqlBatchList)
        {
            if (batchCount >= this.session.getDatabaseConnectionPool().getBatchSize())
            {
                this.session.statement.executeBatch();
                this.session.statement.clearBatch();
                batchCount = 0;
            }
            this.session.statement.addBatch(sql);
            batchCount++;
        }
        if (batchCount > 0)
            this.session.statement.executeBatch();
        return 0;
    }

    private int innerExecuteBatchUpdate(boolean submit) throws SQLException
    {
        int submitCount = 0;
        PreparedStatement preparedStatement = (PreparedStatement) this.session.statement;
        preparedStatement.addBatch();
        if (submit)
        {
            preparedStatement.executeBatch();
            preparedStatement.clearBatch();
            submitCount = this.session.batchCount;
            this.session.batchCount = 0;
        }
        return submitCount;
    }

    private int innerExecuteQuery(String sql) throws SQLException
    {
        this.session.statement = this.session.getConnection().createStatement();
        this.session.resultSet = this.session.statement.executeQuery(sql);
        return 0;
    }

    /**
     * 执行命令。
     * @param sql 表达语句。
     * @throws SQLException
     */
    private int innerExecuteSql(String sql) throws SQLException
    {
        this.session.statement = this.session.getConnection().createStatement();
        return this.session.statement.executeUpdate(sql);
    }

    private int innerExecuteUpdate() throws SQLException
    {
        PreparedStatement preparedStatement = (PreparedStatement) this.session.statement;
        return preparedStatement.executeUpdate();
    }
}
