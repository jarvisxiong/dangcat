package org.dangcat.persistence.entity;

import org.dangcat.persistence.DataReader;
import org.dangcat.persistence.filter.FilterExpress;

import java.util.LinkedList;
import java.util.List;

/**
 * 实体对象数据读取器。
 *
 * @author dangcat
 */
public class EntityDataReader<T> extends EntityDataAccess<T> implements DataReader {
    private List<T> entityList = null;
    private FilterExpress filterExpress = null;

    public EntityDataReader(List<T> entityList) {
        super(entityList);
    }

    public EntityDataReader(List<T> entityList, Class<T> classType) {
        super(entityList);
        this.setClassType(classType);
    }

    @Override
    public FilterExpress getFilterExpress() {
        return this.filterExpress;
    }

    @Override
    public void setFilterExpress(FilterExpress filterExpress) {
        this.filterExpress = filterExpress;
        this.refresh();
    }

    @Override
    public Object getValue(int index, String fieldName) {
        List<T> entityList = this.entityList;
        if (entityList == null)
            entityList = this.getEntityList();

        Object value = null;
        if (index < this.size()) {
            T entity = entityList.get(index);
            value = this.getEntityMetaData().getValue(fieldName, entity);
        } else if (index == this.size()) {
            if (this.getTotal() != null)
                value = this.getEntityMetaData().getValue(fieldName, this.getTotal());
        }
        return value;
    }

    @Override
    public void refresh() {
        if (this.filterExpress == null) {
            this.entityList = null;
            return;
        }
        List<T> entityList = new LinkedList<T>();
        for (T entity : this.getEntityList()) {
            if (this.filterExpress.isValid(entity))
                entityList.add(entity);
        }
        this.entityList = entityList;
    }

    @Override
    public int size() {
        return this.entityList == null ? super.size() : this.entityList.size();
    }
}
