package org.dangcat.persistence.syntax;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orm.TableSqlBuilder;

/**
 * 表达式构建器。
 * @author dangcat
 * 
 */
public class OracleSyntaxAdapter extends SqlSyntaxHelperBase
{
    private static final String BLOB = "BLOB";
    private static final String CLOB = "CLOB";
    private static final String DATE = "DATE";
    private static final String INT = "INT";
    private static final String NUMERIC = "NUMERIC";
    private static final String SMALLINT = "SMALLINT";
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String VARCHAR2 = "VARCHAR2";

    protected Map<String, String> defaultParams = new HashMap<String, String>();

    /**
     * 构建表存在表达式。
     * @param databaseName 数据库名。
     * @param tableName 表名。
     * @return 查询语句。
     */
    @Override
    public String buildExistsStatement(String databaseName, String tableName)
    {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT 1 FROM USER_TABLES WHERE TABLE_NAME IN (UPPER('");
        sqlBuilder.append(tableName);
        sqlBuilder.append("'), LOWER('");
        sqlBuilder.append(tableName);
        sqlBuilder.append("'))");
        return sqlBuilder.toString();
    }

    /**
     * 构建含有范围载入的语句表达式。
     * @param sql 查询语句。
     * @param range 查询范围。
     * @return 载入的表达语句。
     */
    @Override
    public String buildRangeLoadStatement(String sql, Range range)
    {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT A.*, ROWNUM RN ");
        sqlBuilder.append(TableSqlBuilder.SEPERATE_LINE);
        sqlBuilder.append("FROM (");
        sqlBuilder.append(sql);
        sqlBuilder.append(") A ");
        sqlBuilder.append(TableSqlBuilder.SEPERATE_LINE);
        sqlBuilder.append("WHERE ROWNUM <= ");
        sqlBuilder.append(range.getTo());
        if (range.getFrom() > 1)
        {
            sqlBuilder.insert(0, "SELECT * FROM (");
            sqlBuilder.append(") WHERE RN >= ");
            sqlBuilder.append(range.getFrom());
        }
        range.setMode(Range.BY_SQLSYNTAX);
        return sqlBuilder.toString();
    }

    /**
     * 转换SQL栏位数据类型。
     * @param column 栏位对象。
     * @return SQL数据类型。
     */
    @Override
    public String getSqlType(Column column)
    {
        String sqlDataType = "";
        int displaySize = column.getDisplaySize();
        if (String.class.equals(column.getFieldClass()))
            sqlDataType = VARCHAR2 + "(" + displaySize + ")";
        else if (Byte.class.equals(column.getFieldClass()) || byte.class.equals(column.getFieldClass()) || Short.class.equals(column.getFieldClass()) || short.class.equals(column.getFieldClass())
                || Boolean.class.equals(column.getFieldClass()) || boolean.class.equals(column.getFieldClass()))
            sqlDataType = SMALLINT;
        else if (Integer.class.equals(column.getFieldClass()) || int.class.equals(column.getFieldClass()) || Long.class.equals(column.getFieldClass()) || long.class.equals(column.getFieldClass()))
            sqlDataType = INT;
        else if (Date.class.equals(column.getFieldClass()))
            sqlDataType = DATE;
        else if (Timestamp.class.equals(column.getFieldClass()))
            sqlDataType = TIMESTAMP;
        else if (Double.class.equals(column.getFieldClass()) || double.class.equals(column.getFieldClass()))
        {
            int scale = column.getScale();
            if (scale == 0)
                sqlDataType = NUMERIC + "(" + (displaySize - 1) + ")";
            else
                sqlDataType = NUMERIC + "(" + (displaySize - scale) + ", " + scale + ")";
        }
        else if (Byte[].class.equals(column.getFieldClass()) || byte[].class.equals(column.getFieldClass()))
            sqlDataType = BLOB;
        else if (Character[].class.equals(column.getFieldClass()) || char[].class.equals(column.getFieldClass()))
            sqlDataType = CLOB;
        return sqlDataType;
    }
}
