package org.dangcat.persistence.model;

import org.dangcat.commons.database.DatabaseType;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.calculate.CalculatorImpl;
import org.dangcat.persistence.event.TableEventAdapter;
import org.dangcat.persistence.exception.TableException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orm.SqlBuilder;
import org.dangcat.persistence.sql.Sqls;
import org.dangcat.persistence.tablename.TableName;
import org.dangcat.persistence.validator.TableDataValidator;
import org.dangcat.persistence.validator.exception.DataValidateException;

import java.util.*;

/**
 * 数据表对象。
 * @author dangcat
 * 
 */
public class Table implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;
    /** 计算器配置。 */
    private CalculatorImpl calculators = new CalculatorImpl();
    /** 栏位集合。 */
    private Columns columns;
    /** 所属数据库。 */
    private String databaseName;
    /** 过滤表达式。 */
    private FilterExpress filter;
    /** 固定过滤表达式。 */
    private FilterExpress fixFilter;
    /** 索引列表。 */
    private Collection<OrderBy> indexes = new HashSet<OrderBy>();
    /** 数据表对象名。 */
    private String name;
    /** 排序字段，多个字段以分号分隔。 */
    private OrderBy orderBy;
    /** 参数列表。 */
    private Map<String, Object> params = new HashMap<String, Object>();
    /** 载入数据范围。 */
    private Range range;
    /** 资料行。 */
    private Rows rows;
    /** 查询表达式。 */
    private SqlBuilder sqlBuilder;
    /** 当前SQL脚本名。 */
    private String sqlName;
    /** 查询语句配置集合。 */
    private Sqls sqls = new Sqls();
    /** 事件适配集合。 */
    private List<TableEventAdapter> tableEventAdapterList = new LinkedList<TableEventAdapter>();
    /** 表管理器。 */
    private TableManager tableManager = new TableManagerImpl();
    /** 数据表名。 */
    private TableName tableName;
    /** 数据表状态。 */
    private TableState tableState = TableState.Normal;
    /** 数据加总行。 */
    private Row total;

    public Table()
    {
        this.rows = new Rows(this);
        this.columns = new Columns(this);
    }

    public Table(String name)
    {
        this();
        this.name = name;
    }

    public Table(TableName tableName)
    {
        this();
        this.tableName = tableName;
    }

    public void addTableEventAdapter(TableEventAdapter tableEventAdapter)
    {
        if (tableEventAdapter != null && !this.tableEventAdapterList.contains(tableEventAdapter))
            this.tableEventAdapterList.add(tableEventAdapter);
    }

    public void calculate()
    {
        this.calculateRowNum();
        this.getCalculators().calculate(this);
    }

    public void calculateRowNum()
    {
        Column rowNumColumn = this.getColumns().getRowNumColumn();
        if (rowNumColumn != null)
        {
            Integer startRow = null;
            if (this.getRange() != null)
                startRow = this.getRange().getFrom();
            TableUtils.calculateRowNum(this.getRows(), startRow);
        }
    }

    public void calculateTotal() throws TableException
    {
        TableUtils.calculateTotal(this);
    }

    @Override
    public Object clone()
    {
        Table cloneTable = new Table(this.getName());
        cloneTable.getIndexes().addAll(this.getIndexes());
        cloneTable.setDatabaseName(this.getDatabaseName());
        for (Column column : this.getColumns())
            cloneTable.getColumns().add((Column) column.clone());
        return cloneTable;
    }

    /**
     * 在当前的数据源中构建数据表。
     */
    public int create() throws TableException
    {
        return this.tableManager.create(this);
    }

    private void decorateSqlBuilder(SqlBuilder sqlBuilder)
    {
        sqlBuilder.setFilter(this.getFilterSql());
        sqlBuilder.setTableName(this.getTableName().getName());
        sqlBuilder.setParams(this.getParams());
    }

    public int delete() throws TableException
    {
        return this.tableManager.delete(this);
    }

    public int drop() throws TableException
    {
        return this.tableManager.drop(this);
    }

    public int execute() throws TableException
    {
        return this.tableManager.execute(this);
    }

    public boolean exists()
    {
        return this.tableManager.exists(this);
    }

    public CalculatorImpl getCalculators()
    {
        return this.calculators;
    }

    public Columns getColumns()
    {
        return this.columns;
    }

    public String getDatabaseName()
    {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public DataState getDataState()
    {
        return this.rows.getDataState();
    }

    public FilterExpress getFilter()
    {
        return this.filter;
    }

    public void setFilter(FilterExpress filter) {
        this.filter = filter;
    }

    public String getFilterSql()
    {
        // 过滤条件。
        StringBuilder sqlFilter = new StringBuilder();
        if (this.getFilter() != null)
        {
            sqlFilter.append(" AND ");
            sqlFilter.append(this.getFilter());
        }
        // 固定过滤条件。
        if (this.getFixFilter() != null)
        {
            sqlFilter.append(" AND ");
            sqlFilter.append(this.getFixFilter());
        }
        return sqlFilter.toString();
    }

    public FilterExpress getFixFilter()
    {
        return this.fixFilter;
    }

    public void setFixFilter(FilterExpress fixFilter) {
        this.fixFilter = fixFilter;
    }

    public Collection<OrderBy> getIndexes()
    {
        return this.indexes;
    }

    public String getName()
    {
        if (this.name == null && this.tableName != null)
            return this.tableName.getName();
        return this.name;
    }

    public void setName(String name) {
        if (this.tableName != null && name != null && !name.equals(this.tableName.toString()))
            this.tableName = null;
        this.name = name;
    }

    public OrderBy getOrderBy()
    {
        return this.orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public Map<String, Object> getParams()
    {
        return this.params;
    }

    public Range getRange()
    {
        return this.range;
    }

    public void setRange(Range range) {
        this.range = range;
    }

    public Rows getRows()
    {
        return this.rows;
    }

    public SqlBuilder getSql()
    {
        if (this.sqlBuilder == null)
            this.sqlBuilder = new SqlBuilder();
        this.decorateSqlBuilder(this.sqlBuilder);
        return this.sqlBuilder;
    }

    public SqlBuilder getSql(DatabaseType databaseType, String name)
    {
        SqlBuilder sqlBuilder = null;
        if (!ValueUtils.isEmpty(name))
        {
            sqlBuilder = this.getSqls().find(databaseType, name);
            if (sqlBuilder != null)
                this.decorateSqlBuilder(sqlBuilder);
        }
        else
            sqlBuilder = this.getSql();
        return sqlBuilder;
    }

    public String getSqlName()
    {
        return this.sqlName;
    }

    public void setSqlName(String sqlName) {
        this.sqlName = sqlName;
    }

    public Sqls getSqls()
    {
        return this.sqls;
    }

    public List<TableEventAdapter> getTableEventAdapterList()
    {
        return this.tableEventAdapterList;
    }

    public TableName getTableName()
    {
        if (this.tableName == null)
            this.tableName = new TableName(this.name);
        return this.tableName;
    }

    public void setTableName(TableName tableName) {
        this.tableName = tableName;
    }

    public TableState getTableState()
    {
        return this.tableState;
    }

    public void setTableState(TableState tableState) {
        if (tableState == TableState.Insert) // 设置全表为新增状态。
            this.getRows().setDataState(DataState.Insert);
        else
            this.tableState = tableState;
        for (TableEventAdapter tableEventAdapter : this.tableEventAdapterList)
            tableEventAdapter.onTableStateChanged(this);
    }

    public Row getTotal()
    {
        return this.total;
    }

    public void setTotal(Row row) {
        if (row != null)
            row.setParent(this);
        this.total = row;
        if (row != null) {
            for (TableEventAdapter tableEventAdapter : this.tableEventAdapterList)
                tableEventAdapter.onCalculateTotal(row);
        }
    }

    public void load() throws TableException
    {
        this.tableManager.load(this);
    }

    public void loadMetaData() throws TableException
    {
        this.tableManager.loadMetaData(this);
    }

    public void removeTableEventAdapter(TableEventAdapter tableEventAdapter)
    {
        if (tableEventAdapter != null && this.tableEventAdapterList.contains(tableEventAdapter))
            this.tableEventAdapterList.remove(tableEventAdapter);
    }

    public void save() throws TableException
    {
        this.tableManager.save(this);
    }

    @Override
    public String toString()
    {
        // 打印Table表及数据
        StringBuffer info = new StringBuffer();
        info.append("Name : " + this.getName() + " \n");
        // 输出栏位信息。
        if (this.getRows().size() == 0)
            info.append(this.getColumns());
        // 输出数据行信息。
        info.append(this.getRows());
        return info.toString();
    }

    public int truncate() throws TableException
    {
        return this.tableManager.truncate(this);
    }

    public void validate() throws DataValidateException
    {
        TableDataValidator.validate(this);
    }
}