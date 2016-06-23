package org.dangcat.persistence.entity;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.*;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orm.SessionFactory;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.orm.SqlSyntaxHelper;
import org.dangcat.persistence.sql.Sql;
import org.dangcat.persistence.tablename.DynamicTableUtils;
import org.dangcat.persistence.tablename.TableName;

import java.util.List;
import java.util.Map;

/**
 * 语句构建基础。
 * @author dangcat
 * 
 */
public abstract class SqlBuilderBase
{
    public static final String SEPERATE_LINE = Environment.LINE_SEPARATOR;
    private String databaseName;

    public SqlBuilderBase(String databaseName)
    {
        this.databaseName = databaseName;
    }

    /**
     * 构建建立数据表的表达式。
     * @param table 数据表对象。
     * @return 载入的表达语句。
     */
    public SqlBuilder buildCreateStatement()
    {
        SqlBuilder sqlBuilder = this.getSql(Sql.CREATE);
        if (sqlBuilder != null)
            return sqlBuilder;
        sqlBuilder = new SqlBuilder();
        Columns columns = this.getTable().getColumns();
        if (columns == null || columns.size() == 0)
            return sqlBuilder;

        SqlSyntaxHelper sqlSyntaxHelper = SessionFactory.getInstance().getSqlSyntaxHelper(this.getDatabaseName());
        sqlSyntaxHelper.buildCreateStatement(sqlBuilder, this);
        return this.replaceTableName(sqlBuilder);
    }

    /**
     * 构建删除表的表达式。
     * @return 存储的表达语句。
     */
    public SqlBuilder buildDeleteStatement()
    {
        SqlBuilder sqlBuilder = this.getSql(Sql.DELETE);
        if (sqlBuilder != null)
            return sqlBuilder;
        sqlBuilder = new SqlBuilder();
        sqlBuilder.append("DELETE FROM ");
        sqlBuilder.append(this.getName());
        String sqlFilter = this.getFilterSql();
        if (!ValueUtils.isEmpty(sqlFilter))
        {
            sqlBuilder.append(SEPERATE_LINE);
            if (!ValueUtils.isEmpty(sqlFilter))
            {
                sqlBuilder.append("WHERE 1=1");
                sqlBuilder.append(sqlFilter);
            }
        }
        sqlBuilder.append(SEPERATE_LINE);
        return this.replaceTableName(sqlBuilder);
    }

    /**
     * 构建数据行的删除的表达式。
     * @return 存储的表达语句。
     */
    public String buildDeleteStatement(List<String> fieldNameList)
    {
        // 主键数组
        Column[] keyColumns = this.getTable().getColumns().getPrimaryKeys();
        if (keyColumns == null || keyColumns.length == 0)
            return null;

        fieldNameList.clear();
        SqlBuilder sqlBuilder = new SqlBuilder();
        for (Column column : keyColumns)
        {
            if (fieldNameList.size() > 0)
                sqlBuilder.append("AND ");
            else
            {
                sqlBuilder.append("DELETE FROM ");
                sqlBuilder.append(this.getName());
                sqlBuilder.append(" WHERE ");
            }
            sqlBuilder.append(column.getFieldName() + " = ? ");
            fieldNameList.add(column.getName());
        }
        return sqlBuilder.toString();
    }

    /**
     * 构建删除数据表的表达式。
     * @param table 数据表对象。
     * @return 删除的表达语句。
     */
    public SqlBuilder buildDropStatement()
    {
        SqlBuilder sqlBuilder = this.getSql(Sql.DROP);
        if (sqlBuilder != null)
            return sqlBuilder;
        sqlBuilder = new SqlBuilder();
        sqlBuilder.append("DROP TABLE ");
        sqlBuilder.append(this.getActualTableName());
        return this.replaceTableName(sqlBuilder);
    }

    /**
     * 构建执行的表达式。
     * @return 表达语句。
     */
    public SqlBuilder buildExecuteStatement()
    {
        SqlBuilder sqlBuilder = this.getSql(Sql.EXECUTE);
        if (sqlBuilder != null)
            return sqlBuilder;
        return this.getSql(null);
    }

    /**
     * 构建表存在查询表达语句。
     * @return
     */
    public String buildExistsStatement()
    {
        String databaseName = this.getDatabaseName();
        String tableName = this.getActualTableName();
        SqlSyntaxHelper sqlSyntaxHelper = SessionFactory.getInstance().getSqlSyntaxHelper(databaseName);
        return sqlSyntaxHelper.buildExistsStatement(databaseName, tableName);
    }

    /**
     * 构建索引表达式。
     * @param tableName 数据表名。
     * @param seq 索引序号。
     * @param orderBy 排序方式。
     * @return 构建语句。
     */
    public String buildIndexStatement(Integer seq, OrderBy orderBy)
    {
        String index = orderBy.toIndex();
        if (ValueUtils.isEmpty(index))
            return null;

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("CREATE INDEX IX_");
        sqlBuilder.append(this.getTableName().getName());
        sqlBuilder.append("_");
        sqlBuilder.append(seq);
        sqlBuilder.append(" ON ");
        sqlBuilder.append(this.getName());
        sqlBuilder.append("(");
        sqlBuilder.append(index);
        sqlBuilder.append(")");
        return sqlBuilder.toString();
    }

    /**
     * 构建数据行的插入的表达式。
     * @return 存储的表达语句。
     */
    public String buildInsertStatement(List<String> fieldNameList)
    {
        fieldNameList.clear();
        // 字段内容。
        StringBuilder sqlFields = new StringBuilder();
        // 值内容。
        StringBuilder sqlValues = new StringBuilder();
        for (Column column : this.getTable().getColumns())
        {
            // 自动新增的不需要插入。
            if (column.isIndentityGeneration() || column.isCalculate())
                continue;

            if (sqlFields.length() > 0)
            {
                sqlFields.append(", ");
                sqlValues.append(", ");
            }

            sqlFields.append(column.getFieldName());
            sqlValues.append("?");
            fieldNameList.add(column.getName());
        }
        // 构建新增脚本。
        StringBuilder sqlBuilder = new StringBuilder();
        if (sqlFields.length() > 0)
        {
            sqlBuilder.append("INSERT INTO ");
            sqlBuilder.append(this.getName());
            sqlBuilder.append("(");
            sqlBuilder.append(sqlFields.toString());
            sqlBuilder.append(")");
            sqlBuilder.append(SEPERATE_LINE);
            sqlBuilder.append("VALUES(");
            sqlBuilder.append(sqlValues.toString());
            sqlBuilder.append(") ");
        }
        return sqlBuilder.toString();
    }

    /**
     * 构建数据表载入的表达式。
     * @return 载入的表达语句。
     */
    public abstract SqlBuilder buildLoadStatement();

    /**
     * 构建数据表载入的表达式。
     * @return 载入的表达语句。
     */
    protected SqlBuilder buildLoadStatement(SqlBuilder sqlBuilder, Range range)
    {
        String sqlExpress = sqlBuilder.toString();
        if (range != null && !ValueUtils.isEmpty(sqlExpress))
        {
            SqlSyntaxHelper sqlSyntaxHelper = SessionFactory.getInstance().getSqlSyntaxHelper(this.getDatabaseName());
            String sqlStatement = sqlSyntaxHelper.buildRangeLoadStatement(sqlExpress, range);
            sqlExpress = sqlStatement.replace(Range.TOP_SQLFLAG, "");
            sqlBuilder = new SqlBuilder(sqlExpress);
        }
        return this.replaceTableName(sqlBuilder);
    }

    /**
     * 构建查询数据表记录数的表达式。
     * @return 计算的表达语句。
     */
    public SqlBuilder buildTotalSizeStatement()
    {
        // 查询范围
        SqlBuilder sqlBuilder = this.getSql(Sql.TOTALSIZE);
        if (sqlBuilder != null)
            return this.replaceTableName(sqlBuilder);
        sqlBuilder = this.getTable().getSql();
        if (sqlBuilder.length() == 0 && !ValueUtils.isEmpty(this.getName()))
        {
            sqlBuilder = new SqlBuilder();
            sqlBuilder.setTableName(this.getLoadTableName());
            sqlBuilder.setFilter(this.getFilterSql());
            sqlBuilder.setParams(this.getParams());

            sqlBuilder.append("SELECT COUNT(*) ");
            sqlBuilder.append(Range.TOTALSIZE);
            sqlBuilder.append(" FROM ");
            sqlBuilder.append(SqlBuilder.TABLENAME_FLAG);
            sqlBuilder.append(SEPERATE_LINE);
            sqlBuilder.append("WHERE 1=1 ");
            sqlBuilder.append(SqlBuilder.FILTER_FLAG);
            return this.replaceTableName(sqlBuilder);
        }

        sqlBuilder.setTableName(this.getName());
        sqlBuilder.setFilter(this.getFilterSql());
        sqlBuilder.append("SELECT COUNT(*) ");
        sqlBuilder.append(Range.TOTALSIZE);
        sqlBuilder.append(" FROM (");

        SqlBuilder result = new SqlBuilder();
        result.append(SEPERATE_LINE);
        result.append(sqlBuilder.toString());
        result.append(SEPERATE_LINE);
        result.append(") T");
        return this.replaceTableName(result);
    }

    /**
     * 构建清除表的表达式。
     * @return 存储的表达语句。
     */
    public SqlBuilder buildTruncateStatement()
    {
        SqlBuilder sqlBuilder = new SqlBuilder();
        sqlBuilder.append("TRUNCATE TABLE ");
        sqlBuilder.append(this.getName());
        return this.replaceTableName(sqlBuilder);
    }

    /**
     * 构建数据行的修改的表达式。
     * @return 存储的表达语句。
     */
    public String buildUpdateStatement(Row row, List<String> fieldNameList, List<String> primaryKeyList)
    {
        // 主键数组
        Table table = this.getTable();
        Column[] keyColumns = table.getColumns().getPrimaryKeys();
        if (keyColumns == null || keyColumns.length == 0)
            return null;

        fieldNameList.clear();
        primaryKeyList.clear();

        // 被修改的字段值。
        StringBuilder sqlBuilder = new StringBuilder();
        for (Column column : table.getColumns())
        {
            if (column.isCalculate())
                continue;

            if (row == null || row.getField(column.getName()).getDataState() == DataState.Modified)
            {
                if (sqlBuilder.length() == 0)
                {
                    sqlBuilder.append("UPDATE ");
                    sqlBuilder.append(this.getName());
                    sqlBuilder.append(" SET ");
                }
                else
                    sqlBuilder.append(", ");

                sqlBuilder.append(column.getFieldName());
                sqlBuilder.append(" = ? ");
                fieldNameList.add(column.getName());
            }
        }
        // 主键条件。
        if (sqlBuilder.length() > 0)
        {
            for (Column column : keyColumns)
            {
                if (primaryKeyList.size() == 0)
                    sqlBuilder.append(" WHERE ");
                else
                    sqlBuilder.append("AND ");
                sqlBuilder.append(column.getFieldName());
                sqlBuilder.append(" = ? ");
                primaryKeyList.add(column.getName());
            }
        }
        return sqlBuilder.toString();
    }

    protected String getActualTableName()
    {
        return DynamicTableUtils.getActualTableName(this.getTableName());
    }

    /**
     * 数据库名。
     */
    protected String getDatabaseName()
    {
        return this.databaseName;
    }

    protected DatabaseType getDatabaseType()
    {
        return SessionFactory.getInstance().getDatabaseType(this.getDatabaseName());
    }

    /**
     * 过滤条件。
     */
    protected abstract String getFilterSql();

    protected String getLoadTableName()
    {
        TableName tableName = this.getTableName();
        String loadTableName = DynamicTableUtils.getTableName(tableName);
        String alias = tableName.getAlias();
        if (!ValueUtils.isEmpty(alias) && !alias.equals(tableName.getName()))
            loadTableName += " " + alias;
        return loadTableName;
    }

    /**
     * 数据表名对象。
     */
    public String getName()
    {
        return DynamicTableUtils.getTableName(this.getTableName());
    }

    protected Map<String, Object> getParams()
    {
        return this.getTable().getParams();
    }

    protected SqlBuilder getSql(String name)
    {
        SqlBuilder sqlBuilder = null;
        Table table = this.getTable();
        // 以当前命名为首选。
        if (!ValueUtils.isEmpty(this.getSqlName()))
            sqlBuilder = table.getSql(this.getDatabaseType(), this.getSqlName());
        if (sqlBuilder == null)
            sqlBuilder = table.getSql(this.getDatabaseType(), name);
        return sqlBuilder;
    }

    protected abstract String getSqlName();

    /**
     * 数据表定义。
     */
    public abstract Table getTable();

    /**
     * 数据表名对象。
     */
    protected abstract TableName getTableName();

    protected SqlBuilder replaceTableName(SqlBuilder sqlBuilder)
    {
        return DynamicTableUtils.replaceTableName(sqlBuilder, this.getTableName());
    }
}
