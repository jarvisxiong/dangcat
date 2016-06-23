package org.dangcat.persistence.validator;

import java.util.Map;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.entity.EntityField;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.model.Columns;
import org.dangcat.persistence.validator.impl.RangeValidator;
import org.dangcat.persistence.validator.impl.ValueMapValidator;

/**
 * Table数据基本校验。
 * @author dangcat
 * 
 */
public class EntityDataValidatorCreater
{
    private EntityMetaData entityMetaData = null;

    public EntityDataValidatorCreater(EntityMetaData entityMetaData)
    {
        this.entityMetaData = entityMetaData;
    }

    public void create()
    {
        Class<?> entityClass = this.entityMetaData.getEntityClass();
        Columns columns = this.entityMetaData.getTable().getColumns();
        columns.createDataValidators(entityClass);

        for (EntityField entityField : this.entityMetaData.getEntityFieldCollection())
        {
            // 范围校验器。
            this.createRangeValidator(entityClass, columns, entityField);
            // 数值映射校验器。
            this.createValueMapValidator(entityClass, columns, entityField);
            // 逻辑类型校验器。
            this.createLogicValidators(entityClass, columns, entityField);
        }
    }

    private void createLogicValidators(Class<?> entityClass, Columns columns, EntityField entityField)
    {
        org.dangcat.persistence.validator.annotation.LogicValidators logicValidatorsAnnotation = BeanUtils.getAnnotation(entityClass, entityField.getName(),
                org.dangcat.persistence.validator.annotation.LogicValidators.class);
        if (logicValidatorsAnnotation != null)
        {
            for (Class<?> classType : logicValidatorsAnnotation.value())
            {
                LogicValidator logicValidator = LogicValidatorUtils.creatInstance(classType, entityClass, entityField.getColumn());
                if (logicValidator != null)
                    columns.addDataValidator(logicValidator);
            }
        }
    }

    private void createRangeValidator(Class<?> entityClass, Columns columns, EntityField entityField)
    {
        org.dangcat.persistence.validator.annotation.RangeValidator rangeValidatorAnnotation = BeanUtils.getAnnotation(entityClass, entityField.getName(),
                org.dangcat.persistence.validator.annotation.RangeValidator.class);
        if (rangeValidatorAnnotation != null)
        {
            Object minValue = ValueUtils.parseValue(entityField.getClassType(), rangeValidatorAnnotation.minValue());
            Object maxValue = ValueUtils.parseValue(entityField.getClassType(), rangeValidatorAnnotation.maxValue());
            if (minValue != null || maxValue != null)
            {
                RangeValidator rangeValidator = new RangeValidator(entityClass, entityField.getColumn());
                rangeValidator.setMinValue(minValue);
                rangeValidator.setMaxValue(maxValue);
                columns.addDataValidator(rangeValidator);
            }
        }
    }

    private void createValueMapValidator(Class<?> entityClass, Columns columns, EntityField entityField)
    {
        org.dangcat.persistence.validator.annotation.ValueMapValidator valueMapValidatorAnnotation = BeanUtils.getAnnotation(entityClass, entityField.getName(),
                org.dangcat.persistence.validator.annotation.ValueMapValidator.class);
        if (valueMapValidatorAnnotation != null)
        {
            ResourceReader resourceReader = this.entityMetaData.getResourceReader();
            Map<Integer, String> valueMap = null;
            String resourceKey = valueMapValidatorAnnotation.resourceKey();
            if (!ValueUtils.isEmpty(resourceKey))
                valueMap = resourceReader.getValueMap(resourceKey);
            String tableNameResourceKey = entityField.getTableName().getName() + "." + entityField.getName();
            if (valueMap == null)
                valueMap = resourceReader.getValueMap(tableNameResourceKey);
            if (valueMap == null)
                valueMap = resourceReader.getValueMap(entityField.getName());
            if (valueMap != null && valueMap.size() > 0)
                columns.addDataValidator(new ValueMapValidator(entityClass, entityField.getColumn(), valueMap));
        }
    }
}
