package org.dangcat.commons.log;

public interface LogCallback
{
    void debug(String message);

    void error(String message);

    void fatal(String message);

    void info(String message);

    void warn(String message);
}
