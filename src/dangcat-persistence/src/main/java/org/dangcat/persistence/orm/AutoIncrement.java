package org.dangcat.persistence.orm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 自动主键生成器。
 *
 * @author dangcat
 */
class AutoIncrement {
    private int count = 0;
    private long currentValue = -1l;
    private String fieldName = null;
    private TableGenerator tableGenerator = null;
    private String tableName = null;

    AutoIncrement(String tableName, String fieldName, TableGenerator tableGenerator) {
        this.tableName = tableName;
        this.fieldName = fieldName;
        this.tableGenerator = tableGenerator;
    }

    /**
     * 初始化数列好。
     *
     * @param connection 数据库连接。
     * @throws SQLException 运行异常。
     */
    protected void initialize(Connection connection) throws SQLException {
        if (this.tableGenerator != null) {
            Long currentValue = this.tableGenerator.query(connection, this.tableName);
            if (currentValue != null)
                this.currentValue = currentValue;
        } else {
            Statement statement = null;
            try {
                statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery("SELECT MAX(" + this.fieldName + ") MAXID FROM " + this.tableName);
                if (resultSet.next() && resultSet.getObject("MAXID") != null)
                    this.currentValue = resultSet.getLong("MAXID");
                resultSet.close();
            } finally {
                if (statement != null)
                    statement.close();
            }
        }
    }

    /**
     * 读取下一个序列号。
     *
     * @param classType 数据类型。
     * @return 序列号。
     */
    protected synchronized Object nextSequence(Class<?> classType) {
        this.currentValue++;
        this.count++;
        if (Long.class.equals(classType))
            return this.currentValue;
        return (int) this.currentValue;
    }

    /**
     * 更新序号策略表的值。
     *
     * @param connection 数据库连接。
     * @throws SQLException 执行异常。
     */
    protected void update(Connection connection) throws SQLException {
        if (this.tableGenerator != null && this.count > 0)
            this.tableGenerator.update(connection, this.tableName, this.currentValue);
    }
}
