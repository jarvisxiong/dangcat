package org.dangcat.boot.event;

import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.event.Event;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.DataState;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * 数据表事件。
 *
 * @author dangcat
 */
public class TableEvent extends Event {
    private static final long serialVersionUID = 1L;
    /**
     * 数据状态。
     */
    private DataState dataState = null;
    /**
     * 过滤条件。
     */
    private FilterExpress filterExpress = null;
    /**
     * 目标主键列表。
     */
    private Collection<Object> primaryKeys = null;
    /**
     * 目标表名。
     */
    private String tableName = null;
    /**
     * 目标列表。
     */
    private Collection<Object> values = null;

    public TableEvent(String tableName, DataState dataState) {
        this(tableName, dataState, null);
    }

    public TableEvent(String tableName, DataState dataState, FilterExpress filterExpress) {
        super(TableEvent.class.getSimpleName());
        this.tableName = tableName;
        this.dataState = dataState;
        this.filterExpress = filterExpress;
    }

    public void addPrimaryKey(Object... values) {
        if (values != null && values.length > 0) {
            if (this.primaryKeys == null)
                this.primaryKeys = new LinkedHashSet<Object>();
            if (!this.primaryKeys.contains(values)) {
                if (values.length > 1)
                    this.primaryKeys.add(values);
                else if (values[0] != null)
                    this.primaryKeys.add(values[0]);
            }
        }
    }

    public void addValues(Object... values) {
        if (values != null && values.length > 0) {
            if (this.values == null)
                this.values = new LinkedHashSet<Object>();
            if (!this.values.contains(values)) {
                if (values.length > 1)
                    this.values.add(values);
                else if (values[0] != null)
                    this.values.add(values[0]);
            }
        }
    }

    public DataState getDataState() {
        return this.dataState;
    }

    public void setDataState(DataState dataState) {
        this.dataState = dataState;
    }

    public FilterExpress getFilterExpress() {
        return this.filterExpress;
    }

    public void setFilterExpress(FilterExpress filterExpress) {
        this.filterExpress = filterExpress;
    }

    public Collection<Object> getPrimaryKeys() {
        return this.primaryKeys;
    }

    public String getTableName() {
        return this.tableName;
    }

    public Collection<Object> getValues() {
        return this.values;
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder(super.toString());
        info.append(Environment.LINE_SEPARATOR);
        info.append("TableName: " + this.getTableName());
        info.append(", ");
        info.append("DataState: " + this.getDataState());
        if (this.getFilterExpress() != null) {
            info.append(Environment.LINE_SEPARATOR);
            info.append("FilterExpress: " + this.getFilterExpress());
        }
        if (this.getPrimaryKeys() != null) {
            info.append(Environment.LINE_SEPARATOR);
            info.append("PrimaryKeyList: ");
            int count = 0;
            for (Object value : this.getPrimaryKeys()) {
                if (count > 0)
                    info.append(", ");
                if (value.getClass().isArray()) {
                    Object[] values = (Object[]) value;
                    StringBuilder arrayInfo = new StringBuilder();
                    for (Object key : values) {
                        if (arrayInfo.length() > 0)
                            arrayInfo.append(", ");
                        arrayInfo.append(key);
                    }
                    info.append("[");
                    info.append(arrayInfo.toString());
                    info.append("]");
                } else
                    info.append(value);
                count++;
            }
        }
        return info.toString();
    }
}
