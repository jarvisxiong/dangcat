package org.dangcat.persistence.entity;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.Column;

import java.util.LinkedHashMap;
import java.util.Map;

class EntityMetaUtils
{
    /**
     * 比较两个实体大小。
     * @param srcEntity 来源实体对象。
     * @param dstEntity 目标实体对象。
     * @return
     */
    protected static int compare(EntityMetaData entityMetaData, Object srcEntity, Object dstEntity)
    {
        if (srcEntity == null || dstEntity == null)
        {
            if (srcEntity != null)
                return 1;
            if (dstEntity != null)
                return -1;
            return 0;
        }

        for (EntityField entityField : entityMetaData.getEntityFieldCollection())
        {
            Object srcValue = entityField.getValue(srcEntity);
            Object dstValue = entityField.getValue(dstEntity);
            int result = ValueUtils.compare(srcValue, dstValue);
            if (result != 0)
                return result;
        }
        return 0;
    }

    /**
     * 比较两个实体大小。
     * @param srcEntity 来源实体对象。
     * @param dstEntity 目标实体对象。
     * @return
     */
    protected static int compareByPrimaryKey(EntityMetaData entityMetaData, Object srcEntity, Object dstEntity)
    {
        if (srcEntity == null || dstEntity == null)
        {
            if (srcEntity != null)
                return 1;
            if (dstEntity != null)
                return -1;
            return 0;
        }

        for (EntityField entityField : entityMetaData.getPrimaryKeyFieldCollection())
        {
            Object srcValue = entityField.getValue(srcEntity);
            Object dstValue = entityField.getValue(dstEntity);
            int result = ValueUtils.compare(srcValue, dstValue);
            if (result != 0)
                return result;
        }
        return 0;
    }

    /**
     * 主键值是否有效。
     * @param primaryKeyValues 主键值。
     * @return
     */
    protected static boolean isPrimaryKeyValueValid(Object[] primaryKeyValues)
    {
        if (primaryKeyValues == null || primaryKeyValues.length == 0)
            return false;

        for (Object value : primaryKeyValues)
        {
            if (value == null)
                return false;
        }
        return true;
    }

    /**
     * 排序栏位顺序。
     */
    protected static Map<String, EntityField> sort(EntityMetaData entityMetaData)
    {
        boolean isDefaultIndex = true;
        for (EntityField entityField : entityMetaData.getEntityFieldCollection())
        {
            if (entityField.getColumn().getIndex() != -1)
            {
                isDefaultIndex = false;
                break;
            }
        }
        if (isDefaultIndex)
        {
            int index = 0;
            for (Column column : entityMetaData.getTable().getColumns().getPrimaryKeys())
                column.setIndex(index++);
        }
        entityMetaData.getTable().getColumns().sort();
        Map<String, EntityField> entityFieldMap = new LinkedHashMap<String, EntityField>();
        for (Column column : entityMetaData.getTable().getColumns())
        {
            EntityField entityField = entityMetaData.getEntityField(column.getName());
            entityFieldMap.put(entityField.getName(), entityField);
        }
        return entityFieldMap;
    }

    protected static String toString(EntityMetaData entityMetaData)
    {
        StringBuilder info = new StringBuilder();
        info.append("EntityClass = ");
        info.append(entityMetaData.getEntityClass());
        info.append(Environment.LINE_SEPARATOR);
        info.append("TableName = ");
        info.append(entityMetaData.getTableName());
        if (entityMetaData.getJoinTableCollection().size() > 0)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("JoinTableList : ");
            for (JoinTable joinTable : entityMetaData.getJoinTableCollection())
                info.append(joinTable);
        }
        info.append(Environment.LINE_SEPARATOR);
        info.append(entityMetaData.getTable().getSqls());
        if (entityMetaData.getRelations().size() > 0)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("RelationList : ");
            for (Relation relation : entityMetaData.getRelations())
                info.append(relation);
        }
        for (EntityField entityField : entityMetaData.getEntityFieldCollection())
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("EntityField : ");
            info.append(entityField);
        }
        for (EntityStatement entityStatement : entityMetaData.getEntityStatementMap().values())
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("EntityStatement : ");
            info.append(entityStatement);
        }
        info.append(Environment.LINE_SEPARATOR);
        info.append(entityMetaData.getTable());
        return info.toString();
    }
}
