package org.dangcat.persistence.cache;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.ResourceLoader;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.persistence.cache.xml.Cache;
import org.dangcat.persistence.cache.xml.CachesXmlResolver;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityMetaData;
import org.dangcat.persistence.entity.EntityPending;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.inject.CacheInjectProvider;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;

/**
 * 内存数据缓存管理。
 * @author dangcat
 * 
 */
public class EntityCacheManager
{
    protected static final Logger logger = Logger.getLogger(EntityCacheManager.class);
    private static EntityCacheManager instance = new EntityCacheManager();

    static
    {
        ServiceHelper.addInjectProvider(new CacheInjectProvider());
    }

    private Map<Class<?>, EntityCacheImpl<?>> entityCacheMap = Collections.synchronizedMap(new LinkedHashMap<Class<?>, EntityCacheImpl<?>>());
    private EntityCacheNotifier entityCacheNotifier = new EntityCacheNotifier();
    private EntityCacheManager()
    {
    }

    public static EntityCacheManager getInstance() {
        return instance;
    }

    public <T> EntityCacheImpl<T> addCache(Class<T> classType)
    {
        Cache cache = new Cache();
        cache.setClassType(classType.getName());
        return this.addCache(classType, cache);
    }

    public <T> EntityCacheImpl<T> addCache(Class<T> classType, Cache cache)
    {
        EntityCacheImpl<T> entityCache = this.createEntityCache(classType, cache);
        if (entityCache != null)
            this.entityCacheMap.put(classType, entityCache);
        return entityCache;
    }

    public void addEntityUpdateNotifier(EntityUpdateNotifier entityUpdateNotifier)
    {
        if (entityUpdateNotifier != null)
            this.entityCacheNotifier.addNotifier(entityUpdateNotifier);
    }

    public void clear(boolean force)
    {
        for (EntityCacheImpl<?> entityCache : this.entityCacheMap.values())
            entityCache.clear(force);
    }

    @SuppressWarnings("unchecked")
    private <T> EntityCacheImpl<T> createEntityCache(Class<T> classType, Cache cache)
    {
        // 产生主键索引。
        EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classType);
        if (entityMetaData == null)
            throw new RuntimeException(classType + " is not a entity type.");

        EntityCacheImpl<T> entityCacheImpl = (EntityCacheImpl<T>) this.entityCacheMap.get(classType);
        if (entityCacheImpl == null)
            entityCacheImpl = new EntityCacheImpl<T>(classType, entityMetaData.getTableName().getName());

        String[] primaryKeyNames = entityMetaData.getPrimaryKeyNames();
        if (primaryKeyNames != null && primaryKeyNames.length > 0)
            entityCacheImpl.appendIndex(ValueUtils.join(primaryKeyNames), true);

        // 产生附加索引。
        ReflectUtils.copyProperties(cache, entityCacheImpl);
        for (String indexName : cache.getIndexList())
            entityCacheImpl.appendIndex(indexName, false);
        return entityCacheImpl;
    }

    public void delete(Class<?> entityClass, FilterExpress filterExpress)
    {
        MemCache<?> memCache = this.getMemCache(entityClass);
        if (memCache != null)
            memCache.remove(filterExpress);
    }

    @SuppressWarnings("unchecked")
    public <T> EntityCache<T> getEntityCache(Class<T> entityClassType)
    {
        return (EntityCache<T>) this.entityCacheMap.get(entityClassType);
    }

    private Collection<EntityCacheImpl<?>> getEntityCaches(String tableName)
    {
        Collection<EntityCacheImpl<?>> entityCaches = null;
        for (EntityCache<?> entityCache : this.entityCacheMap.values())
        {
            EntityCacheImpl<?> entityCacheImpl = (EntityCacheImpl<?>) entityCache;
            if (entityCacheImpl.getTableName().equalsIgnoreCase(tableName))
            {
                if (entityCaches == null)
                    entityCaches = new ArrayList<EntityCacheImpl<?>>();
                entityCaches.add(entityCacheImpl);
            }
        }
        return entityCaches;
    }

    public <T> MemCache<T> getMemCache(Class<T> classType)
    {
        return this.getEntityCache(classType);
    }

    /**
     * 由资源文件载入缓存配置。
     */
    public void load(Class<?> classType, String cacheFile)
    {
        if (classType == null || ValueUtils.isEmpty(cacheFile))
        {
            logger.warn("Load config is empty by " + classType);
            return;
        }

        InputStream inputStream = ResourceLoader.load(classType, cacheFile);
        if (inputStream != null)
        {
            try
            {
                this.load(inputStream);
            }
            catch (DocumentException e)
            {
            }
            finally
            {
                FileUtils.close(inputStream);
            }
        }
    }

    /**
     * 由配置文件载入缓存配置。
     * @param cacheFile 配置文件。
     */
    public void load(File cacheFile)
    {
        if (cacheFile == null || !cacheFile.exists() || !cacheFile.isFile())
        {
            logger.error("Load cache file is not valid : " + cacheFile);
            return;
        }

        InputStream inputStream = null;
        try
        {
            inputStream = new FileInputStream(cacheFile);
            this.load(inputStream);
        }
        catch (Exception e)
        {
            logger.error("Load cache file error : " + cacheFile, e);
        }
        finally
        {
            inputStream = FileUtils.close(inputStream);
        }
    }

    /**
     * 由配置文件载入缓存配置。
     * @param cacheFile 配置文件。
     * @throws DocumentException
     */
    private void load(InputStream inputStream) throws DocumentException
    {
        CachesXmlResolver cachesXmlResolver = new CachesXmlResolver();
        cachesXmlResolver.setResolveObject(new ArrayList<Cache>());
        cachesXmlResolver.open(inputStream);
        cachesXmlResolver.resolve();
        for (Cache cache : cachesXmlResolver.getCacheList())
        {
            Class<?> classType = ReflectUtils.loadClass(cache.getClassType());
            if (classType == null)
            {
                logger.error("The cache define " + cache.getClassType() + " is not found.");
                continue;
            }
            this.addCache(classType, cache);
        }
        this.loadData();
    }

    public void loadData()
    {
        for (EntityCache<?> entityCache : this.entityCacheMap.values())
        {
            EntityCacheImpl<?> entityCacheImpl = (EntityCacheImpl<?>) entityCache;
            if (entityCacheImpl.isPreload())
                entityCacheImpl.loadData();
        }
    }

    public void modifyEntities(String tableName, Object... entities)
    {
        if (entities != null && entities.length > 0)
        {
            Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
            if (entityCaches != null)
            {
                for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
                    entityCacheImpl.modifyEntities(entities);
            }
        }
    }

    /**
     * 当表发生变化时，刷新内存中的数据。
     * @param tableName 表名。
     * @param filterExpress 过滤条件。
     */
    public void remove(String tableName, FilterExpress filterExpress)
    {
        Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
        if (entityCaches != null)
        {
            for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
                entityCacheImpl.remove(filterExpress);
        }
    }

    /**
     * 当表发生变化时，刷新内存中的数据。
     * @param tableName 表名。
     * @param primaryKeys 记录索引。
     */
    public void remove(String tableName, Object... primaryKeys)
    {
        Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
        if (entityCaches != null)
        {
            for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
            {
                if (primaryKeys != null && primaryKeys.length > 0)
                    entityCacheImpl.removeEntity(primaryKeys);
                else
                    entityCacheImpl.clear(true);
            }
        }
    }

    public void removeCache(Class<?> classType)
    {
        this.entityCacheMap.remove(classType);
    }

    public int removeEntities(String tableName, Object... entities)
    {
        int count = 0;
        if (entities != null && entities.length > 0)
        {
            Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
            if (entityCaches != null)
            {
                for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
                    count += entityCacheImpl.removeEntities(entities);
            }
        }
        return count;
    }

    public void removeEntityUpdateNotifier(EntityUpdateNotifier entityUpdateNotifier)
    {
        if (entityUpdateNotifier != null)
            this.entityCacheNotifier.removeNotifier(entityUpdateNotifier);
    }

    public int size()
    {
        return this.entityCacheMap.size();
    }

    public void truncate()
    {
        this.clear(true);
        Class<?>[] classTypes = this.entityCacheMap.keySet().toArray(new Class<?>[0]);
        for (int i = classTypes.length - 1; i >= 0; i--)
        {
            EntityMetaData entityMetaData = EntityHelper.getEntityMetaData(classTypes[i]);
            if (entityMetaData != null)
            {
                if (entityMetaData.getTable().exists())
                    entityMetaData.getTable().truncate();
            }
        }
    }

    public void update(EntityPending entityPending)
    {
        Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(entityPending.getTableName());
        if (entityCaches != null)
            this.entityCacheNotifier.update(entityPending, entityCaches);
    }

    /**
     * 当表发生变化时，刷新内存中的数据。
     * @param tableName 表名。
     * @param filterExpress 过滤条件。
     */
    public void update(String tableName, FilterExpress filterExpress)
    {
        Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
        if (entityCaches != null)
        {
            for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
            {
                entityCacheImpl.remove(filterExpress);
                entityCacheImpl.load(filterExpress);
            }
        }
    }

    /**
     * 当表发生变化时，刷新内存中的数据。
     * @param tableName 表名。
     * @param primaryKeys 记录索引。
     */
    public void update(String tableName, Object... primaryKeys)
    {
        Collection<EntityCacheImpl<?>> entityCaches = this.getEntityCaches(tableName);
        if (entityCaches != null)
        {
            for (EntityCacheImpl<?> entityCacheImpl : entityCaches)
            {
                if (primaryKeys != null && primaryKeys.length > 0)
                {
                    entityCacheImpl.removeEntity(primaryKeys);
                    entityCacheImpl.load(primaryKeys);
                }
            }
        }
    }
}