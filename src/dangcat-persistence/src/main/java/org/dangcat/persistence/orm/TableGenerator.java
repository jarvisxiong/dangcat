package org.dangcat.persistence.orm;

import java.sql.*;

/**
 * 自增序号记录表。
 * @author dangcat
 * 
 */
public class TableGenerator
{
    private String idFieldName;
    private String tableName;
    private String valueFieldName;

    public TableGenerator(String tableName, String idFieldName, String valueFieldName)
    {
        this.tableName = tableName;
        this.idFieldName = idFieldName;
        this.valueFieldName = valueFieldName;
    }

    private String getInsertSQL(String tableName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        sql.append(this.tableName);
        sql.append("(");
        sql.append(this.idFieldName);
        sql.append(", ");
        sql.append(this.valueFieldName);
        sql.append(") VALUES('");
        sql.append(tableName);
        sql.append("', ?)");
        return sql.toString();
    }

    private String getQuerySQL(String tableName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        sql.append(this.valueFieldName);
        sql.append(" FROM ");
        sql.append(this.tableName);
        sql.append(" WHERE ");
        sql.append(this.idFieldName);
        sql.append(" = '");
        sql.append(tableName);
        sql.append("'");
        return sql.toString();
    }

    private String getUpdateSQL(String tableName)
    {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        sql.append(this.tableName);
        sql.append(" SET ");
        sql.append(this.valueFieldName);
        sql.append(" = ? WHERE ");
        sql.append(this.idFieldName);
        sql.append(" = '");
        sql.append(tableName);
        sql.append("'");
        return sql.toString();
    }

    /**
     * 查询策略表当前值。
     * @param connection 数据库连接。
     * @throws SQLException 执行异常。
     */
    public Long query(Connection connection, String tableName) throws SQLException
    {
        Long value = null;
        Statement statement = null;
        try
        {
            statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(this.getQuerySQL(tableName));
            if (resultSet.next() && resultSet.getObject(this.valueFieldName) != null)
                value = resultSet.getLong(this.valueFieldName);
            resultSet.close();
        }
        finally
        {
            if (statement != null)
                statement.close();
        }
        return value;
    }

    /**
     * 更新序号策略表的值。
     * @param connection 数据库连接。
     * @throws SQLException 执行异常。
     */
    public void update(Connection connection, String tableName, long value) throws SQLException
    {
        PreparedStatement preparedStatement = null;
        try
        {
            Long currentValue = this.query(connection, tableName);
            if (currentValue != null)
                preparedStatement = connection.prepareStatement(this.getUpdateSQL(tableName));
            else
                preparedStatement = connection.prepareStatement(this.getInsertSQL(tableName));
            preparedStatement.setLong(1, value);
            preparedStatement.executeUpdate();
        }
        finally
        {
            if (preparedStatement != null)
                preparedStatement.close();
        }
    }
}
