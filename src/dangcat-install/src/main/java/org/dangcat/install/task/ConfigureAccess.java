package org.dangcat.install.task;

import java.util.Properties;

public interface ConfigureAccess {
    boolean isEnabled();

    void load(Properties properties);

    void save(Properties properties);
}
