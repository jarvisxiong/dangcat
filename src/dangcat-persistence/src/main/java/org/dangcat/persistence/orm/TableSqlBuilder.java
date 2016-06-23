package org.dangcat.persistence.orm;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.SqlBuilderBase;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.sql.Sql;
import org.dangcat.persistence.tablename.TableName;

/**
 * 动态表语句构建器。
 *
 * @author dangcat
 */
public class TableSqlBuilder extends SqlBuilderBase {
    private Table table;

    public TableSqlBuilder(Table table, String databaseName) {
        super(databaseName);
        this.table = table;
    }

    /**
     * 构建数据表载入的表达式。
     *
     * @return 载入的表达语句。
     */
    @Override
    public SqlBuilder buildLoadStatement() {
        // 查询范围
        Range range = this.table.getRange();
        // 加入排序条件。
        OrderBy orderBy = this.table.getOrderBy();
        SqlBuilder sqlBuilder = this.getSql(Sql.QUERY);
        if (sqlBuilder == null)
            sqlBuilder = this.getSql(null);
        if (sqlBuilder.length() == 0 && !ValueUtils.isEmpty(this.getName())) {
            sqlBuilder = new SqlBuilder();
            sqlBuilder.setTableName(this.getLoadTableName());
            sqlBuilder.setFilter(this.getFilterSql());
            sqlBuilder.setParams(this.getParams());
            sqlBuilder.append("SELECT ");
            if (range != null) {
                if (range.getFrom() == 1)
                    sqlBuilder.append(Range.TOP_SQLFLAG);
            }
            // Fields
            StringBuilder sqlFields = new StringBuilder();
            for (Column column : this.table.getColumns()) {
                if (!column.isCalculate()) {
                    if (sqlFields.length() > 0)
                        sqlFields.append(", ");
                    sqlFields.append(column.getFieldName());
                }
            }
            if (sqlFields.length() == 0)
                sqlBuilder.append(" * ");
            else {
                sqlBuilder.append(sqlFields.toString());
                sqlBuilder.append(SEPERATE_LINE);
            }
            sqlBuilder.append("FROM ");
            sqlBuilder.append(SqlBuilder.TABLENAME_FLAG);
            sqlBuilder.append(SEPERATE_LINE);
            sqlBuilder.append("WHERE 1=1 ");
            sqlBuilder.append(SqlBuilder.FILTER_FLAG);
            // 加入排序条件。
            if (orderBy != null) {
                sqlBuilder.append(SEPERATE_LINE);
                sqlBuilder.append(orderBy.toString());
            }
        } else {
            sqlBuilder.setTableName(this.getName());
            sqlBuilder.setFilter(this.getFilterSql());
        }
        return super.buildLoadStatement(sqlBuilder, range);
    }

    @Override
    protected String getFilterSql() {
        return this.getTable().getFilterSql();
    }

    @Override
    protected String getSqlName() {
        return this.getTable().getSqlName();
    }

    /**
     * 数据表定义。
     */
    @Override
    public Table getTable() {
        return this.table;
    }

    /**
     * 数据表名。
     */
    @Override
    protected TableName getTableName() {
        return this.getTable().getTableName();
    }
}
