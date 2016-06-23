package org.dangcat.business.settle.impl;

import org.dangcat.boot.service.impl.ThreadService;
import org.dangcat.business.settle.SettleConfig;
import org.dangcat.business.settle.SettleService;
import org.dangcat.business.settle.SettleUnit;
import org.dangcat.commons.timer.CronAlarmClock;
import org.dangcat.framework.service.ServiceProvider;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 结算服务。
 * @author dangcat
 * 
 */
public class SettleServiceImpl extends ThreadService implements SettleService
{
    private static final String SERVICE_NAME = "SETTLE";
    /** 结算对象列表。 */
    private Map<SettleUnit, SettleExecutor> settleUnitMap = new LinkedHashMap<SettleUnit, SettleExecutor>();

    /**
     * 构造服务。
     * @param parent 所属服务。
     */
    public SettleServiceImpl(ServiceProvider parent)
    {
        super(parent, SERVICE_NAME);
    }

    /**
     * 添加结算对象。
     * @param SettleUnit 结算对象。
     */
    public void addSettleUnit(SettleUnit settleUnit)
    {
        if (settleUnit != null && !this.settleUnitMap.containsKey(settleUnit))
            this.settleUnitMap.put(settleUnit, new SettleExecutor(settleUnit));
    }

    /**
     * 执行结算。
     */
    @Override
    public void execute()
    {
        this.innerExecute();
    }

    @Override
    public void initialize()
    {
        super.initialize();

        CronAlarmClock cronAlarmClock = new CronAlarmClock(this)
        {
            @Override
            public String getCronExpression()
            {
                return SettleConfig.getInstance().getCronExpression();
            }

            @Override
            public boolean isEnabled()
            {
                return SettleConfig.getInstance().isEnabled() && SettleServiceImpl.this.settleUnitMap.size() > 0;
            }
        };
        if (cronAlarmClock.isValidExpression())
            this.setAlarmClock(cronAlarmClock);
    }

    /**
     * 定时执行结算。
     */
    @Override
    protected void innerExecute()
    {
        if (this.settleUnitMap.size() > 0)
        {
            for (SettleExecutor settleExecutor : this.settleUnitMap.values())
                settleExecutor.execute();
        }
    }

    /**
     * 删除结算对象。
     * @param settleUnit 结算对象。
     */
    public void removeSettleUnit(SettleUnit settleUnit)
    {
        if (settleUnit != null && this.settleUnitMap.containsKey(settleUnit))
            this.settleUnitMap.remove(settleUnit);
    }

    @Override
    public void stop()
    {
        if (this.settleUnitMap.size() > 0)
        {
            for (SettleExecutor settleExecutor : this.settleUnitMap.values())
                settleExecutor.reset();
        }
        super.stop();
    }
}
