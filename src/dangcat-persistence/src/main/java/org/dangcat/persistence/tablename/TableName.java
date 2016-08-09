package org.dangcat.persistence.tablename;

import org.dangcat.commons.utils.ValueUtils;

/**
 * 表名对象。
 *
 * @author dangcat
 */
public class TableName {
    private String alias;
    private String name;

    public TableName(String name) {
        this(name, null);
    }

    public TableName(String name, String alias) {
        this.name = name;
        this.alias = alias;
    }

    public TableName copy() {
        TableName tableName = null;
        try {
            tableName = (TableName) super.clone();
        } catch (CloneNotSupportedException e) {
        }
        return tableName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (this.getClass() != obj.getClass())
            return false;
        if (obj instanceof TableName)
            return this.getName().equalsIgnoreCase(((TableName) obj).getName());
        return false;
    }

    public String getAlias() {
        if (ValueUtils.isEmpty(this.alias))
            return this.getName();
        return this.alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return this.name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.getName() == null) ? 0 : this.getName().hashCode());
        return result;
    }

    @Override
    public String toString() {
        String name = this.getName();
        String alias = this.getAlias();

        StringBuilder info = new StringBuilder();
        info.append(name);
        if (!ValueUtils.isEmpty(alias) && !alias.equals(name)) {
            info.append(" ");
            info.append(alias);
        }
        return info.toString();
    }
}
