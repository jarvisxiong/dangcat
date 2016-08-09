package org.dangcat.persistence.cache;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.persistence.entity.EntityLoader;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.filter.FilterExpress;

import java.util.Collection;
import java.util.List;

/**
 * 实体缓存。
 *
 * @author dangcat
 */
public class EntityCacheImpl<T> extends MemCacheImpl<T> implements EntityCache<T> {
    private String databaseName = null;
    private EntityLoader<T> entityLoader = null;
    private boolean isPreload = false;
    private Class<?> loaderClass = null;

    public EntityCacheImpl(Class<T> classType, String tableName) {
        super(classType, tableName);
    }

    private void addCache(Collection<T> dataCollection) {
        if (dataCollection != null) {
            for (T data : dataCollection)
                this.add(data);
        }
    }

    /**
     * 删除指定条件的缓存数据和数据库数据。
     */
    @Override
    public void delete(FilterExpress filterExpress) {
        if (filterExpress != null)
            this.getEntityManager().delete(this.getClassType(), filterExpress);
    }

    /**
     * 删除缓存数据和数据库数据。
     */
    @Override
    public void delete(T data) {
        if (data != null)
            this.getEntityManager().delete(data);
    }

    public String getDatabaseName() {
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    @SuppressWarnings("unchecked")
    private EntityLoader<T> getEntityLoader() {
        if (this.loaderClass != null && this.entityLoader == null) {
            this.entityLoader = (EntityLoader<T>) ReflectUtils.newInstance(this.loaderClass);
            if (this.entityLoader != null)
                ServiceHelper.inject(ServiceFactory.getInstance(), this.entityLoader);
        }
        return this.entityLoader;
    }

    private EntityManager getEntityManager() {
        return EntityManagerFactory.getInstance().open(this.getDatabaseName());
    }

    public Class<?> getLoaderClass() {
        return this.loaderClass;
    }

    public void setLoaderClass(Class<?> loaderClass) {
        this.loaderClass = loaderClass;
    }

    public boolean isPreload() {
        return this.isPreload;
    }

    public void setPreload(boolean isPreload) {
        this.isPreload = isPreload;
    }

    /**
     * 按照指定的条件在数据库查找数据后添加到缓存。
     *
     * @param filterExpress 索引条件。
     * @return 数据集合。
     */
    @Override
    public Collection<T> load(FilterExpress filterExpress) {
        Collection<T> dataCollection = this.find(filterExpress);
        if (dataCollection == null) {
            EntityManager entityManager = this.getEntityManager();
            dataCollection = entityManager.load(this.getClassType(), filterExpress);
            if (dataCollection != null)
                this.addCache(dataCollection);
        }
        return dataCollection;
    }

    /**
     * 根据主键值找到记录行。
     *
     * @param params 主键参数值。
     * @return 找到的数据行。
     */
    @Override
    public T load(Object... params) {
        T data = this.locate(params);
        if (data == null) {
            EntityManager entityManager = this.getEntityManager();
            data = entityManager.load(this.getClassType(), params);
            if (data != null)
                this.add(data);
        }
        return data;
    }

    /**
     * 按照指定的字段值查找数据。
     *
     * @param fieldNames 字段名，多字段以分号间隔。
     * @param values     字段数值，必须与字段对应。
     * @return 找到的记录行。
     */
    @Override
    public Collection<T> load(String[] fieldNames, Object... values) {
        Collection<T> dataCollection = this.find(fieldNames, values);
        if (dataCollection == null || dataCollection.size() == 0) {
            EntityManager entityManager = this.getEntityManager();
            dataCollection = entityManager.load(this.getClassType(), fieldNames, values);
            if (dataCollection != null)
                this.addCache(dataCollection);
        }
        return dataCollection;
    }

    public void loadData() {
        EntityManager entityManager = this.getEntityManager();
        long beginTime = DateUtils.currentTimeMillis();
        List<T> dataList = null;
        EntityLoader<T> entityLoader = this.getEntityLoader();
        if (entityLoader != null)
            dataList = entityLoader.load(entityManager);
        else
            dataList = entityManager.load(this.getClassType());
        if (dataList != null) {
            this.clear(true);
            this.addCache(dataList);
            logger.info("The cache " + this.getClassType() + " preload cost time " + (DateUtils.currentTimeMillis() - beginTime) + " ms and size is " + dataList.size());
        }
    }

    /**
     * 刷新内存数据。
     *
     * @param data 目标数据。
     * @return 刷新后数据。
     */
    @Override
    public T refresh(T data) {
        T entity = null;
        if (data != null) {
            EntityManager entityManager = this.getEntityManager();
            entity = entityManager.refresh(data);
            if (entity == null)
                this.remove(data);
        }
        return entity;
    }

    /**
     * 刷新指定主键的内存数据。
     *
     * @param primaryKeys 目标主键数据。
     * @return 刷新后数据。
     */
    @Override
    public T refreshEntity(Object... primaryKeys) {
        T entity = null;
        if (primaryKeys != null && primaryKeys.length > 0) {
            EntityManager entityManager = this.getEntityManager();
            entity = entityManager.load(this.getClassType(), primaryKeys);
            T data = this.locate(primaryKeys);
            if (data != null)
                this.remove(data);
            if (entity != null)
                this.add(entity);
        }
        return entity;
    }

    /**
     * 删除指定条件的缓存数据。
     */
    @Override
    public Collection<T> remove(FilterExpress filterExpress) {
        Collection<T> dataCollection = null;
        if (filterExpress != null) {
            dataCollection = this.find(filterExpress);
            if (dataCollection != null) {
                for (T data : dataCollection)
                    this.remove(data);
            }
        }
        return dataCollection;
    }

    /**
     * 添加缓存数据。
     */
    @Override
    public void save(T data) {
        if (data != null)
            this.getEntityManager().save(data);
    }
}
