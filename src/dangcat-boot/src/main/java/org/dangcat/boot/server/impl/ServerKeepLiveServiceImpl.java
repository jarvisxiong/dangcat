package org.dangcat.boot.server.impl;

import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.boot.event.service.EventSendService;
import org.dangcat.boot.server.config.ServerConfig;
import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.boot.server.domain.ServerStatusLog;
import org.dangcat.boot.server.event.ServerEvent;
import org.dangcat.boot.service.impl.EntityBatchServiceImpl;
import org.dangcat.boot.service.impl.TimerServiceImpl;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.timer.CronAlarmClock;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.annotation.Service;
import org.dangcat.persistence.batch.EntityBatchStorer;
import org.dangcat.persistence.entity.EntityHelper;
import org.dangcat.persistence.entity.EntityManagerFactory;
import org.dangcat.persistence.filter.FilterExpress;
import org.dangcat.persistence.filter.FilterType;
import org.dangcat.persistence.filter.FilterUnit;
import org.dangcat.persistence.model.Table;

import java.util.Date;

/**
 * 服务器心跳监控。
 * @author dangcat
 * 
 */
public class ServerKeepLiveServiceImpl extends ServiceBase implements Runnable
{
    @Service
    private EventSendService eventSendService = null;

    /**
     * 构建服务
     * @param parent 所属父服务。
     */
    public ServerKeepLiveServiceImpl(ServiceProvider parent)
    {
        super(parent);
    }

    private boolean checkExists(Class<?> classType)
    {
        Table table = EntityHelper.getEntityMetaData(classType).getTable();
        if (table != null)
        {
            if (!table.exists())
            {
                table.create();
                return false;
            }
        }
        return true;
    }

    private void createAlarmClock()
    {
        CronAlarmClock cronAlarmClock = new CronAlarmClock(this)
        {
            @Override
            public String getCronExpression()
            {
                return ServerConfig.getInstance().getCronExpression();
            }

            @Override
            public boolean isEnabled()
            {
                return ServerConfig.getInstance().isEnabled();
            }
        };
        if (cronAlarmClock.isValidExpression())
            TimerServiceImpl.getInstance().createTimer(cronAlarmClock);
    }

    private EntityBatchStorer getEntityBatchStorer()
    {
        return EntityBatchServiceImpl.getInstance().getEntityBatchStorer();
    }

    @Override
    public Object handle(Event event)
    {
        // 在管理中心注册成功。
        if (event instanceof ServerEvent && ServerEvent.KeepLive.equalsIgnoreCase(event.getId()))
        {
            ServerEvent serverEvent = (ServerEvent) event;
            ServerInfo serverInfo = serverEvent.getServerInfo();
            if (serverInfo != null)
                this.store(serverInfo);
            event.setHandled(true);
        }
        return super.handle(event);
    }

    @Override
    public void initialize()
    {
        super.initialize();

        ServerConfig.getInstance().addConfigChangeEventAdaptor(new ChangeEventAdaptor()
        {
            @Override
            public void afterChanged(Object sender, Event event)
            {
                if (ServerConfig.CronExpression.equals(event.getId()))
                    createAlarmClock();
            }
        });
        this.createAlarmClock();

        this.checkExists(ServerInfo.class);
        this.checkExists(ServerStatusLog.class);
        ServerManager.getInstance().load();
    }

    /**
     * 定时清除过期数据。
     */
    @Override
    public void run()
    {
        try
        {
            int logMaxKeepLength = ServerConfig.getInstance().getLogMaxKeepLength() * -1;
            Date dateTime = DateUtils.add(DateUtils.DAY, DateUtils.now(), logMaxKeepLength);
            FilterExpress filterExpress = new FilterUnit(ServerStatusLog.DateTime, FilterType.le, dateTime);
            EntityManagerFactory.getInstance().open().delete(ServerStatusLog.class, filterExpress);
            if (logger.isDebugEnabled())
                logger.info("clear ServerStatusLog data where date <= " + DateUtils.format(dateTime));
        }
        catch (Exception e)
        {
            logger.error(this, e);
        }
    }

    private void store(ServerInfo serverInfo)
    {
        Date dateTime = DateUtils.now();
        serverInfo.setLastResponseTime(dateTime);
        ServerInfo exitsServerInfo = ServerManager.getInstance().getServerInfo(serverInfo.getIp());
        if (serverInfo.getId() == null && exitsServerInfo != null)
            serverInfo.setId(exitsServerInfo.getId());
        if (serverInfo.getId() == null || exitsServerInfo == null)
        {
            serverInfo.setId(null);
            EntityManagerFactory.getInstance().open().save(serverInfo);
            this.eventSendService.send(new ServerEvent(ServerEvent.Register, serverInfo));
            ServerManager.getInstance().load();
            return;
        }

        // 保存服务器信息。
        EntityBatchStorer entityBatchStorer = this.getEntityBatchStorer();
        entityBatchStorer.save(serverInfo);

        // 保存状态日志。
        this.checkExists(ServerStatusLog.class);
        ServerStatusLog serverStatusLog = new ServerStatusLog();
        ReflectUtils.copyProperties(serverInfo, serverStatusLog);
        serverStatusLog.setId(null);
        serverStatusLog.setServerId(serverInfo.getId());
        serverStatusLog.setDateTime(dateTime);
        entityBatchStorer.save(serverStatusLog);
    }
}
