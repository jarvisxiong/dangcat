package org.dangcat.persistence.orm;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dangcat.persistence.DataReader;

class BatchExecutSession
{
    private List<BatchExecutor> batchExecutorList = new ArrayList<BatchExecutor>();
    private DataReader dataReader = null;
    private Session session = null;
    private List<String> sqlBatchList = null;

    BatchExecutSession(Session session, DataReader dataReader, List<String> sqlBatchList)
    {
        this.session = session;
        this.dataReader = dataReader;
        this.sqlBatchList = sqlBatchList;
    }

    protected List<BatchExecutor> getBatchExecutorList()
    {
        return batchExecutorList;
    }

    protected void prepare() throws SQLException
    {
        for (String sql : this.sqlBatchList)
        {
            BatchExecutor batchExecutor = new BatchExecutor(dataReader);
            String sqlExpress = BatchExecutHelper.analyze(sql, batchExecutor.getFieldNameList());
            PreparedStatement preparedStatement = this.session.getConnection().prepareStatement(sqlExpress);
            batchExecutor.setPreparedStatement(preparedStatement);
            this.batchExecutorList.add(batchExecutor);
        }
    }

    protected void release()
    {
        for (BatchExecutor batchExecutor : this.getBatchExecutorList())
            batchExecutor.release();
    }
}
