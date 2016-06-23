package org.dangcat.commons.log;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

public class CallbackAppender extends AppenderSkeleton
{
    private LogCallback[] logCallbacks = null;

    public synchronized void addLogCallback(LogCallback logCallback)
    {
        if (logCallback != null)
        {
            if (this.logCallbacks == null)
                this.logCallbacks = new LogCallback[] { logCallback };
            else
            {
                LogCallback[] logCallbacks = new LogCallback[this.logCallbacks.length + 1];
                for (int i = 0; i < this.logCallbacks.length; i++)
                    logCallbacks[i] = this.logCallbacks[i];
                logCallbacks[this.logCallbacks.length] = logCallback;
                this.logCallbacks = logCallbacks;
            }
        }
    }

    @Override
    protected void append(LoggingEvent loggingEvent)
    {
        try
        {
            String message = this.getLayout().format(loggingEvent);
            if (this.logCallbacks != null && this.logCallbacks.length > 0)
            {
                for (LogCallback logCallback : this.logCallbacks)
                {
                    if (Level.DEBUG.equals(loggingEvent.getLevel()))
                        logCallback.debug(message);
                    else if (Level.WARN.equals(loggingEvent.getLevel()))
                        logCallback.warn(message);
                    else if (Level.INFO.equals(loggingEvent.getLevel()))
                        logCallback.info(message);
                    else if (Level.FATAL.equals(loggingEvent.getLevel()) || Level.ERROR.equals(loggingEvent.getLevel()))
                    {
                        StringBuilder text = new StringBuilder();
                        text.append(message);
                        String[] errors = loggingEvent.getThrowableStrRep();
                        if (errors != null && errors.length > 0)
                        {
                            for (int i = 0; i < errors.length; i++)
                            {
                                if (i > 0)
                                    text.append("\r\n");
                                text.append(errors[i]);
                            }
                            text.append("\r\n");
                        }

                        if (Level.FATAL.equals(loggingEvent.getLevel()))
                            logCallback.fatal(text.toString());
                        else if (Level.ERROR.equals(loggingEvent.getLevel()))
                            logCallback.error(text.toString());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean requiresLayout()
    {
        return true;
    }
}
