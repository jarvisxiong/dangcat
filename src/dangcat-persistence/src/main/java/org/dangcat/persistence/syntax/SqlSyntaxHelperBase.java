package org.dangcat.persistence.syntax;

import org.dangcat.persistence.entity.SqlBuilderBase;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.model.Columns;
import org.dangcat.persistence.model.GenerationType;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.orm.SqlSyntaxHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class SqlSyntaxHelperBase implements SqlSyntaxHelper
{
    protected Map<String, String> defaultParams = new HashMap<String, String>();
    private Map<String, String> params = null;

    @Override
    public void buildCreateStatement(SqlBuilder sqlBuilder, SqlBuilderBase sqlBuilderBase)
    {
        Columns columns = sqlBuilderBase.getTable().getColumns();
        // 构建表语句。
        sqlBuilder.append(this.createCreateStatement(sqlBuilderBase.getName()));
        sqlBuilder.append(SqlBuilderBase.SEPERATE_LINE);
        sqlBuilder.append("( ");
        sqlBuilder.append(SqlBuilderBase.SEPERATE_LINE);
        StringBuilder sqlFields = new StringBuilder();
        for (Column column : columns)
        {
            if (column.isCalculate())
                continue;

            if (sqlFields.length() > 0)
            {
                sqlFields.append(", ");
                sqlFields.append(SqlBuilderBase.SEPERATE_LINE);
            }
            sqlFields.append(column.getFieldName());
            sqlFields.append(" ");
            sqlFields.append(this.getSqlType(column));
            if (GenerationType.IDENTITY.equals(column.getGenerationType()))
                this.createIdentityColumnSql(column, sqlFields);
            if (!column.isNullable())
                sqlFields.append(" NOT NULL");
        }
        sqlBuilder.append(sqlFields.toString());

        // 构建主键索引语句。
        Column[] keyColumns = columns.getPrimaryKeys();
        if (keyColumns.length != 0)
        {
            sqlBuilder.append(", ");
            sqlBuilder.append(SqlBuilderBase.SEPERATE_LINE);
            StringBuilder sqlPrimaryKeys = new StringBuilder();
            for (Column keyColumn : keyColumns)
            {
                if (sqlPrimaryKeys.length() > 0)
                    sqlPrimaryKeys.append(", ");
                sqlPrimaryKeys.append(keyColumn.getFieldName());
            }
            sqlBuilder.append("CONSTRAINT PK_");
            sqlBuilder.append(sqlBuilderBase.getName());
            sqlBuilder.append(" PRIMARY KEY (");
            sqlBuilder.append(sqlPrimaryKeys.toString());
            sqlBuilder.append(")");
        }
        sqlBuilder.append(SqlBuilderBase.SEPERATE_LINE);
        sqlBuilder.append(")");
        // 构建索引。
        int seq = 1;
        for (OrderBy orderBy : sqlBuilderBase.getTable().getIndexes())
        {
            String sqlIndex = sqlBuilderBase.buildIndexStatement(seq++, orderBy);
            if (sqlIndex != null)
            {
                if (sqlBuilder.length() > 0)
                    sqlBuilder.addBatch();
                sqlBuilder.append(sqlIndex);
            }
        }
    }

    protected boolean containsParam(String key, String value)
    {
        if (this.params != null && value != null)
            return value.equalsIgnoreCase(this.params.get(key));
        return false;
    }

    protected String createCreateStatement(String tableName)
    {
        return "CREATE TABLE " + tableName;
    }

    /**
     * 产生默认的参数配置。
     */
    public void createDefaultParams(Map<String, String> params)
    {
        for (Entry<String, String> entry : this.defaultParams.entrySet())
        {
            if (!params.containsKey(entry.getKey()))
                params.put(entry.getKey(), entry.getValue());
        }
        this.params = params;
    }

    protected void createIdentityColumnSql(Column column, StringBuilder sqlFields)
    {
    }
}
