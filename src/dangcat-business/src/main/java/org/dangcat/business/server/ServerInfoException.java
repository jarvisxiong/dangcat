package org.dangcat.business.server;

import org.dangcat.business.exceptions.BusinessException;

/**
 * The service exception for ServerInfo.
 *@author ${authorName}
 * 
 */
public class ServerInfoException extends BusinessException
{
    private static final long serialVersionUID = 1L;

    public ServerInfoException(Class<?> classType, String fieldName, Integer messageId, Object... params)
    {
        super(classType, fieldName, messageId, params);
    }

    public ServerInfoException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }
}
