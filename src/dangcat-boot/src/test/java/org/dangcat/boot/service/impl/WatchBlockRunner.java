package org.dangcat.boot.service.impl;

import java.io.IOException;

public class WatchBlockRunner extends WatchNormalRunner
{
    private boolean isRunning = false;

    @Override
    public void run()
    {
        this.isRunning = true;
        while (this.isRunning)
        {
            try
            {
                System.out.println(System.in.read());
            }
            catch (IOException e)
            {
                this.logger.error(this, e);
            }
        }
    }

    @Override
    public void terminate()
    {
        this.isRunning = false;
    }
}
