package org.dangcat.install.task;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.log4j.Logger;

public class ProcessTaskCollection implements ProcessTask
{
    private boolean cancel = false;
    private boolean enabled = true;
    private Collection<ProcessTask> processTasks = new LinkedList<ProcessTask>();

    public void addTask(ProcessTask processTask)
    {
        if (processTask != null)
            this.processTasks.add(processTask);
    }

    @Override
    public void cancel()
    {
        this.cancel = true;
        for (ProcessTask processTask : this.processTasks)
        {
            if (processTask.isEnabled())
                processTask.cancel();
        }
    }

    @Override
    public void execute(Logger logger) throws Exception
    {
        this.cancel = false;
        for (ProcessTask processTask : this.processTasks)
        {
            if (this.isCancel())
                break;

            if (processTask.isEnabled())
                processTask.execute(logger);
        }
    }

    @Override
    public long getFinishedSize()
    {
        long finishedSize = 0l;
        for (ProcessTask processTask : this.processTasks)
        {
            if (processTask.isEnabled())
                finishedSize += processTask.getFinishedSize();
        }
        return finishedSize;
    }

    @Override
    public long getTaskSize()
    {
        long taskSize = 0l;
        for (ProcessTask processTask : this.processTasks)
        {
            if (processTask.isEnabled())
                taskSize += processTask.getTaskSize();
        }
        return taskSize;
    }

    public boolean isCancel()
    {
        return this.cancel;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    @Override
    public void prepare()
    {
        for (ProcessTask processTask : this.processTasks)
        {
            if (processTask.isEnabled())
                processTask.prepare();
        }
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }
}
