package org.dangcat.commons.log;

public interface LogCallback
{
    public void debug(String message);

    public void error(String message);

    public void fatal(String message);

    public void info(String message);

    public void warn(String message);
}
