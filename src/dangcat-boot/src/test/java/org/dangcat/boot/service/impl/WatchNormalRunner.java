package org.dangcat.boot.service.impl;

import org.apache.log4j.Logger;
import org.dangcat.boot.service.WatchRunnable;

public class WatchNormalRunner implements WatchRunnable
{
    private int actualValue = 0;
    private int expectedValue = 100;
    private long lastResponseTime = System.currentTimeMillis();
    protected Logger logger = Logger.getLogger(this.getClass());

    @Override
    public long getLastResponseTime()
    {
        return this.lastResponseTime;
    }

    @Override
    public Logger getLogger()
    {
        return this.logger;
    }

    @Override
    public long getTimeOutLength()
    {
        return 5000l;
    }

    public boolean isSucess()
    {
        return this.actualValue == this.expectedValue;
    }

    @Override
    public void run()
    {
        this.actualValue = this.expectedValue;
    }

    @Override
    public void terminate()
    {
    }
}
