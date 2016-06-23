package org.dangcat.persistence.entity;

import org.dangcat.commons.reflect.BeanUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.persistence.model.DataState;
import org.dangcat.persistence.model.DataStatus;
import org.dangcat.persistence.model.Table;
import org.dangcat.persistence.model.TableUtils;
import org.dangcat.persistence.orm.Session;
import org.dangcat.persistence.tablename.DynamicTable;
import org.dangcat.persistence.tablename.TableName;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class EntityUtils {
    public static DataState checkDataState(Object entity) {
        DataState dataState = null;
        if (entity != null) {
            if (entity instanceof DataStatus)
                dataState = ((DataStatus) entity).getDataState();
            else {
                // 自增主键字段判断主键是否存在。
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
                EntityField entityField = entityMetaData.getAutoIncrement();
                if (entityField != null) {
                    if (entityField.getValue(entity) == null)
                        dataState = DataState.Insert;
                    else
                        dataState = DataState.Modified;
                }
            }
            if (dataState == null)
                dataState = DataState.Insert;
        }
        return dataState;
    }

    public static boolean exists(Class<?> classType) {
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData != null)
            return exists(entityMetaData, null);
        return false;
    }

    public static boolean exists(EntityMetaData entityMetaData, EntityContext entityContext) {
        TableName tableName = entityMetaData.getTableName();
        if (entityContext != null && entityContext.getTableName() != null)
            tableName = entityContext.getTableName();
        Table table = null;
        if (tableName instanceof DynamicTable) {
            table = (Table) entityMetaData.getTable().clone();
            table.setTableName(tableName);
        } else
            table = entityMetaData.getTable();
        if (entityContext != null) {
            Session session = ((EntityManagerImpl) entityContext.getEntityManager()).getSession();
            if (session != null)
                return TableUtils.exists(table, session);
        }
        return table.exists();
    }

    public static <T> Collection<T> find(Collection<T> dataCollection, String[] fieldNames, Object... values) {
        if (dataCollection == null || dataCollection.isEmpty())
            return null;
        if (values == null || values.length == 0)
            return null;

        EntityMetaData entityMetaData = null;
        Collection<T> found = null;
        Object[] primaryKeyValues = new Object[fieldNames.length];
        for (T entity : dataCollection) {
            if (entityMetaData == null)
                entityMetaData = EntityHelper.getEntityMetaData(entity);
            if (entityMetaData == null)
                return null;
            for (int i = 0; i < fieldNames.length; i++)
                primaryKeyValues[i] = entityMetaData.getValue(fieldNames[i], entity);
            entityMetaData.getPrimaryKeyValues(entity);
            if (ValueUtils.compare(values, primaryKeyValues) == 0) {
                if (found == null)
                    found = new LinkedHashSet<T>();
                found.add(entity);
                break;
            }
        }
        return found;
    }

    public static <T> Collection<T> findRepeat(Collection<T> dataCollection, String... fieldNames) {
        if (dataCollection == null || dataCollection.isEmpty())
            return null;
        if (fieldNames == null || fieldNames.length == 0)
            return null;

        EntityMetaData entityMetaData = null;
        Collection<T> found = null;
        Collection<String> keys = new HashSet<String>();
        for (T entity : dataCollection) {
            if (entityMetaData == null)
                entityMetaData = EntityHelper.getEntityMetaData(entity);
            if (entityMetaData == null)
                return null;
            StringBuilder info = new StringBuilder();
            for (String fieldName : fieldNames) {
                info.append("<");
                info.append(entityMetaData.getValue(fieldName, entity));
                info.append(">");
            }
            String key = info.toString();
            if (keys.contains(key)) {
                if (found == null)
                    found = new LinkedHashSet<T>();
                found.add(entity);
            } else
                keys.add(key);
        }
        return found;
    }

    public static Collection<Relation> getSerializeRelations(Class<?> classType) {
        Collection<Relation> relationCollection = null;
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData != null && entityMetaData.getRelations() != null && entityMetaData.getRelations().size() > 0) {
            for (Relation relation : entityMetaData.getRelations()) {
                if (isSerializeIgnore(entityMetaData.getEntityClass(), relation.getName()))
                    continue;
                if (relationCollection == null)
                    relationCollection = new ArrayList<Relation>();
                relationCollection.add(relation);
            }
        }
        return relationCollection;
    }

    public static String getTitle(Class<?> classType, String fieldName) {
        String title = null;
        if (classType != null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
            if (entityMetaData != null)
                title = entityMetaData.getResourceReader().getText(fieldName);
        }
        return title;
    }

    public static boolean isSerializeIgnore(Class<?> classType, String name) {
        org.dangcat.commons.serialize.annotation.Serialize serializeAnnotation = BeanUtils.getAnnotation(classType, name, org.dangcat.commons.serialize.annotation.Serialize.class);
        return serializeAnnotation != null && serializeAnnotation.ignore();
    }

    public static <T extends EntityBase> T locate(Collection<T> dataCollection, Object... values) {
        if (dataCollection == null || dataCollection.isEmpty())
            return null;
        if (values == null || values.length == 0)
            return null;

        EntityMetaData entityMetaData = null;
        T found = null;
        for (T entity : dataCollection) {
            if (entityMetaData == null)
                entityMetaData = EntityHelper.getEntityMetaData(entity);
            if (entityMetaData == null)
                return null;
            Object[] primaryKeyValues = entityMetaData.getPrimaryKeyValues(entity);
            if (ValueUtils.compare(values, primaryKeyValues) == 0) {
                found = entity;
                break;
            }
        }
        return found;
    }

    /**
     * 置空自增字段数值。
     */
    public static void resetAutoIncrement(Collection<?> entityCollection) {
        try {
            for (Object entity : entityCollection) {
                EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
                EntityField entityField = entityMetaData.getAutoIncrement();
                entityField.setValue(entity, null);
            }
        } catch (Exception e) {
        }
    }

    public static void resetDataState(Object entity) {
        if (entity != null) {
            if (entity instanceof Collection<?>) {
                Collection<?> entityCollection = (Collection<?>) entity;
                for (Object member : entityCollection)
                    resetDataState(member);
                return;
            }
            if (entity instanceof DataStatus) {
                DataStatus DataStatus = (DataStatus) entity;
                DataStatus.setDataState(DataState.Browse);
            } else if (entity instanceof EntityBase) {
                EntityBase entityBase = (EntityBase) entity;
                entityBase.setDataState(DataState.Browse);
            }
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            if (entityMetaData != null) {
                Collection<Relation> relationCollection = entityMetaData.getRelations();
                if (relationCollection != null) {
                    for (Relation relation : relationCollection) {
                        Collection<Object> children = relation.getMembers(entity);
                        if (children != null) {
                            for (Object child : children)
                                resetDataState(child);
                        }
                    }
                }
            }
        }
    }

    public static void resetEntityBase(EntityBase entityBase) {
        if (entityBase != null) {
            entityBase.clearServiceException();
            entityBase.clearServiceInformation();
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entityBase);
            if (entityMetaData != null) {
                Collection<Relation> relationCollection = entityMetaData.getRelations();
                if (relationCollection != null) {
                    for (Relation relation : relationCollection) {
                        Collection<Object> children = relation.getMembers(entityBase);
                        if (children != null) {
                            for (Object child : children) {
                                if (child instanceof EntityBase)
                                    resetEntityBase((EntityBase) child);
                            }
                        }
                    }
                }
            }
        }
    }

    public static void sortRelation(Object entity) {
        if (entity != null) {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(entity.getClass());
            if (entityMetaData != null) {
                Collection<Relation> relationCollection = entityMetaData.getRelations();
                if (relationCollection != null) {
                    for (Relation relation : relationCollection) {
                        if (relation.getSortBy() != null) {
                            Collection<Object> children = relation.getMembers(entity);
                            if (children != null && children.size() > 0)
                                relation.getSortBy().sort(children);
                        }
                    }
                }
            }
        }
    }
}
