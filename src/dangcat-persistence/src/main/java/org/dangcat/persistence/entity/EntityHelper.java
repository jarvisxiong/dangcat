package org.dangcat.persistence.entity;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.calculate.CalculatorImpl;
import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.model.Column;
import org.dangcat.persistence.orderby.OrderBy;
import org.dangcat.persistence.sql.Sql;
import org.dangcat.persistence.sql.Sqls;
import org.dangcat.persistence.tablename.TableName;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体帮助工具。
 * @author dangcat
 * 
 */
public class EntityHelper
{
    private static Map<Class<?>, EntityMetaData> entityMetaDataMap = new HashMap<Class<?>, EntityMetaData>();

    private static void createCalculators(EntityMetaData entityMetaData, Class<?> classType)
    {
        org.dangcat.persistence.annotation.Calculators calculatorsAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Calculators.class);
        CalculatorImpl calculator = entityMetaData.getTable().getCalculators();
        if (calculatorsAnnotation != null)
        {
            for (org.dangcat.persistence.annotation.Calculator calculatorAnnotation : calculatorsAnnotation.value())
                calculator.add(calculatorAnnotation.value());
        }
        else
        {
            org.dangcat.persistence.annotation.Calculator calculatorAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Calculator.class);
            if (calculatorAnnotation != null)
                calculator.add(calculatorAnnotation.value());
        }
        calculator.initialize();
    }

    /**
     * 产生实体事件。
     * @param entityMetaData 实体元数据。
     */
    protected static void createEntityEvent(EntityMetaData entityMetaData)
    {
        for (Method method : entityMetaData.getEntityClass().getDeclaredMethods())
        {
            if (method.getAnnotation(org.dangcat.persistence.annotation.BeforeDelete.class) != null)
                entityMetaData.getBeforeDeleteCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.AfterDelete.class) != null)
                entityMetaData.getAfterDeleteCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.BeforeSave.class) != null)
                entityMetaData.getBeforeSaveCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.BeforeInsert.class) != null)
                entityMetaData.getBeforeInsertCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.AfterSave.class) != null)
                entityMetaData.getAfterSaveCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.AfterCommit.class) != null)
                entityMetaData.getAfterCommitCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.AfterInsert.class) != null)
                entityMetaData.getAfterInsertCollection().add(method);
            if (method.getAnnotation(org.dangcat.persistence.annotation.AfterLoad.class) != null)
                entityMetaData.getAfterLoadCollection().add(method);
        }
    }

    private static void createEntityField(EntityMetaData entityMetaData, Class<?> classType)
    {
        for (Field field : classType.getDeclaredFields())
        {
            org.dangcat.persistence.annotation.Column columnAnnotation = field.getAnnotation(org.dangcat.persistence.annotation.Column.class);
            org.dangcat.persistence.annotation.Relation relationAnnotation = field.getAnnotation(org.dangcat.persistence.annotation.Relation.class);
            if (columnAnnotation != null || relationAnnotation != null)
            {
                EntityField entityField = entityMetaData.getEntityField(field.getName());
                if (entityField == null)
                    entityField = createEntityField(entityMetaData, columnAnnotation, field.getName(), field.getType());
                entityField.setField(field);
                if (columnAnnotation != null)
                    entityMetaData.addEntityField(entityField);
                else
                    entityMetaData.addRelation(new Relation(entityField, relationAnnotation));
            }
        }
        List<PropertyDescriptor> propertyDescriptorList = BeanUtils.getPropertyDescriptorList(classType);
        if (propertyDescriptorList != null)
        {
            for (PropertyDescriptor propertyDescriptor : propertyDescriptorList)
            {
                Method readMethod = propertyDescriptor.getReadMethod();
                if (readMethod == null)
                    continue;
                org.dangcat.persistence.annotation.Column columnAnnotation = readMethod.getAnnotation(org.dangcat.persistence.annotation.Column.class);
                org.dangcat.persistence.annotation.Relation relationAnnotation = readMethod.getAnnotation(org.dangcat.persistence.annotation.Relation.class);
                if (columnAnnotation != null || relationAnnotation != null)
                {
                    EntityField entityField = entityMetaData.getEntityField(propertyDescriptor.getName());
                    if (entityField == null)
                        entityField = createEntityField(entityMetaData, columnAnnotation, propertyDescriptor.getName(), propertyDescriptor.getPropertyType());
                    entityField.setPropertyDescriptor(propertyDescriptor);
                    if (columnAnnotation != null)
                    {
                        entityMetaData.addEntityField(entityField);
                        entityField.getColumn().assign(columnAnnotation);
                    }
                    else
                        entityMetaData.addRelation(new Relation(entityField, relationAnnotation));
                }
            }
        }
    }

    private static EntityField createEntityField(EntityMetaData entityMetaData, org.dangcat.persistence.annotation.Column columnAnnotation, String name, Class<?> classType)
    {
        EntityField entityField = null;
        if (columnAnnotation != null)
        {
            Column column = entityMetaData.getTable().getColumns().add(ReflectUtils.toFieldName(name), classType);
            column.assign(columnAnnotation);
            entityField = new EntityField(column);
        }
        else
        {
            Column column = new Column();
            column.setName(ReflectUtils.toFieldName(name));
            entityField = new EntityField(column);
        }
        return entityField;
    }

    /**
     * 产生实体元数据信息。
     * @param entityMetaData 元数据对象。
     */
    public static void createEntityMetaData(EntityMetaData entityMetaData)
    {
        find(entityMetaData, entityMetaData.getEntityClass());
        new EntityFieldInfoCreator(entityMetaData).execute();
    }

    private static void createIndex(EntityMetaData entityMetaData, org.dangcat.persistence.annotation.Index indexAnnotation)
    {
        if (indexAnnotation != null && !ValueUtils.isEmpty(indexAnnotation.value()))
        {
            OrderBy index = OrderBy.parse(indexAnnotation.value());
            if (index != null)
                entityMetaData.getTable().getIndexes().add(index);
        }
    }

    private static void createIndexes(EntityMetaData entityMetaData, Class<?> classType)
    {
        org.dangcat.persistence.annotation.Indexes indexesAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Indexes.class);
        if (indexesAnnotation != null)
        {
            for (org.dangcat.persistence.annotation.Index indexAnnotation : indexesAnnotation.value())
                createIndex(entityMetaData, indexAnnotation);
        }
        else
        {
            org.dangcat.persistence.annotation.Index indexAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Index.class);
            if (indexAnnotation != null)
                createIndex(entityMetaData, indexAnnotation);
        }
    }

    protected static JoinTable createJoinTable(EntityMetaData entityMetaData, org.dangcat.persistence.annotation.JoinTable joinTableAnnotation)
    {
        TableName tableName = entityMetaData.getTableName();
        if (!ValueUtils.isEmpty(joinTableAnnotation.tableName()))
            tableName = new TableName(joinTableAnnotation.tableName(), joinTableAnnotation.tableAlias());

        String joinTableName = ValueUtils.isEmpty(joinTableAnnotation.joinTableName()) ? entityMetaData.getTableName().getName() : joinTableAnnotation.joinTableName();
        String joinTableAlias = ValueUtils.isEmpty(joinTableAnnotation.joinTableAlias()) ? entityMetaData.getTableName().getAlias() : joinTableAnnotation.joinTableAlias();
        JoinTable joinTable = new JoinTable(tableName, new TableName(joinTableName, joinTableAlias), joinTableAnnotation);
        for (org.dangcat.persistence.annotation.JoinColumn joinColumnAnnotation : joinTableAnnotation.joinColumns())
            joinTable.addJoinColumn(joinColumnAnnotation.name(), joinColumnAnnotation.joinName());

        if (!entityMetaData.getJoinTableCollection().contains(joinTable))
            entityMetaData.getJoinTableCollection().add(joinTable);
        return joinTable;
    }

    private static void createJoinTables(EntityMetaData entityMetaData, Class<?> classType)
    {
        org.dangcat.persistence.annotation.JoinTables joinTablesAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.JoinTables.class);
        if (joinTablesAnnotation != null)
        {
            for (org.dangcat.persistence.annotation.JoinTable joinTableAnnotation : joinTablesAnnotation.value())
                createJoinTable(entityMetaData, joinTableAnnotation);
        }
    }

    private static void createOrderBy(EntityMetaData entityMetaData, Class<?> classType)
    {
        org.dangcat.persistence.annotation.OrderBy orderBy = classType.getAnnotation(org.dangcat.persistence.annotation.OrderBy.class);
        if (orderBy != null)
            entityMetaData.getTable().setOrderBy(OrderBy.parse(orderBy.value()));
    }

    private static void createSqls(EntityMetaData entityMetaData, Class<?> classType)
    {
        Sqls sqls = entityMetaData.getTable().getSqls();
        sqls.setClassType(entityMetaData.getEntityClass());
        org.dangcat.persistence.annotation.Sql sqlAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Sql.class);
        if (sqlAnnotation != null)
            sqls.add(new Sql(sqlAnnotation));
        org.dangcat.persistence.annotation.Sqls sqlsAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Sqls.class);
        if (sqlsAnnotation != null)
        {
            for (org.dangcat.persistence.annotation.Sql qryAnnotation : sqlsAnnotation.value())
                sqls.add(new Sql(qryAnnotation));
        }

        // 读取XML配置的查询语句。
        org.dangcat.persistence.annotation.SqlXml sqlXmlAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.SqlXml.class);
        if (sqlXmlAnnotation != null)
            sqls.read(entityMetaData.getEntityClass(), sqlXmlAnnotation.value());
    }

    private static void createTableName(EntityMetaData entityMetaData, Class<?> classType)
    {
        org.dangcat.persistence.annotation.Table tableAnnotation = classType.getAnnotation(org.dangcat.persistence.annotation.Table.class);
        if (tableAnnotation != null)
        {
            String tableName = ValueUtils.isEmpty(tableAnnotation.value()) ? classType.getSimpleName() : tableAnnotation.value();
            if (!TableName.class.equals(tableAnnotation.tableName()))
                entityMetaData.setTableName((TableName) ReflectUtils.newInstance(tableAnnotation.tableName(), new Class<?>[] { String.class }, new Object[] { tableName }));
            else
                entityMetaData.setTableName(new TableName(tableName, tableAnnotation.alias()));
        }
    }

    /**
     * 寻找实体注释信息。
     * @param entityMetaData 元数据对象。
     * @param classType 类型。
     */
    private static void find(EntityMetaData entityMetaData, Class<?> classType)
    {
        if (Object.class.equals(classType))
            return;

        // 从父类开始寻找。
        find(entityMetaData, classType.getSuperclass());
        // 表名
        createTableName(entityMetaData, classType);
        // 索引
        createIndexes(entityMetaData, classType);
        // 排序方式。
        createOrderBy(entityMetaData, classType);
        // 连接表。
        createJoinTables(entityMetaData, classType);
        // 查询语句。
        createSqls(entityMetaData, classType);
        // 解析字段。
        createEntityField(entityMetaData, classType);
        // 计算器。
        createCalculators(entityMetaData, classType);
    }

    /**
     * 根据实体类型读取元数据。
     * @param classType 实体类型
     * @return 实体元数据。
     * @throws EntityException
     */
    public static EntityMetaData getEntityMetaData(Class<?> classType) throws EntityException
    {
        EntityMetaData entityMetaData = entityMetaDataMap.get(classType);
        if (entityMetaData == null && isEntityClass(classType))
        {
            entityMetaData = new EntityMetaData(classType);
            if (entityMetaData.getTable() == null || entityMetaData.getTable().getColumns().size() == 0)
                throw new EntityException("The " + classType + " is invalid entity type.");

            synchronized (entityMetaDataMap)
            {
                entityMetaDataMap.put(classType, entityMetaData);
                entityMetaData.initialize();
                ResourceManager.getInstance().addResourceReader(classType, entityMetaData.getResourceReader());
            }
        }
        return entityMetaData;
    }

    /**
     * 根据实体对象读取元数据。
     * @param entity 实体对象
     * @return 实体元数据。
     */
    public static EntityMetaData getEntityMetaData(Object entity)
    {
        EntityMetaData entityMetaData = null;
        if (entity != null)
        {
            try
            {
                entityMetaData = getEntityMetaData(entity.getClass());
            }
            catch (EntityException e)
            {
            }
        }
        return entityMetaData;
    }

    public static boolean isEntityClass(Class<?> classType)
    {
        if (classType != null && !classType.isInterface() && !Object.class.equals(classType))
        {
            if (classType.getAnnotation(org.dangcat.persistence.annotation.Table.class) != null)
                return true;
            else
                return isEntityClass(classType.getSuperclass());

        }
        return false;
    }
}
