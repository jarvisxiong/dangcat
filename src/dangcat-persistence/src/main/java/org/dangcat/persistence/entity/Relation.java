package org.dangcat.persistence.entity;

import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterGroup;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.orderby.OrderBy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 关联对象。
 *
 * @author dangcat
 */
public class Relation {
    private boolean associateDelete = true;
    private boolean associateLoad = true;
    private boolean associateSave = true;
    private String[] childFieldNames;
    private EntityField entityField;
    private Class<?> memberType;
    private String[] parentFieldNames;
    private OrderBy sortBy = null;

    public Relation(EntityField entityField, org.dangcat.persistence.annotation.Relation relationAnnotation) {
        this(entityField, relationAnnotation.parentFieldNames(), relationAnnotation.childFieldNames(), relationAnnotation.associateLoad(), relationAnnotation.associateSave(), relationAnnotation
                .associateDelete(), relationAnnotation.sortBy());
    }

    /**
     * 建立关联关系。
     *
     * @param fieldName           字段名。
     * @param parentFieldNames    父表字段关系。
     * @param childEntityMetaData 子实体元数据。
     * @param childFieldNames     子表映射关系。
     */
    public Relation(EntityField entityField, String[] parentFieldNames, String[] childFieldNames, boolean associateLoad, boolean associateSave, boolean associateDelete, String sortBy) {
        this.entityField = entityField;
        this.parentFieldNames = parentFieldNames;
        this.childFieldNames = childFieldNames;
        if (this.parentFieldNames.length == 0) {
            this.associateLoad = false;
            this.associateSave = false;
            this.associateDelete = false;
        } else {
            this.associateLoad = associateLoad;
            this.associateSave = associateSave;
            this.associateDelete = associateDelete;
        }
        this.sortBy = OrderBy.parse(sortBy);
    }

    public String[] getChildFieldNames() {
        return childFieldNames;
    }

    /**
     * 由父表实体产生子表过滤条件。
     *
     * @param parentEntity 父实体对象。
     * @return 过滤条件。
     * @throws EntityException
     */
    public FilterExpress getChildFilterExpress(Object parentEntity) throws EntityException {
        if (parentEntity == null)
            return null;

        EntityMetaData parentEntityMetaData = EntityHelper.getEntityMetaData(parentEntity.getClass());
        EntityMetaData childEntityMetaData = EntityHelper.getEntityMetaData(this.getMemberType());

        FilterGroup filterGroup = new FilterGroup();
        for (int i = 0; i < this.parentFieldNames.length; i++) {
            Object value = null;
            EntityField parentEntityField = parentEntityMetaData.getEntityField(this.parentFieldNames[i]);
            if (parentEntityField != null)
                value = parentEntityField.getValue(parentEntity);
            if (value != null) {
                EntityField childEntityField = childEntityMetaData.getEntityField(this.childFieldNames[i]);
                if (childEntityField == null)
                    return null;
                filterGroup.add(new FilterUnit(childEntityField.getFilterFieldName(), FilterType.eq, value));
            }
        }
        if (filterGroup.getFilterExpressList().size() == 0)
            return null;
        return filterGroup.getFilterExpressList().size() == 1 ? filterGroup.getFilterExpressList().get(0) : filterGroup;
    }

    /**
     * 读取明细数据的关联对象。
     *
     * @param instance 父实例对象。
     * @return
     */
    @SuppressWarnings("unchecked")
    public Collection<Object> getMembers(Object instance) {
        Collection<Object> members = null;
        try {
            Object value = this.entityField.getValue(instance);
            if (value != null) {
                if (this.isCollectionMember())
                    members = (Collection) value;
                else {
                    members = new HashSet<Object>();
                    members.add(value);
                }
            }
        } catch (Exception e) {
        }
        return members;
    }

    /**
     * 读取字段的泛型类型。
     *
     * @param field 字段对象。
     * @param index 泛型位置。
     * @return 泛型类型
     */
    public Class<?> getMemberType() {
        if (this.memberType == null) {
            this.memberType = this.entityField.getClassType();
            if (Collection.class.isAssignableFrom(this.entityField.getClassType())) {
                Type genericFieldType = this.entityField.getGenericType();
                if (genericFieldType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) genericFieldType;
                    Type[] actualTypes = parameterizedType.getActualTypeArguments();
                    if (actualTypes.length > 0)
                        this.memberType = (Class<?>) actualTypes[0];
                }
            }
        }
        return this.memberType;
    }

    public String getName() {
        return this.entityField.getName();
    }

    public String[] getParentFieldNames() {
        return parentFieldNames;
    }

    public OrderBy getSortBy() {
        return sortBy;
    }

    public boolean isAssociateDelete() {
        return associateDelete;
    }

    public void setAssociateDelete(boolean associateDelete) {
        this.associateDelete = associateDelete;
    }

    public boolean isAssociateLoad() {
        return associateLoad;
    }

    public void setAssociateLoad(boolean associateLoad) {
        this.associateLoad = associateLoad;
    }

    public boolean isAssociateSave() {
        return this.associateSave;
    }

    public void setAssociateSave(boolean associateSave) {
        this.associateSave = associateSave;
    }

    public boolean isCollectionMember() {
        return Collection.class.isAssignableFrom(this.entityField.getClassType());
    }

    /**
     * 载入关联属性。
     *
     * @param entity     父实体。
     * @param entityList 子实体列表。
     * @throws EntityException
     */
    @SuppressWarnings("unchecked")
    public void load(Object entity, List<?> entityList) {
        if (entity == null || entityList == null || entityList.size() == 0)
            return;

        try {
            Object value = this.entityField.getValue(entity);
            // 泛型集合保存多个明细对象。
            if (Collection.class.isAssignableFrom(entityField.getClassType())) {
                if (value != null) // 必须内建实例对象。
                {
                    Collection entityCollection = (Collection) value;
                    entityCollection.clear();
                    entityCollection.addAll(entityList);
                }
            } else if (entityList.size() == 1) // 非集合只能有一个对象。
            {
                if (this.entityField.getClassType().isAssignableFrom(entityList.get(0).getClass()))
                    this.entityField.setValue(entity, entityList.get(0));
            } else
                this.entityField.setValue(entity, null);
        } catch (Exception e) {
        }
    }

    @Override
    public String toString() {
        StringBuilder info = new StringBuilder();
        info.append(Relation.class.getSimpleName());
        info.append(" : ");
        info.append("fieldName = ");
        info.append(this.getName());
        info.append(", parentFieldNames = {");
        boolean isFirst = true;
        for (String fieldName : this.getParentFieldNames()) {
            if (!isFirst)
                info.append(", ");
            info.append(fieldName);
            isFirst = false;
        }
        info.append("}, childFieldNames = {");
        isFirst = true;
        for (String fieldName : this.getChildFieldNames()) {
            if (!isFirst)
                info.append(", ");
            info.append(fieldName);
            isFirst = false;
        }
        info.append("}, associateLoad = ");
        info.append(this.associateLoad);
        info.append(", associateSave = ");
        info.append(this.isAssociateSave());
        info.append(", associateDelete = ");
        info.append(this.isAssociateDelete());
        return info.toString();
    }

    /**
     * 同步父实体和子实体关联数据。
     *
     * @param parentEntity 父实体。
     * @param childEntity  子实体。
     * @throws EntityException
     */
    public void update(Object parentEntity, Collection<Object> members) throws EntityException {
        EntityMetaData parentEntityMetaData = EntityHelper.getEntityMetaData(parentEntity.getClass());
        for (int i = 0; i < this.parentFieldNames.length; i++) {
            EntityField parentEntityField = parentEntityMetaData.getEntityField(this.parentFieldNames[i]);
            if (parentEntityField != null) {
                EntityField childEntityField = null;
                for (Object member : members) {
                    if (childEntityField == null) {
                        EntityMetaData childEntityMetaData = EntityHelper.getEntityMetaData(member.getClass());
                        childEntityField = childEntityMetaData.getEntityField(this.childFieldNames[i]);
                    }
                    if (childEntityField != null) {
                        Object value = parentEntityField.getValue(parentEntity);
                        if (parentEntityField.getColumn().isSequenceGeneration() && value == null)
                            continue;
                        childEntityField.setValue(member, value);
                    }
                }
            }
        }
    }
}
