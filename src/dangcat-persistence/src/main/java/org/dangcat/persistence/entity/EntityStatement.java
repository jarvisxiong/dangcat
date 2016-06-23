package org.dangcat.persistence.entity;

import org.dangcat.persistence.model.Column;

import java.util.LinkedList;
import java.util.List;

public class EntityStatement
{
    private String databaseName;
    private String deleteSql = null;
    private EntityMetaData entityMetaData;
    private List<String> generatedKeyList = new LinkedList<String>();
    private List<String> insertFieldNameList = new LinkedList<String>();
    private String insertSql = null;
    private List<String> modifiedFieldNameList = new LinkedList<String>();
    private String modifiedSql = null;
    private List<String> primaryKeyList = new LinkedList<String>();

    public EntityStatement(EntityMetaData entityMetaData, String databaseName)
    {
        this.entityMetaData = entityMetaData;
        this.databaseName = databaseName;
    }

    public String getDeleteSql()
    {
        return deleteSql.toString();
    }

    public List<String> getGeneratedKeyList()
    {
        return generatedKeyList;
    }

    public List<String> getInsertFieldNameList()
    {
        return insertFieldNameList;
    }

    public String getInsertSql()
    {
        return insertSql.toString();
    }

    public List<String> getModifiedFieldNameList()
    {
        return modifiedFieldNameList;
    }

    public String getModifiedSql()
    {
        return modifiedSql.toString();
    }

    public List<String> getPrimaryKeyList()
    {
        return primaryKeyList;
    }

    public void initialize()
    {
        this.initialize(null);
    }

    public void initialize(SaveEntityContext saveEntityContext)
    {
        EntitySqlBuilder entitySqlBuilder = new EntitySqlBuilder(this.entityMetaData, this.databaseName, saveEntityContext);
        // 构建查询语句
        this.insertSql = entitySqlBuilder.buildInsertStatement(this.insertFieldNameList);
        this.deleteSql = entitySqlBuilder.buildDeleteStatement(this.primaryKeyList);
        // 自增主键列表。
        Column[] primaryKeys = this.entityMetaData.getTable().getColumns().getPrimaryKeys();
        for (Column column : primaryKeys)
        {
            if (column.isIndentityGeneration())
                this.generatedKeyList.add(column.getFieldName());
        }
        this.modifiedSql = entitySqlBuilder.buildUpdateStatement(null, this.modifiedFieldNameList, this.primaryKeyList);
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("DatabaseName = ");
        info.append(this.databaseName);
        info.append(" GeneratedKeyList = {");
        boolean isFirst = true;
        for (String fieldName : this.getGeneratedKeyList())
        {
            if (!isFirst)
                info.append(", ");
            info.append(fieldName);
            isFirst = false;
        }
        info.append("} ");
        info.append(" deleteSql = {");
        info.append(this.getDeleteSql());
        info.append("} ");
        info.append(" InsertSql = {");
        info.append(this.getInsertSql());
        info.append("} ");
        info.append(" ModifiedSql = {");
        info.append(this.getModifiedSql());
        info.append("} ");
        return info.toString();
    }
}
