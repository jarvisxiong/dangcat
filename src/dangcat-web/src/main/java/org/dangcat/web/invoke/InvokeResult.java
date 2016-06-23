package org.dangcat.web.invoke;

public class InvokeResult
{
    private boolean finished = false;
    private String name = null;
    private double process = 0;
    private int status = 0;

    public String getName()
    {
        return name;
    }

    public double getProcess()
    {
        return process;
    }

    public int getStatus()
    {
        return status;
    }

    public boolean isFinished()
    {
        return finished;
    }

    public void setFinished(boolean finished)
    {
        this.finished = finished;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setProcess(double process)
    {
        this.process = process;
    }

    public void setStatus(int status)
    {
        this.status = status;
    }
}
