package org.dangcat.business.code.entity;

import java.util.Collection;
import java.util.Date;

import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.commons.database.Database;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 实体对象代码产生工具。
 * @author dangcat
 * 
 */
public class DomainCodeGenerator extends DirectoryCodeGenerator
{
    private static final String BASE_CLASS_NAME = "baseClassName";
    private static final String BASE_CLASS_SIMPLE_NAME = "baseClassSimpleName";
    private static final String COLUMNS = "columns";
    private static final String ENTITY_NAME = "entityName";
    private static final String HAS_DATE_TYPE = "hasDateType";
    private static final String TABLE_NAME = "tableName";
    private String baseClassName = null;
    private Database database = null;
    private Collection<String> tableNames = null;

    public DomainCodeGenerator()
    {
        this.addCodeGenerator(new EntityCodeGenerator());
        this.addCodeGenerator(new EntityResourcesGenerator());
    }

    @Override
    public void generate() throws Exception
    {
        String baseClassName = this.getBaseClassName();
        if (!ValueUtils.isEmpty(baseClassName))
        {
            this.put(BASE_CLASS_NAME, baseClassName);
            this.put(BASE_CLASS_SIMPLE_NAME, baseClassName.substring(baseClassName.lastIndexOf(".") + 1));
        }
        else
            this.put(BASE_CLASS_NAME, "");

        TableManager tableManager = new TableManager(this.database);
        tableManager.load();
        if (this.tableNames != null)
        {
            for (String tableName : this.tableNames)
                this.generate(tableManager.getTable(tableName));
        }
        else
        {
            for (Table table : tableManager.getTables())
                this.generate(table);
        }
    }

    private void generate(Table table) throws Exception
    {
        this.put(ENTITY_NAME, table.getName());
        this.put(TABLE_NAME, table.getTableName());
        this.put(COLUMNS, table.getColumns());

        boolean hasDateType = false;
        for (Column column : table.getColumns())
        {
            if (Date.class.equals(column.getFieldClass()))
            {
                hasDateType = true;
                break;
            }
        }
        this.put(HAS_DATE_TYPE, hasDateType);

        super.generate();
    }

    public String getBaseClassName()
    {
        return this.baseClassName;
    }

    public Database getDatabase()
    {
        return this.database;
    }

    @Override
    protected String getOutputFile()
    {
        return null;
    }

    public Collection<String> getTableNames()
    {
        return this.tableNames;
    }

    @Override
    protected String getTemplate()
    {
        return null;
    }

    public void setBaseClassName(String baseClassName)
    {
        this.baseClassName = baseClassName;
    }

    public void setDatabase(Database database)
    {
        this.database = database;
    }

    public void setTableNames(Collection<String> tableNames)
    {
        this.tableNames = tableNames;
    }
}
