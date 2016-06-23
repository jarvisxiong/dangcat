package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.tablename.TableName;

import java.util.ArrayList;
import java.util.List;

/**
 * 连接表对象。
 * @author dangcat
 * 
 */
class JoinTable
{
    private List<JoinColumn> joinColumns = new ArrayList<JoinColumn>();
    private TableName joinTableName = null;
    private JoinType joinType = JoinType.Inner;
    private TableName tableName = null;

    public JoinTable(TableName tableName, TableName joinTableName)
    {
        this.tableName = tableName;
        this.joinTableName = joinTableName;
    }

    public JoinTable(TableName tableName, TableName joinTableName, org.dangcat.persistence.annotation.JoinTable joinTableAnnotation)
    {
        this.tableName = tableName;
        this.joinTableName = joinTableName;
        if (!ValueUtils.isEmpty(joinTableAnnotation.joinType()))
            this.joinType = joinTableAnnotation.joinType();
    }

    public void addJoinColumn(String name, String joinName)
    {
        this.getJoinColumns().add(new JoinColumn(this, name, joinName));
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof JoinTable)
        {
            JoinTable joinTable = (JoinTable) obj;
            if (!this.getTableName().equals(joinTable.getTableName()))
                return false;
            if (!this.getJoinTableName().equals(joinTable.getJoinTableName()))
                return false;
            if (!this.getJoinType().equals(joinTable.getJoinType()))
                return false;
            if (this.joinColumns.size() != joinTable.getJoinColumns().size())
                return false;
            for (JoinColumn srcJoinColumn : this.joinColumns)
            {
                if (!joinTable.getJoinColumns().contains(srcJoinColumn))
                    return false;
            }
            return true;
        }
        return super.equals(obj);
    }

    public List<JoinColumn> getJoinColumns()
    {
        return this.joinColumns;
    }

    public TableName getJoinTableName()
    {
        return this.joinTableName;
    }

    public JoinType getJoinType()
    {
        return this.joinType;
    }

    public TableName getTableName()
    {
        return this.tableName;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.tableName == null) ? 0 : this.tableName.toString().hashCode());
        result = prime * result + ((this.joinTableName == null) ? 0 : this.joinTableName.toString().hashCode());
        result = prime * result + ((this.joinType == null) ? 0 : this.joinType.hashCode());
        result = prime * result + ((this.joinColumns == null) ? 0 : this.joinColumns.size());
        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(this.getJoinType().name().toUpperCase());
        if (JoinType.Left.equals(this.getJoinType()) || JoinType.Right.equals(this.getJoinType()) || JoinType.Full.equals(this.getJoinType()))
            info.append(" OUTER");
        info.append(" JOIN ");
        info.append(this.getTableName());
        info.append(" ON ");
        for (JoinColumn joinColumn : this.joinColumns)
            info.append(joinColumn);
        return info.toString();
    }
}
