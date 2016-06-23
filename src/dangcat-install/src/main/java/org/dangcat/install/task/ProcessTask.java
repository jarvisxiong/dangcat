package org.dangcat.install.task;

import org.apache.log4j.Logger;

public interface ProcessTask
{
    public void cancel();

    public void execute(Logger logger) throws Exception;

    public long getFinishedSize();

    public long getTaskSize();

    public boolean isEnabled();

    public void prepare();

    public void setEnabled(boolean enabled);
}
