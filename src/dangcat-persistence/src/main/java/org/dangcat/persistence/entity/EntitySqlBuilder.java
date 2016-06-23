package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.CloneAble;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orderby.OrderByUnit;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.sql.Sql;
import org.dangcat.persistence.tablename.DynamicTableUtils;
import org.dangcat.persistence.tablename.TableName;

import java.util.Map;

/**
 * 实体SQL语句构建器。
 *
 * @author dangcat
 */
public class EntitySqlBuilder extends SqlBuilderBase {
    private EntityContext entityContext;
    private EntityMetaData entityMetaData;
    private FilterExpress filterExpress;
    private OrderBy orderBy;
    private Range range;

    public EntitySqlBuilder(EntityMetaData entityMetaData, String databaseName) {
        super(databaseName);
        this.entityMetaData = entityMetaData;
    }

    public EntitySqlBuilder(EntityMetaData entityMetaData, String databaseName, DeleteEntityContext deleteEntityContext) {
        this(entityMetaData, databaseName);
        this.entityContext = deleteEntityContext;
        this.filterExpress = deleteEntityContext.getFilterExpress();
    }

    public EntitySqlBuilder(EntityMetaData entityMetaData, String databaseName, LoadEntityContext loadEntityContext) {
        this(entityMetaData, databaseName);
        this.entityContext = loadEntityContext;
        this.filterExpress = loadEntityContext.getFilterExpress();
        this.range = loadEntityContext.getRange();
        this.orderBy = loadEntityContext.getOrderBy();
    }

    public EntitySqlBuilder(EntityMetaData entityMetaData, String databaseName, SaveEntityContext saveEntityContext) {
        this(entityMetaData, databaseName);
        this.entityContext = saveEntityContext;
    }

    @Override
    public SqlBuilder buildLoadStatement() {
        SqlBuilder sqlBuilder = this.getSql(Sql.QUERY);
        if (sqlBuilder == null)
            sqlBuilder = this.getSql(null);
        if (sqlBuilder.length() == 0 && !ValueUtils.isEmpty(this.getName())) {
            sqlBuilder = new SqlBuilder();
            sqlBuilder.setTableName(this.getLoadTableName());
            sqlBuilder.setFilter(this.getFilterSql());
            sqlBuilder.setParams(this.getParams());
            sqlBuilder.append("SELECT ");
            if (this.range != null) {
                if (this.range.getFrom() == 1)
                    sqlBuilder.append(Range.TOP_SQLFLAG);
            }
            // Fields
            StringBuilder sqlFields = new StringBuilder();
            for (EntityField entityField : this.entityMetaData.getEntityFieldCollection()) {
                if (entityField.isJoin() || !entityField.getColumn().isCalculate()) {
                    if (sqlFields.length() > 0)
                        sqlFields.append(", ");
                    String fieldName = entityField.getFilterFieldName();
                    sqlFields.append(fieldName);
                    if (fieldName.indexOf('.') != -1) {
                        sqlFields.append(" ");
                        sqlFields.append(entityField.getName());
                    }
                }
            }
            if (sqlFields.length() == 0)
                sqlBuilder.append(" * ");
            else {
                sqlBuilder.append(sqlFields.toString());
                sqlBuilder.append(SEPERATE_LINE);
            }
            // From
            sqlBuilder.append("FROM ");
            StringBuilder sqlFom = new StringBuilder();
            sqlFom.append(SqlBuilder.TABLENAME_FLAG);
            for (JoinTable joinTable : this.entityMetaData.getJoinTableCollection())
                sqlFom.append(joinTable);
            sqlBuilder.append(sqlFom.toString());
            // 过滤条件。
            sqlBuilder.append(SEPERATE_LINE);
            sqlBuilder.append("WHERE 1=1 ");
            sqlBuilder.append(SqlBuilder.FILTER_FLAG);
            // 加入排序条件。
            OrderBy orderBy = this.getOrderBy();
            if (orderBy != null && orderBy.size() > 0) {
                sqlBuilder.append(SEPERATE_LINE);
                sqlBuilder.append(orderBy.toString());
            }
        } else {
            sqlBuilder.setTableName(this.getName());
            sqlBuilder.setFilter(this.getFilterSql());
            sqlBuilder.setParams(this.getParams());
        }
        return super.buildLoadStatement(sqlBuilder, this.range);
    }

    /**
     * 构建查询数据表记录数的表达式。
     *
     * @return 计算的表达语句。
     */
    @Override
    public SqlBuilder buildTotalSizeStatement() {
        // 查询范围
        SqlBuilder sqlBuilder = this.getSql(Sql.TOTALSIZE);
        if (sqlBuilder == null) {
            sqlBuilder = this.getSql(Sql.QUERY);
            if (sqlBuilder == null)
                sqlBuilder = this.getSql(null);
            if (sqlBuilder.length() == 0 && !ValueUtils.isEmpty(this.getName())) {
                sqlBuilder = new SqlBuilder();
                sqlBuilder.setTableName(this.getLoadTableName());
                sqlBuilder.setFilter(this.getFilterSql());
                sqlBuilder.setParams(this.getParams());

                sqlBuilder.append("SELECT COUNT(*) ");
                sqlBuilder.append(Range.TOTALSIZE);
                // From
                StringBuilder sqlFom = new StringBuilder();
                sqlFom.append(SqlBuilder.TABLENAME_FLAG);
                for (JoinTable joinTable : this.entityMetaData.getJoinTableCollection())
                    sqlFom.append(joinTable);
                sqlBuilder.append(SEPERATE_LINE);
                sqlBuilder.append("FROM ");
                sqlBuilder.append(sqlFom.toString());
                // 过滤条件。
                sqlBuilder.append(SEPERATE_LINE);
                sqlBuilder.append("WHERE 1=1 ");
                sqlBuilder.append(SqlBuilder.FILTER_FLAG);
            }
            return this.replaceTableName(sqlBuilder);
        }
        return super.buildTotalSizeStatement();
    }

    /**
     * 转换字段名。
     *
     * @param fieldName 原始字段名。
     * @return
     */
    private String getFieldName(String fieldName) {
        EntityField entityField = this.entityMetaData.getEntityField(fieldName);
        if (entityField != null)
            fieldName = entityField.getFilterFieldName();
        return fieldName;
    }

    /**
     * 当前的过滤条件。
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    private FilterExpress getFilterExpress() {
        FilterExpress currentFilterExpress = null;
        FilterExpress filterExpress = this.filterExpress;
        if (filterExpress != null)
            currentFilterExpress = ((CloneAble<FilterExpress>) filterExpress).clone();
        if (currentFilterExpress != null)
            this.replaceFieldName(currentFilterExpress);
        return currentFilterExpress;
    }

    @Override
    protected String getFilterSql() {
        // 过滤条件。
        StringBuilder sqlFilter = new StringBuilder();
        FilterExpress filterExpress = this.getFilterExpress();
        if (filterExpress != null && !ValueUtils.isEmpty(filterExpress.toString())) {
            sqlFilter.append(" AND ");
            sqlFilter.append(filterExpress);
        }
        return sqlFilter.toString();
    }

    /**
     * 当前的排序条件。
     *
     * @return
     */
    private OrderBy getOrderBy() {
        OrderBy orderBy = this.orderBy;
        if (orderBy == null)
            orderBy = this.getTable().getOrderBy();
        OrderBy currentOrderBy = new OrderBy();
        if (orderBy != null) {
            for (OrderByUnit orderByUnit : orderBy) {
                String fieldName = this.getFieldName(orderByUnit.getFieldName());
                currentOrderBy.add(new OrderByUnit(fieldName, orderByUnit.getOrderByType()));
            }
        }
        return currentOrderBy;
    }

    @Override
    protected Map<String, Object> getParams() {
        return this.entityContext.getParams();
    }

    @Override
    protected String getSqlName() {
        return this.entityContext.getSqlName();
    }

    /**
     * 数据表定义。
     */
    @Override
    public Table getTable() {
        return this.entityMetaData.getTable();
    }

    /**
     * 数据表名。
     */
    @Override
    protected TableName getTableName() {
        if (this.entityContext != null) {
            String tableName = DynamicTableUtils.getTableName(this.entityContext.getTableName());
            if (!ValueUtils.isEmpty(tableName))
                return this.entityContext.getTableName();
        }
        return this.entityMetaData.getTableName();
    }

    /**
     * 当前的过滤条件。
     *
     * @return
     */
    private void replaceFieldName(FilterExpress filterExpress) {
        if (filterExpress instanceof FilterUnit) {
            FilterUnit filterUnit = (FilterUnit) filterExpress;
            String fieldName = this.getFieldName(filterUnit.getFieldName());
            filterUnit.setFieldName(fieldName);
        } else if (filterExpress instanceof FilterGroup) {
            FilterGroup filterGroup = (FilterGroup) filterExpress;
            for (FilterExpress childFilterExpress : filterGroup.getFilterExpressList())
                this.replaceFieldName(childFilterExpress);
        }
    }
}
