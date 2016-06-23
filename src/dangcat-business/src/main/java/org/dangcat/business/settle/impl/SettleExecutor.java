package org.dangcat.business.settle.impl;

import org.apache.log4j.Logger;
import org.dangcat.business.settle.SettleUnit;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.persistence.entity.EntityManager;
import org.dangcat.persistence.entity.EntityManagerFactory;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ½áËã·þÎñ¡£
 * @author dangcat
 * 
 */
class SettleExecutor
{
    protected static final Logger logger = Logger.getLogger(SettleExecutor.class);
    private DateTimeExecutor currentExecutor = null;
    private EntityManager entityManager = null;
    private boolean isRunning = false;
    private SettleUnit settleUnit = null;
    private Map<String, DateTimeExecutor> sheduleExecutorMap = new LinkedHashMap<String, DateTimeExecutor>();

    SettleExecutor(SettleUnit settleUnit)
    {
        this.settleUnit = settleUnit;
    }

    private void checkCurrentExecutor()
    {
        if (this.currentExecutor != null && !this.currentExecutor.isEquals(DateUtils.now()))
        {
            synchronized (this.sheduleExecutorMap)
            {
                this.sheduleExecutorMap.put(this.currentExecutor.getName(), this.currentExecutor);
            }
            this.currentExecutor = null;
        }
    }

    private DateTimeExecutor createDateTimeExecutor(Date dateTime)
    {
        DateTimeExecutor dateTimeExecutor = new DateTimeExecutor(dateTime);
        dateTimeExecutor.setSettleUnit(this.settleUnit);
        dateTimeExecutor.setEntityManager(this.getEntityManager());
        dateTimeExecutor.initialize();
        return dateTimeExecutor;
    }

    protected synchronized void execute()
    {
        if (this.isRunning)
            return;

        try
        {
            this.isRunning = true;
            this.checkCurrentExecutor();

            DateTimeExecutor dateTimeExecutor = null;
            do
            {
                synchronized (this.sheduleExecutorMap)
                {
                    if (this.sheduleExecutorMap.isEmpty())
                        dateTimeExecutor = null;
                    else
                    {
                        String key = this.sheduleExecutorMap.keySet().iterator().next();
                        dateTimeExecutor = this.sheduleExecutorMap.remove(key);
                    }
                }
                if (dateTimeExecutor != null)
                    this.execute(dateTimeExecutor);
            } while (dateTimeExecutor != null);

            this.execute(this.getCurrentExecutor());
        }
        finally
        {
            this.isRunning = false;
        }
    }

    private void execute(DateTimeExecutor dateTimeExecutor)
    {
        int count = 0;
        long beginTime = DateUtils.currentTimeMillis();
        try
        {
            count = dateTimeExecutor.execute();
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error(e);
        }
        finally
        {
            if (count > 0)
            {
                long timeCost = DateUtils.currentTimeMillis() - beginTime;
                logger.info("The unit " + dateTimeExecutor.getName() + " settle finished, code time " + timeCost + "(ms).");
            }
        }
    }

    private DateTimeExecutor getCurrentExecutor()
    {
        if (this.currentExecutor == null)
            this.currentExecutor = this.createDateTimeExecutor(DateUtils.now());
        return this.currentExecutor;
    }

    private EntityManager getEntityManager()
    {
        if (this.entityManager == null)
            this.entityManager = EntityManagerFactory.getInstance().open();
        return this.entityManager;
    }

    protected void reset()
    {
        this.currentExecutor = null;
        this.sheduleExecutorMap.clear();
    }

    protected void shedule(Date dateTime)
    {
        if (!this.getCurrentExecutor().equals(dateTime))
        {
            DateTimeExecutor dateTimeExecutor = this.createDateTimeExecutor(dateTime);
            synchronized (this.sheduleExecutorMap)
            {
                this.sheduleExecutorMap.put(dateTimeExecutor.getName(), dateTimeExecutor);
            }
        }
    }
}
