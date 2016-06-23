package org.dangcat.persistence.entity;

import org.dangcat.commons.formator.DateFormatProvider;
import org.dangcat.commons.formator.DateType;
import org.dangcat.commons.formator.FormatProvider;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.orderby.OrderByUnit;
import org.dangcat.persistence.orm.TableGenerator;
import org.dangcat.persistence.tablename.TableName;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Date;

class EntityFieldInfoCreator {
    private EntityMetaData entityMetaData = null;

    EntityFieldInfoCreator(EntityMetaData entityMetaData) {
        this.entityMetaData = entityMetaData;
    }

    private void createAutoIncrement(EntityField entityField) {
        if (entityField.getColumn().isAutoIncrement()) {
            org.dangcat.persistence.annotation.TableGenerator tableGeneratorAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.TableGenerator.class);
            if (tableGeneratorAnnotation != null)
                entityField.setTableGenerator(new TableGenerator(tableGeneratorAnnotation.tableName(), tableGeneratorAnnotation.idFieldName(), tableGeneratorAnnotation.valueFieldName()));
            this.entityMetaData.setAutoIncrement(entityField);
        }
    }

    private void createDateTime(EntityField entityField) {
        Column column = entityField.getColumn();
        column.setDateType(null);
        if (Date.class.isAssignableFrom(entityField.getClassType())) {
            org.dangcat.commons.formator.annotation.DateStyle dateStyleAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.commons.formator.annotation.DateStyle.class);
            if (dateStyleAnnotation != null)
                column.setDateType(dateStyleAnnotation.value());
            else
                column.setDateType(DateType.Full);
            if (column.getFormatProvider() == null)
                column.setFormatProvider(new DateFormatProvider(column.getDateType()));
            column.setDisplaySize(column.getFormatProvider().getFormat().length());
        }
    }

    private void createFieldName(EntityField entityField) {
        // 设置字段名。
        org.dangcat.persistence.annotation.Column columnAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.Column.class);
        if (columnAnnotation != null) {
            String fieldName = ValueUtils.isEmpty(columnAnnotation.fieldName()) ? entityField.getName() : columnAnnotation.fieldName();
            int index = fieldName.indexOf(".");
            if (index == -1)
                entityField.setFieldName(fieldName);
            else {
                TableName tableName = this.findJoinTableName(fieldName.substring(0, index));
                if (tableName != null)
                    entityField.setTableName(tableName);
                else {
                    tableName = this.findJoinTableName(fieldName.substring(index + 1));
                    if (tableName != null)
                        entityField.setTableName(tableName);
                }
                entityField.setFieldName(fieldName.substring(index + 1));
            }
        }
    }

    private void createFormatProvider(EntityField entityField) {
        org.dangcat.commons.formator.annotation.FormatProvider formatProviderAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.commons.formator.annotation.FormatProvider.class);
        if (formatProviderAnnotation != null && formatProviderAnnotation.value() != null) {
            FormatProvider formatProvider = (FormatProvider) ReflectUtils.newInstance(formatProviderAnnotation.value());
            if (formatProvider != null)
                entityField.getColumn().setFormatProvider(formatProvider);
        }
    }

    private void createJoinTable(EntityField entityField) {
        org.dangcat.persistence.annotation.JoinTable joinTableAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.JoinTable.class);
        if (joinTableAnnotation != null) {
            JoinTable joinTable = EntityHelper.createJoinTable(this.entityMetaData, joinTableAnnotation);
            entityField.setTableName(joinTable.getTableName());
            entityField.setJoin(true);
            // 连接表数据都算计算字段。
            entityField.getColumn().setCalculate(true);
        }
    }

    private OrderBy createOrderBy(EntityField entityField, OrderBy orderBy) {
        org.dangcat.persistence.annotation.OrderBy orderByAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.OrderBy.class);
        if (orderByAnnotation != null) {
            if (orderBy == null)
                orderBy = new OrderBy();
            orderBy.add(new OrderByUnit(entityField.getName(), orderByAnnotation.type(), orderByAnnotation.index()));
        }
        return orderBy;
    }

    private void createParam(EntityField entityField, org.dangcat.persistence.annotation.Param paramAnnotation) {
        Object value = ValueUtils.parseValue(paramAnnotation.classType(), paramAnnotation.value());
        if (value != null)
            entityField.getColumn().getParams().put(paramAnnotation.name(), value);
    }

    private void createParams(EntityField entityField) {
        org.dangcat.persistence.annotation.Params paramsAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.Params.class);
        if (paramsAnnotation != null) {
            for (org.dangcat.persistence.annotation.Param paramAnnotation : paramsAnnotation.value())
                this.createParam(entityField, paramAnnotation);
        } else {
            org.dangcat.persistence.annotation.Param paramAnnotation = this.getEntityFieldAnnotation(entityField, org.dangcat.persistence.annotation.Param.class);
            if (paramAnnotation != null)
                this.createParam(entityField, paramAnnotation);
        }
    }

    protected void execute() {
        OrderBy orderBy = null;
        for (EntityField entityField : this.entityMetaData.getEntityFieldCollection()) {
            this.createFieldName(entityField);
            // 自增字段。
            this.createAutoIncrement(entityField);
            // 产生关联表信息。
            this.createJoinTable(entityField);
            // 排序条件。
            orderBy = this.createOrderBy(entityField, orderBy);
            // Params
            this.createParams(entityField);
            // 格式化对象。
            this.createFormatProvider(entityField);
            // 日期类型。
            this.createDateTime(entityField);
        }
        // 如果有关联表，需要设置默认栏位的主表。
        if (this.entityMetaData.getJoinTableCollection().size() > 0) {
            for (EntityField entityField : this.entityMetaData.getEntityFieldCollection()) {
                if (entityField.getTableName() == null)
                    entityField.setTableName(this.entityMetaData.getTableName());
            }
        }
        this.entityMetaData.sort();

        // 设置排序条件。
        if (orderBy != null)
            this.entityMetaData.getTable().setOrderBy(orderBy);
    }

    private TableName findJoinTableName(String name) {
        TableName found = null;
        for (JoinTable joinTable : this.entityMetaData.getJoinTableCollection()) {
            TableName tableName = joinTable.getTableName();
            if (tableName.getName().equals(name) || tableName.getAlias().equals(name)) {
                found = tableName;
                break;
            }
        }
        return found;
    }

    private <T extends Annotation> T getEntityFieldAnnotation(EntityField entityField, Class<T> annotationClass) {
        T annotation = null;
        if (entityField.getPropertyDescriptor() != null) {
            Method readMethod = entityField.getPropertyDescriptor().getReadMethod();
            if (readMethod != null)
                annotation = readMethod.getAnnotation(annotationClass);
        }
        if (annotation == null && entityField.getField() != null)
            annotation = entityField.getField().getAnnotation(annotationClass);
        return annotation;
    }
}
