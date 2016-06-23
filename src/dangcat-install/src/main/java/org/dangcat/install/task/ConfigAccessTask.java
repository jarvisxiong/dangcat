package org.dangcat.install.task;

import org.apache.log4j.Logger;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public class ConfigAccessTask implements ProcessTask
{
    private Collection<ConfigFileAccess> configFileAccesses = new LinkedList<ConfigFileAccess>();
    private boolean enabled = true;
    private long finishedSize = 0l;

    public void addConfigFileAccess(ConfigFileAccess configFileAccess)
    {
        if (configFileAccess != null)
            this.configFileAccesses.add(configFileAccess);
    }

    @Override
    public void cancel()
    {
        this.load();
    }

    @Override
    public void execute(Logger logger)
    {
        try
        {
            for (ConfigFileAccess configFileAccess : this.configFileAccesses)
            {
                if (configFileAccess.isEnabled())
                {
                    File configFile = configFileAccess.getConfigFile();
                    logger.info("Save config data: " + configFile.getAbsolutePath());
                    configFileAccess.save();
                }
            }
            this.finishedSize++;
        }
        catch (Exception e)
        {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public long getFinishedSize()
    {
        return this.finishedSize;
    }

    @Override
    public long getTaskSize()
    {
        return this.configFileAccesses.size();
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void load()
    {
        for (ConfigFileAccess configFileAccess : this.configFileAccesses)
        {
            if (configFileAccess.isEnabled())
                configFileAccess.load();
        }
    }

    @Override
    public void prepare()
    {
    }
}
