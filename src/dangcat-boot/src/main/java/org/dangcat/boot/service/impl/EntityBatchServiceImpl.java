package org.dangcat.boot.service.impl;

import org.dangcat.boot.config.EntityBatchConfig;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.boot.service.EntityBatchService;
import org.dangcat.commons.timer.AlarmClock;
import org.dangcat.commons.timer.CronAlarmClock;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.impl.ServiceControlBase;
import org.dangcat.persistence.batch.EntityBatchStorer;
import org.dangcat.persistence.orm.SessionFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据批量操作服务。
 * @author dangcat
 * 
 */
public class EntityBatchServiceImpl extends ServiceControlBase implements EntityBatchService, Runnable
{
    private static EntityBatchServiceImpl instance = null;
    private String defaultName = null;
    private Map<String, EntityBatchStorer> entityBatchStorerMap = new HashMap<String, EntityBatchStorer>();
    private long lastStoreTime = DateUtils.currentTimeMillis();

    /**
     * 所属父服务。
     *
     * @param parent
     */
    private EntityBatchServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    public static synchronized EntityBatchService createInstance(ServiceProvider parent)
    {
        if (instance == null)
        {
            instance = new EntityBatchServiceImpl(parent);
            instance.initialize();
        }
        return instance;
    }

    public static EntityBatchService getInstance()
    {
        return instance;
    }

    /**
     * 清除所有待处理数据。
     */
    public void clear()
    {
        Map<String, EntityBatchStorer> entityBatchStorerMap = this.getEntityBatchStorerMap();
        synchronized (entityBatchStorerMap)
        {
            for (EntityBatchStorer entityBatchStorer : entityBatchStorerMap.values())
                entityBatchStorer.clear();
        }
    }

    private void createAlarmClock()
    {
        CronAlarmClock cronAlarmClock = new CronAlarmClock(this)
        {
            @Override
            public String getCronExpression()
            {
                return EntityBatchConfig.getInstance().getCronExpression();
            }

            @Override
            public int getPriority()
            {
                return AlarmClock.LOW_PRIORITY;
            }

            @Override
            public boolean isEnabled()
            {
                return isClockEnabled();
            }
        };

        // 注册定时器。
        if (cronAlarmClock.isValidExpression())
            TimerServiceImpl.getInstance().createTimer(cronAlarmClock);
    }

    /**
     * 得到指定数据库的批量存储。
     * @return 批量操作对象。
     */
    @Override
    public EntityBatchStorer getEntityBatchStorer()
    {
        return getEntityBatchStorer(null);
    }

    /**
     * 得到指定数据库的批量存储。
     * @param databaseName 数据库名。
     * @return 批量操作对象。
     */
    @Override
    public EntityBatchStorer getEntityBatchStorer(String databaseName)
    {
        return this.getEntityBatchStorerMap().get(databaseName == null ? this.defaultName : databaseName);
    }

    private Map<String, EntityBatchStorer> getEntityBatchStorerMap()
    {
        if (this.entityBatchStorerMap.size() == 0)
        {
            this.defaultName = SessionFactory.getInstance().getDefaultName();
            String[] resourceNames = SessionFactory.getInstance().getResourceNames();
            if (resourceNames != null && resourceNames.length > 0)
            {
                Map<String, EntityBatchStorer> entityBatchStorerMap = new HashMap<String, EntityBatchStorer>();
                for (String resourceName : resourceNames)
                    entityBatchStorerMap.put(resourceName, new EntityBatchStorer(resourceName));
                this.entityBatchStorerMap = entityBatchStorerMap;
            }
        }
        return this.entityBatchStorerMap;
    }

    private int getTotalSize()
    {
        int totalSize = 0;
        for (EntityBatchStorer entityBatchStorer : this.getEntityBatchStorerMap().values())
            totalSize += entityBatchStorer.size();
        return totalSize;
    }

    @Override
    public void initialize()
    {
        super.initialize();

        EntityBatchConfig.getInstance().addConfigChangeEventAdaptor(new ChangeEventAdaptor()
        {
            @Override
            public void afterChanged(Object sender, Event event)
            {
                if (EntityBatchConfig.CronExpression.equals(event.getId()))
                    createAlarmClock();
            }
        });
        this.createAlarmClock();
    }

    public boolean isClockEnabled()
    {
        return EntityBatchConfig.getInstance().isEnabled() && this.getEntityBatchStorerMap().size() > 0;
    }

    private boolean isTimeOut(int totalSize)
    {
        if (totalSize > 0)
        {
            if (totalSize > EntityBatchConfig.getInstance().getBatchSize())
                return true;

            int interval = (int) ((DateUtils.currentTimeMillis() - this.lastStoreTime) / 1000);
            if (interval > EntityBatchConfig.getInstance().getMaxInterval())
                return true;
        }
        return false;
    }

    /**
     * 定时清理过期的缓存数据。
     */
    @Override
    public void run()
    {
        this.save();
        if (Environment.isTestEnabled())
            this.save();
    }

    /**
     * 调用批量存储操作。
     */
    public void save()
    {
        int totalSize = this.getTotalSize();
        if (!this.isTimeOut(totalSize))
            return;

        try
        {
            if (logger.isDebugEnabled())
                logger.debug("The entity batch store start: totalSize = " + totalSize + ". ");

            this.lastStoreTime = DateUtils.currentTimeMillis();
            for (EntityBatchStorer entityBatchStorer : this.getEntityBatchStorerMap().values())
            {
                if (entityBatchStorer.size() > 0)
                    entityBatchStorer.save();
            }
        }
        finally
        {
            if (totalSize > 0)
            {
                long costTime = DateUtils.currentTimeMillis() - this.lastStoreTime;
                int second = (int) (costTime / 1000);
                long velocity = 0;
                if (second > 0)
                    velocity = totalSize / second;
                if (logger.isDebugEnabled())
                    logger.debug("The entity batch store finished: totalSize = " + totalSize + ", costTime = " + costTime + "(ms), velocity = " + velocity + "/S. ");
            }
        }
    }
}
