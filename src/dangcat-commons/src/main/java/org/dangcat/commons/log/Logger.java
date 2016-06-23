package org.dangcat.commons.log;

import org.apache.log4j.Appender;
import org.apache.log4j.Category;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.dangcat.commons.utils.ValueUtils;

public class Logger extends org.apache.log4j.Logger {
    private String appenderName = null;
    private LogCallback logCallback = null;
    private org.apache.log4j.Logger logger = null;

    public Logger(org.apache.log4j.Logger logger) {
        super(logger.getName());
        this.logger = logger;
    }

    public Logger(org.apache.log4j.Logger logger, String appenderName, LogCallback logCallback) {
        super(logger.getName());
        this.logger = logger;
        this.logCallback = logCallback;
        this.appenderName = appenderName;
    }

    private String createMessage(Object message, Level level, Throwable throwable) {
        Layout layout = getLayout(this.logger);
        if (layout == null)
            return null;

        LoggingEvent loggingEvent = new LoggingEvent(Category.class.getName(), this.logger, level, message, throwable);
        String content = layout.format(loggingEvent);
        if (Level.FATAL.equals(loggingEvent.getLevel()) || Level.ERROR.equals(loggingEvent.getLevel())) {
            StringBuilder text = new StringBuilder();
            text.append(content);
            String[] errors = loggingEvent.getThrowableStrRep();
            if (errors != null && errors.length > 0) {
                for (int i = 0; i < errors.length; i++) {
                    if (i > 0)
                        text.append("\r\n");
                    text.append(errors[i]);
                }
                text.append("\r\n");
            }
            content = text.toString();
        }
        return content;
    }

    @Override
    public void debug(Object message) {
        logger.debug(message);
        if (logCallback != null && this.isDebugEnabled())
            logCallback.debug(this.createMessage(message, Level.DEBUG, null));
    }

    @Override
    public void debug(Object message, Throwable throwable) {
        logger.debug(message, throwable);
        if (logCallback != null && this.isDebugEnabled())
            logCallback.debug(this.createMessage(message, Level.DEBUG, throwable));
    }

    @Override
    public void error(Object message) {
        logger.error(message);
        if (logCallback != null)
            logCallback.error(this.createMessage(message, Level.ERROR, null));
    }

    @Override
    public void error(Object message, Throwable throwable) {
        logger.error(message, throwable);
        if (logCallback != null)
            logCallback.error(this.createMessage(message, Level.ERROR, throwable));
    }

    @Override
    public void fatal(Object message) {
        logger.fatal(message);
        if (logCallback != null)
            logCallback.fatal(message.toString());
    }

    @Override
    public void fatal(Object message, Throwable throwable) {
        logger.fatal(message, throwable);
        if (logCallback != null)
            logCallback.fatal(this.createMessage(message, Level.FATAL, throwable));
    }

    private Layout getLayout(Category category) {
        Layout layout = null;
        if (category != null && !ValueUtils.isEmpty(this.appenderName)) {
            Appender appender = category.getAppender(this.appenderName);
            if (appender != null)
                layout = appender.getLayout();
            if (layout == null && category.getAllAppenders().hasMoreElements()) {
                appender = (Appender) category.getAllAppenders().nextElement();
                if (appender != null)
                    layout = appender.getLayout();
            }

            if (layout == null)
                layout = this.getLayout(category.getParent());
        }
        return layout;
    }

    @Override
    public void info(Object message) {
        logger.info(message);
        if (logCallback != null && this.isInfoEnabled())
            logCallback.info(this.createMessage(message, Level.INFO, null));
    }

    @Override
    public void info(Object message, Throwable throwable) {
        logger.info(message, throwable);
        if (logCallback != null && this.isInfoEnabled())
            logCallback.info(this.createMessage(message, Level.INFO, throwable));
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void warn(Object message) {
        logger.warn(message);
        if (logCallback != null)
            logCallback.warn(this.createMessage(message, Level.WARN, null));
    }

    @Override
    public void warn(Object message, Throwable throwable) {
        logger.warn(message, throwable);
        if (logCallback != null)
            logCallback.warn(this.createMessage(message, Level.WARN, throwable));
    }
}
