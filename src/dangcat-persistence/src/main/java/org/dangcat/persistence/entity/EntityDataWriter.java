package org.dangcat.persistence.entity;

import org.dangcat.persistence.DataWriter;

import java.util.List;

/**
 * Entity对象数据读取器。
 * @author dangcat
 * 
 */
public class EntityDataWriter<T> extends EntityDataAccess<T> implements DataWriter
{
    public EntityDataWriter(List<T> entityList, Class<T> classType)
    {
        super(entityList);
        this.setClassType(classType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(int index, String fieldName, Object value)
    {
        T entity = null;
        EntityMetaData entityMetaData = this.getEntityMetaData();
        if (index < this.size())
            entity = this.getEntityList().get(index);
        else
        {
            try
            {
                entity = (T) entityMetaData.getEntityClass().newInstance();
                this.getEntityList().add(entity);
            }
            catch (InstantiationException e)
            {
            }
            catch (IllegalAccessException e)
            {
            }
        }
        entityMetaData.setValue(fieldName, entity, value);
    }
}
