package org.dangcat.net.ftp.exceptions;

import org.dangcat.framework.exception.ServiceException;

/**
 * 会话产生的异常。
 * @author dangcat
 * 
 */
public class FTPSessionException extends ServiceException
{
    public static final Integer CONNECTION = 104;
    public static final Integer CREATEDIR = 102;
    public static final Integer DOWNLOAD = 101;
    public static final Integer INITPATH = 105;
    private static final long serialVersionUID = 1L;
    public static final Integer TIMEOUT = 106;
    public static final Integer UNKONWN = 100;
    public static final Integer UPLOAD = 103;

    public FTPSessionException(Integer messageId, Object... params)
    {
        super(messageId, params);
    }

    public FTPSessionException(Throwable cause)
    {
        super(cause);
    }
}
