package org.dangcat.business.settle;

import org.dangcat.persistence.annotation.Column;
import org.dangcat.persistence.annotation.Table;
import org.dangcat.persistence.entity.EntityBase;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;

@Table
public class SettleRecord extends EntityBase implements DataStatus {
    public static final String MaxId = "MaxId";
    public static final String TableName = "TableName";
    private static final long serialVersionUID = 1L;
    @Column(index = 1)
    private Integer maxId = null;

    @Column(index = 0, isPrimaryKey = true, displaySize = 40)
    private String tableName = null;

    public SettleRecord() {

    }

    public SettleRecord(String tableName) {
        this.tableName = tableName;
    }

    @Override
    public DataState getDataState() {
        if (super.getDataState() != DataState.Insert)
            return DataState.Modified;
        return DataState.Insert;
    }

    public Integer getMaxId() {
        return maxId;
    }

    public void setMaxId(Integer maxId) {
        this.maxId = maxId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
