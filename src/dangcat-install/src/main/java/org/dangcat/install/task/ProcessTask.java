package org.dangcat.install.task;

import org.apache.log4j.Logger;

public interface ProcessTask
{
    void cancel();

    void execute(Logger logger) throws Exception;

    long getFinishedSize();

    long getTaskSize();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    void prepare();
}
