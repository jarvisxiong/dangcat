package org.dangcat.persistence.syntax;

import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orm.TableSqlBuilder;

/**
 * 表达式构建器。
 *
 * @author dangcat
 */
public class MysqlSyntaxAdapter extends StandSqlSyntaxHelper {
    private static final String BOOLEAN = "BOOLEAN";
    private static final String LONGBLOB = "LONGBLOB";
    private static final String LONGTEXT = "LONGTEXT";
    private static final String UNSIGNED = "UNSIGNED";

    public MysqlSyntaxAdapter() {
        this.defaultParams.put("useUnicode", "true");
        this.defaultParams.put("characterEncoding", "utf-8");
        this.defaultParams.put("useOldAliasMetadataBehavior", "true");
        this.defaultParams.put("rewriteBatchedStatements", "true");
    }

    /**
     * 构建表存在表达式。
     *
     * @param databaseName 数据库名。
     * @param tableName    表名。
     * @return 查询语句。
     */
    @Override
    public String buildExistsStatement(String databaseName, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT TABLE_ROWS FROM `information_schema`.`TABLES` WHERE TABLE_SCHEMA = database() AND TABLE_NAME = '");
        sqlBuilder.append(tableName);
        sqlBuilder.append("'");
        return sqlBuilder.toString();
    }

    /**
     * 构建含有范围载入的语句表达式。
     *
     * @param sql   查询语句。
     * @param range 查询范围。
     * @return 载入的表达语句。
     */
    @Override
    public String buildRangeLoadStatement(String sql, Range range) {
        StringBuilder sqlBuilder = new StringBuilder(sql);
        sqlBuilder.append(TableSqlBuilder.SEPERATE_LINE);
        sqlBuilder.append("LIMIT ");
        if (range.getFrom() == 1)
            sqlBuilder.append(range.getTo() > 0 ? range.getTo() : 0);
        else {
            int from = range.getFrom() - 1;
            sqlBuilder.append(from > 0 ? from : 0);
            sqlBuilder.append(", ");
            sqlBuilder.append(range.getPageSize() > 0 ? range.getPageSize() : 0);
        }
        range.setMode(Range.BY_SQLSYNTAX);
        return sqlBuilder.toString();
    }

    @Override
    protected void createIdentityColumnSql(Column column, StringBuilder sqlFields) {
        sqlFields.append(" ");
        sqlFields.append("AUTO_INCREMENT");
    }

    /**
     * 转换SQL栏位数据类型。
     *
     * @param column 栏位对象。
     * @return SQL数据类型。
     */
    @Override
    public String getSqlType(Column column) {
        String sqlDataType = super.getSqlType(column);
        if (column.isUnsigned()) {
            if (Byte.class.equals(column.getFieldClass()) || byte.class.equals(column.getFieldClass()))
                sqlDataType += " " + UNSIGNED;
            else if (Short.class.equals(column.getFieldClass()) || short.class.equals(column.getFieldClass()))
                sqlDataType += " " + UNSIGNED;
            else if (Integer.class.equals(column.getFieldClass()) || int.class.equals(column.getFieldClass()))
                sqlDataType += " " + UNSIGNED;
            else if (Long.class.equals(column.getFieldClass()) || long.class.equals(column.getFieldClass()))
                sqlDataType += " " + UNSIGNED;
        }

        if (Boolean.class.equals(column.getFieldClass()) || boolean.class.equals(column.getFieldClass()))
            sqlDataType = BOOLEAN;
        else if (char[].class.equals(column.getFieldClass()) || Character[].class.equals(column.getFieldClass()))
            sqlDataType = LONGTEXT;
        else if (Byte[].class.equals(column.getFieldClass()) || byte[].class.equals(column.getFieldClass()))
            sqlDataType = LONGBLOB;
        return sqlDataType;
    }
}
