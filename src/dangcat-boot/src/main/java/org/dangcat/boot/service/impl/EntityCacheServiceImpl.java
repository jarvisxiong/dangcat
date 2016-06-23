package org.dangcat.boot.service.impl;

import org.dangcat.boot.config.EntityCacheConfig;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.boot.event.TableEvent;
import org.dangcat.boot.event.service.EventSendService;
import org.dangcat.commons.timer.CronAlarmClock;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.impl.ServiceControlBase;
import org.dangcat.persistence.cache.EntityCacheManager;
import org.dangcat.persistence.cache.EntityUpdateNotifier;
import org.dangcat.persistence.model.DataState;

import java.util.Collection;

/**
 * 缓存管理服务。
 * @author dangcat
 * 
 */
public class EntityCacheServiceImpl extends ServiceControlBase implements Runnable, EntityUpdateNotifier
{
    /**
     * 所属父服务。
     * @param parent
     */
    public EntityCacheServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    private void createAlarmClock()
    {
        CronAlarmClock cronAlarmClock = new CronAlarmClock(this)
        {
            @Override
            public String getCronExpression()
            {
                return EntityCacheConfig.getInstance().getCronExpression();
            }

            @Override
            public boolean isEnabled()
            {
                return !Environment.isTestEnabled() && EntityCacheConfig.getInstance().isEnabled();
            }
        };
        // 注册定时器。
        if (cronAlarmClock.isValidExpression())
            TimerServiceImpl.getInstance().createTimer(cronAlarmClock);
    }

    @Override
    public Object handle(Event event)
    {
        // 其它系统更新表时通知系统联动。
        if (event instanceof TableEvent)
        {
            TableEvent tableEvent = (TableEvent) event;
            if (DataState.Insert.equals(tableEvent.getDataState()) || DataState.Modified.equals(tableEvent.getDataState()))
                this.update(tableEvent);
            else if (DataState.Deleted.equals(tableEvent.getDataState()))
                this.remove(tableEvent);
        }
        return super.handle(event);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        EntityCacheConfig entityCacheConfig = EntityCacheConfig.getInstance();
        entityCacheConfig.addConfigChangeEventAdaptor(new ChangeEventAdaptor()
        {
            @Override
            public void afterChanged(Object sender, Event event)
            {
                if (EntityCacheConfig.CronExpression.equals(event.getId()))
                    EntityCacheServiceImpl.this.createAlarmClock();
            }
        });
        this.createAlarmClock();

        // 加载缓存配置
        EntityCacheManager.getInstance().load(this.getClass(), entityCacheConfig.getConfigFile());
        if (!ValueUtils.isEmpty(entityCacheConfig.getMessageName()))
            EntityCacheManager.getInstance().addEntityUpdateNotifier(this);
    }

    private void notifyDeleted(String tableName, Collection<Object> deletedPrimaryKeys)
    {
        TableEvent tableEvent = new TableEvent(tableName, DataState.Deleted);
        tableEvent.addPrimaryKey(deletedPrimaryKeys.toArray());
        EventSendService eventSendService = this.getService(EventSendService.class);
        eventSendService.send(EntityCacheConfig.getInstance().getMessageName(), tableEvent);
        if (this.logger.isDebugEnabled())
            this.logger.debug("Send table deleted event : " + tableEvent);
    }

    private void notifyUpdate(String tableName, Collection<Object> updateEntities)
    {
        TableEvent tableEvent = new TableEvent(tableName, DataState.Modified);
        tableEvent.addValues(updateEntities.toArray());
        EventSendService eventSendService = this.getService(EventSendService.class);
        eventSendService.send(EntityCacheConfig.getInstance().getMessageName(), tableEvent);
        if (this.logger.isDebugEnabled())
            this.logger.debug("Send table update event : " + tableEvent);
    }

    @Override
    public void notifyUpdate(String tableName, Collection<Object> deletedPrimaryKeys, Collection<Object> updateEntities)
    {
        EventSendService eventSendService = this.getService(EventSendService.class);
        if (eventSendService != null)
        {
            if (deletedPrimaryKeys != null && !deletedPrimaryKeys.isEmpty())
                this.notifyDeleted(tableName, deletedPrimaryKeys);
            if (updateEntities != null && !updateEntities.isEmpty())
                this.notifyUpdate(tableName, updateEntities);
        }
    }

    private void remove(TableEvent tableEvent)
    {
        String tableName = tableEvent.getTableName();
        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        // 通过主键刷新缓存
        if (tableEvent.getPrimaryKeys() != null)
        {
            for (Object value : tableEvent.getPrimaryKeys())
            {
                if (value.getClass().isArray())
                    entityCacheManager.remove(tableName, (Object[]) value);
                else
                    entityCacheManager.remove(tableName, value);
            }
            if (this.logger.isDebugEnabled())
                this.logger.debug("The table " + tableName + " cache remove range : " + tableEvent.getPrimaryKeys());
        }
        // 通过实体刷新缓存
        if (tableEvent.getValues() != null)
        {
            Object[] entities = tableEvent.getValues().toArray();
            if (entities.length > 0)
            {
                entityCacheManager.removeEntities(tableName, entities);
                if (this.logger.isDebugEnabled())
                    this.logger.debug("The table " + tableName + " cache update range : " + entities);
            }
        }
        // 通过过滤条件刷新缓存
        if (tableEvent.getFilterExpress() != null)
        {
            entityCacheManager.remove(tableName, tableEvent.getFilterExpress());
            if (this.logger.isDebugEnabled())
                this.logger.debug("The table " + tableName + " cache remove range : " + tableEvent.getFilterExpress());
        }
    }

    /**
     * 定时清理过期的缓存数据。
     */
    @Override
    public void run()
    {
        EntityCacheManager.getInstance().clear(false);
    }

    private void update(TableEvent tableEvent)
    {
        String tableName = tableEvent.getTableName();
        EntityCacheManager entityCacheManager = EntityCacheManager.getInstance();
        // 通过主键刷新缓存
        if (tableEvent.getPrimaryKeys() != null)
        {
            for (Object value : tableEvent.getPrimaryKeys())
            {
                if (value.getClass().isArray())
                    entityCacheManager.update(tableName, (Object[]) value);
                else
                    entityCacheManager.update(tableName, value);
            }
            if (this.logger.isDebugEnabled())
                this.logger.debug("The table " + tableName + " cache update range : " + tableEvent.getPrimaryKeys());
        }
        // 通过实体刷新缓存
        if (tableEvent.getValues() != null)
        {
            Object[] entities = tableEvent.getValues().toArray();
            if (entities.length > 0)
            {
                entityCacheManager.modifyEntities(tableName, entities);
                if (this.logger.isDebugEnabled())
                    this.logger.debug("The table " + tableName + " cache update range : " + entities);
            }
        }
        // 通过过滤条件刷新缓存
        if (tableEvent.getFilterExpress() != null)
        {
            entityCacheManager.update(tableName, tableEvent.getFilterExpress());
            if (this.logger.isDebugEnabled())
                this.logger.debug("The table " + tableName + " cache update range : " + tableEvent.getFilterExpress());
        }
    }
}
