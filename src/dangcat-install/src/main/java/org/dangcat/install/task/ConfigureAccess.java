package org.dangcat.install.task;

import java.util.Properties;

public interface ConfigureAccess
{
    public boolean isEnabled();

    public void load(Properties properties);

    public void save(Properties properties);
}
