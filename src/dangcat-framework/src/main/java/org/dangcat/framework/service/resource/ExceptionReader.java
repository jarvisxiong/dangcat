package org.dangcat.framework.service.resource;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.exception.ServiceException;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;

public class ExceptionReader
{
    private Collection<ServiceException> serviceExceptionCollection = new LinkedHashSet<ServiceException>();

    public ExceptionReader(Class<?>... classTypes)
    {
        for (Class<?> classType : classTypes)
        {
            if (ServiceException.class.isAssignableFrom(classType))
            {
                ServiceException serviceException = (ServiceException) ReflectUtils.newInstance(classType, new Class<?>[] { Integer.class, Object[].class }, new Object[] { null, null });
                if (serviceException != null)
                    this.serviceExceptionCollection.add(serviceException);
            }
        }
    }

    public String getText(Locale locale, Integer messageId)
    {
        String text = null;
        for (ServiceException serviceException : this.serviceExceptionCollection)
        {
            synchronized (serviceException)
            {
                serviceException.setLocale(locale);
                serviceException.setMessageId(messageId);
                String message = serviceException.getMessage();
                if (!ValueUtils.isEmpty(message) && !message.equals(messageId.toString()))
                {
                    text = message;
                    break;
                }
            }
        }
        return text;
    }
}
