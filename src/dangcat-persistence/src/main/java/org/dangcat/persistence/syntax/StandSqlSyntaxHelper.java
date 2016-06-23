package org.dangcat.persistence.syntax;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orm.TableSqlBuilder;

import java.sql.Timestamp;
import java.util.Date;

/**
 * 标准表达式构建器。
 * @author dangcat
 * 
 */
public class StandSqlSyntaxHelper extends SqlSyntaxHelperBase
{
    private static final String BIGINT = "BIGINT";
    private static final String BLOB = "BLOB";
    private static final String CLOB = "CLOB";
    private static final String DATETIME = "DATETIME";
    private static final String INT = "INT";
    private static final String NUMERIC = "NUMERIC";
    private static final String SMALLINT = "SMALLINT";
    private static final String TINYINT = "TINYINT";
    private static final String VARCHAR = "VARCHAR";

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
        sqlBuilder.append("SELECT COUNT(*) FROM ");
        sqlBuilder.append(tableName);
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
        if (sql.indexOf(Range.TOP_SQLFLAG) > -1)
            return sql.replace(Range.TOP_SQLFLAG, "TOP " + range.getTo());
        String sqlExpress = sql.toLowerCase();
        final String flag = "select ";
        int index = sqlExpress.indexOf(flag);
        if (index != -1)
        {
            String select = sql.substring(index, index + flag.length());
            return sql.replace(select, select + "TOP " + range.getTo());
        }
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT TOP ");
        sqlBuilder.append(range.getTo());
        sqlBuilder.append(" * FROM (");
        sqlBuilder.append(TableSqlBuilder.SEPERATE_LINE);
        sqlBuilder.append(sql);
        sqlBuilder.append(TableSqlBuilder.SEPERATE_LINE);
        sqlBuilder.append(") A ");
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
            sqlDataType = VARCHAR + "(" + displaySize + ")";
        else if (Byte.class.equals(column.getFieldClass()) || byte.class.equals(column.getFieldClass()))
            sqlDataType = TINYINT;
        else if (Short.class.equals(column.getFieldClass()) || short.class.equals(column.getFieldClass()))
            sqlDataType = SMALLINT;
        else if (Integer.class.equals(column.getFieldClass()) || int.class.equals(column.getFieldClass()))
            sqlDataType = INT;
        else if (Boolean.class.equals(column.getFieldClass()) || boolean.class.equals(column.getFieldClass()))
            sqlDataType = INT;
        else if (Long.class.equals(column.getFieldClass()) || long.class.equals(column.getFieldClass()))
            sqlDataType = BIGINT;
        else if (Date.class.equals(column.getFieldClass()) || Timestamp.class.equals(column.getFieldClass()))
            sqlDataType = DATETIME;
        else if (Double.class.equals(column.getFieldClass()) || double.class.equals(column.getFieldClass()))
        {
            int scale = column.getScale();
            if (scale == 0)
                sqlDataType = NUMERIC + "(" + (displaySize - 1) + ")";
            else
                sqlDataType = NUMERIC + "(" + (displaySize - scale) + ", " + scale + ")";
        }
        else if (Character[].class.equals(column.getFieldClass()) || char[].class.equals(column.getFieldClass()))
            sqlDataType = CLOB;
        else if (Byte[].class.equals(column.getFieldClass()) || byte[].class.equals(column.getFieldClass()))
            sqlDataType = BLOB;
        return sqlDataType;
    }
}
