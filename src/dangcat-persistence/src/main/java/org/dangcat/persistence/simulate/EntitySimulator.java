package org.dangcat.persistence.simulate;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Table;

import java.util.Collection;
import java.util.List;

/**
 * 实体数据模拟器。
 *
 * @author dangcat
 */
public class EntitySimulator extends DataSimulator {
    private Class<?> classType = null;

    public EntitySimulator(Class<?> classType) {
        this.classType = classType;
    }

    @SuppressWarnings("unchecked")
    public <T> void create(Collection<T> entityCollection, int size) {
        this.setSize(size);
        for (int index = 0; index < size; index++) {
            T entity = (T) this.create(index);
            if (entity != null)
                entityCollection.add(entity);
        }
    }

    public Object create(int index) {
        Object entity = ReflectUtils.newInstance(this.classType);
        if (entity != null)
            this.modify(entity, index);
        return entity;
    }

    public Class<?> getClassType() {
        return classType;
    }

    @Override
    public Table getTable() {
        Table table = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getClassType());
        if (entityMetaData != null)
            table = entityMetaData.getTable();
        return table;
    }

    @Override
    public void initialize() {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.classType);
        if (entityMetaData != null) {
            for (EntityField entityField : entityMetaData.getEntityFieldCollection())
                this.addValueSimulator(entityField.getColumn());
        }
    }

    public void modify(List<?> entityList) {
        for (int index = 0; index < entityList.size(); index++) {
            Object entity = entityList.get(index);
            this.modify(entity, entityList.size() - index - 1);
        }
    }

    public void modify(Object entity, int index) {
        if (entity != null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            if (entityMetaData != null) {
                for (EntityField entityField : entityMetaData.getEntityFieldCollection()) {
                    if (!entityField.getColumn().isAutoIncrement()) {
                        Object value = this.getValue(index, entityField.getName());
                        entityField.setValue(entity, value);
                    }
                }
            }
        }
    }
}
