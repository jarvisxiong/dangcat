package org.dangcat.persistence.syntax;

import org.dangcat.persistence.model.Column;

/**
 * 表达式构建器。
 * @author dangcat
 * 
 */
public class SqlServerSyntaxAdapter extends StandSqlSyntaxHelper
{
    private static final String BIT = "BIT";
    private static final String IMAGE = "IMAGE";
    private static final String TEXT = "TEXT";

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
        sqlBuilder.append("SELECT 1 FROM sysobjects WHERE TYPE = 'U' AND id = OBJECT_ID('");
        sqlBuilder.append(tableName);
        sqlBuilder.append("')");
        return sqlBuilder.toString();
    }

    @Override
    protected void createIdentityColumnSql(Column column, StringBuilder sqlFields)
    {
        sqlFields.append(" ");
        sqlFields.append("IDENTITY(1, 1)");
    }

    /**
     * 转换SQL栏位数据类型。
     * @param column 栏位对象。
     * @return SQL数据类型。
     */
    @Override
    public String getSqlType(Column column)
    {
        String sqlDataType = super.getSqlType(column);
        if (Boolean.class.equals(column.getFieldClass()) || boolean.class.equals(column.getFieldClass()))
            sqlDataType = BIT;
        else if (char[].class.equals(column.getFieldClass()) || Character[].class.equals(column.getFieldClass()))
            sqlDataType = TEXT;
        else if (byte[].class.equals(column.getFieldClass()) || Byte[].class.equals(column.getFieldClass()))
            sqlDataType = IMAGE;

        return sqlDataType;
    }
}
