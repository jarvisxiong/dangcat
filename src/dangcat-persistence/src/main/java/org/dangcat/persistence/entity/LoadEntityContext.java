package org.dangcat.persistence.entity;

import org.dangcat.persistence.exception.EntityException;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.model.Range;
import org.dangcat.persistence.orderby.OrderBy;

import java.util.Collection;

/**
 * 载入实体上下文。
 * @author dangcat
 * 
 */
public class LoadEntityContext extends DeleteEntityContext
{
    private Object entity = null;
    private Collection<?> entityCollection = null;
    private OrderBy orderBy = null;
    private Range range;

    /**
     * 按照过滤条件载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     */
    public LoadEntityContext(Class<?> entityClass)
    {
        this(entityClass, null, null, null);
    }

    /**
     * 按照过滤条件载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     */
    public LoadEntityContext(Class<?> entityClass, FilterExpress filterExpress)
    {
        this(entityClass, filterExpress, null, null);
    }

    /**
     * 按照过滤条件、范围载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     */
    public LoadEntityContext(Class<?> entityClass, FilterExpress filterExpress, Range range)
    {
        this(entityClass, filterExpress, range, null);
    }

    /**
     * 按照过滤条件、范围、排序条件载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param filterExpress 过滤条件。
     * @param range 载入范围。
     * @param orderBy 排序条件。
     */
    public LoadEntityContext(Class<?> entityClass, FilterExpress filterExpress, Range range, OrderBy orderBy)
    {
        super(entityClass, filterExpress);
        this.range = range;
        this.orderBy = orderBy;
    }

    /**
     * 按照主键值载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param primaryKeyValues 主键值。
     */
    public LoadEntityContext(Class<?> entityClass, Object... primaryKeyValues)
    {
        this(entityClass, null, primaryKeyValues);
    }

    /**
     * 按照范围载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param range 载入范围。
     */
    public LoadEntityContext(Class<?> entityClass, Range range)
    {
        this(entityClass, null, range, null);
    }

    /**
     * 按照范围、排序条件载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param range 载入范围。
     * @param orderBy 排序条件。
     */
    public LoadEntityContext(Class<?> entityClass, Range range, OrderBy orderBy)
    {
        this(entityClass, null, range, null);
    }

    /**
     * 按照主键值载入指定类型的实体。
     * @param entityManager 实体管理器。
     * @param entityClass 实体类型。
     * @param fieldNames 属性名称。
     * @param values 属性值。
     */
    public LoadEntityContext(Class<?> entityClass, String[] fieldNames, Object... values)
    {
        super(entityClass, fieldNames, values);
    }

    /**
     * 刷新指定的实体。
     * @param entity 实体对象。
     * @param entityClass 实体类型。
     */
    public LoadEntityContext(Object entity)
    {
        this(entity.getClass(), null, null, null);
        this.entity = entity;
    }

    protected void afterLoad()
    {
        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().afterLoad(this);
    }

    protected boolean beforeLoad()
    {
        if (this.getEntityEventAdapter() != null)
            return this.getEntityEventAdapter().beforeLoad(this);
        return true;
    }

    public Object getEntity()
    {
        return this.entity;
    }

    public Collection<?> getEntityCollection()
    {
        return this.entityCollection;
    }

    protected void setEntityCollection(Collection<?> entityCollection) {
        this.entityCollection = entityCollection;
    }

    public OrderBy getOrderBy()
    {
        return this.orderBy;
    }

    public Range getRange()
    {
        return this.range;
    }

    @Override
    public void initialize()
    {
        super.initialize();
        if (this.getFilterExpress() == null && this.entity != null)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(this.getEntityClass());
            String[] primaryKeyNames = entityMetaData.getPrimaryKeyNames();
            Object[] primaryKeyValues = entityMetaData.getPrimaryKeyValues(this.entity);
            this.setFilterExpress(entityMetaData.createFilterExpress(primaryKeyNames, primaryKeyValues));
        }
    }

    protected void onLoadError(EntityException entityException)
    {
        if (this.getEntityEventAdapter() != null)
            this.getEntityEventAdapter().onLoadError(this, entityException);
    }
}