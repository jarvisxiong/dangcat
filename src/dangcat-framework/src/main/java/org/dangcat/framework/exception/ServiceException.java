package org.dangcat.framework.exception;

import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.serialize.json.JsonSerializer;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.serialize.json.ServiceExceptionSerializer;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 应用层异常的基类。
 * @author dangcat
 * 
 */
public class ServiceException extends Exception
{
    private static final long serialVersionUID = -3548883238371600261L;
    static
    {
        JsonSerializer.addSerialize(new ServiceExceptionSerializer());
    }

    /**
     * Holds a collection of exceptions added to this exceptions, for use when
     * the application desires to return multiple exceptions conditions at once.
     * For instance, if the application must evaluate and validate multiple
     * pieces of data before performing an operation, it might be beneficial to
     * validate all of the data and collect all resulting exceptions to be
     * passed back at once.
     */
    private List<ServiceException> exceptionList = null;
    /** 消息国际化。 */
    private Locale locale = null;
    /** The ID to the bundle message. */
    private Integer messageId = null;
    /** The key to the bundle message. */
    private String messageKey = null;
    /** 异常参数。 */
    private List<Object> paramList = null;
    /**
     * An optional nested exceptions used to provide the ability to encapsulate
     * a lower-level exceptions to provide more detailed context information
     * concerning the exact cause of the exceptions.
     */
    private Throwable rootCause = null;

    public ServiceException()
    {
        super();
    }

    public ServiceException(Integer messageId, Object... params)
    {
        this.messageId = messageId;
        this.addParams(params);
    }

    public ServiceException(String messageKey, Object... params)
    {
        super(messageKey);
        this.messageKey = messageKey;
        this.addParams(params);
    }

    public ServiceException(Throwable cause)
    {
        super(cause);
        this.rootCause = cause;
    }

    public ServiceException(Throwable cause, Integer messageId)
    {
        super(cause);
        this.rootCause = cause;
        this.messageId = messageId;
    }

    /**
     * 添加子异常，
     * @param serviceException 子异常。
     */
    public void addException(ServiceException serviceException)
    {
        if (serviceException != null)
        {
            if (this.exceptionList == null)
                this.exceptionList = new ArrayList<ServiceException>();
            if (!this.exceptionList.contains(serviceException))
                this.exceptionList.add(serviceException);
        }
    }

    /**
     * Set an object array that can be used for parametric replacement.
     */
    public void addParams(Object... params)
    {
        if (params != null && params.length > 0)
        {
            if (this.paramList == null)
                this.paramList = new ArrayList<Object>();
            this.paramList.addAll(Arrays.asList(params));
        }
    }

    protected String format(String message)
    {
        if (this.paramList != null && this.paramList.size() > 0)
            message = MessageFormat.format(message, this.paramList.toArray());
        return message;
    }

    /**
     * 子异常列表。
     */
    public List<ServiceException> getChildExceptionList()
    {
        return this.exceptionList;
    }

    public Locale getLocale()
    {
        return this.locale == null ? Environment.getDefaultLocale() : this.locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    @Override
    public String getMessage()
    {
        if (this.getMessageId() != null)
        {
            String message = ResourceUtils.getText(this.getClass(), this.getLocale(), this.getMessageId().toString());
            if (ValueUtils.isEmpty(message))
                return this.getMessageId().toString();
            return this.format(message);
        }
        return super.getMessage();
    }

    public Integer getMessageId()
    {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    /**
     * 异常主键值。
     */
    public String getMessageKey()
    {
        return messageKey;
    }

    /**
     * Set the key to the bundle.
     */
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * 参数列表。
     */
    public Object[] getParams()
    {
        return this.paramList == null ? null : this.paramList.toArray();
    }

    /**
     * Return the root cause exceptions, if one exists.
     */
    public Throwable getRootCause()
    {
        return rootCause;
    }

    /**
     * Set a nested, encapsulated exceptions to provide more low-level detailed
     * information to the client.
     */
    public void setRootCause(Throwable throwable) {
        this.rootCause = throwable;
    }

    /**
     * Print both the normal and rootCause stack traces.
     */
    @Override
    public void printStackTrace()
    {
        printStackTrace(System.err);
    }

    /**
     * Print both the normal and rootCause stack traces.
     */
    @Override
    public void printStackTrace(PrintStream outStream)
    {
        this.printStackTrace(new PrintWriter(outStream));
    }

    /**
     * Print both the normal and rootCause stack traces.
     */
    @Override
    public void printStackTrace(PrintWriter writer)
    {
        super.printStackTrace(writer);
        if (this.getRootCause() != null)
            this.getRootCause().printStackTrace(writer);
        writer.flush();
    }
}
