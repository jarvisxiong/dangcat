package org.dangcat.persistence.orm;

import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.model.Column;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

class BatchExecutor
{
    private DataReader dataReader = null;
    private List<String> fieldNameList = new ArrayList<String>();
    private PreparedStatement preparedStatement = null;

    BatchExecutor(DataReader dataReader)
    {
        this.dataReader = dataReader;
    }

    protected List<String> getFieldNameList()
    {
        return fieldNameList;
    }

    protected PreparedStatement getPreparedStatement()
    {
        return preparedStatement;
    }

    protected void setPreparedStatement(PreparedStatement preparedStatement) {
        this.preparedStatement = preparedStatement;
    }

    protected void prepare(int index) throws SQLException
    {
        int parameterIndex = 0;
        for (String fieldName : this.fieldNameList)
        {
            Column column = this.dataReader.getColumns().find(fieldName);
            Object value = this.dataReader.getValue(index, fieldName);
            JdbcValueUtils.write(this.preparedStatement, ++parameterIndex, value, column);
        }
    }

    protected void release()
    {
        try
        {
            if (this.preparedStatement != null)
            {
                this.preparedStatement.close();
                this.preparedStatement = null;
            }
        }
        catch (SQLException e)
        {
        }
    }
}
