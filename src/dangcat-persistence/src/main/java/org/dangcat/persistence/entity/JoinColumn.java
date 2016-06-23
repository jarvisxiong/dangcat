package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.ValueUtils;

public class JoinColumn {
    private String joinName;
    private JoinTable joinTable;
    private String name;

    public JoinColumn(JoinTable joinTable, String name, String joinName) {
        this.joinTable = joinTable;
        this.name = name;
        this.joinName = joinName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof JoinColumn) {
            JoinColumn joinColumn = (JoinColumn) obj;
            if (!this.getName().equals(joinColumn.getName()))
                return false;
            return this.getJoinName().equals(joinColumn.getJoinName());
        }
        return super.equals(obj);
    }

    public String getJoinName() {
        return this.joinName;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        String tableAlias = this.joinTable.getTableName().getAlias();
        if (!ValueUtils.isEmpty(tableAlias)) {
            info.append(tableAlias);
            info.append(".");
        }
        info.append(this.name);
        info.append(" = ");
        String joinTableAlias = this.joinTable.getJoinTableName().getAlias();
        if (!ValueUtils.isEmpty(joinTableAlias)) {
            info.append(joinTableAlias);
            info.append(".");
        }
        info.append(this.getJoinName());
        return info.toString();
    }
}
