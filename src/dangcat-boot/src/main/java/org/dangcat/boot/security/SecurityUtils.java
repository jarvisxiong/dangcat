package org.dangcat.boot.security;

import javax.servlet.http.HttpServletRequest;

import org.dangcat.commons.crypto.AESUtils;
import org.dangcat.commons.crypto.MD5Utils;
import org.dangcat.commons.utils.ParseUtils;
import org.dangcat.framework.service.ServiceContext;

public class SecurityUtils
{
    public static String decryptContent(String content)
    {
        return AESUtils.decrypt(content, getPassword());
    }

    public static String encryptContent(String content)
    {
        return AESUtils.encrypt(content, getPassword());
    }

    public static String encryptPassword(String no, String password)
    {
        return encryptContent(MD5Utils.encrypt(no, password));
    }

    private static String getPassword()
    {
        String password = null;
        ServiceContext serviceContext = ServiceContext.getInstance();
        if (serviceContext != null)
            password = serviceContext.getSessionId();
        return password;
    }

    public static String getRemoteHost()
    {
        String remoteHost = null;
        HttpServletRequest httpServletRequest = ServiceContext.getInstance().getParam(HttpServletRequest.class);
        if (httpServletRequest != null)
            remoteHost = httpServletRequest.getRemoteHost();
        return remoteHost;
    }

    public static boolean isMatch(String sourcePassword, String encryptedPassword)
    {
        byte[] sourceBytes = ParseUtils.parseHex(sourcePassword);
        byte[] encryptedBytes = ParseUtils.parseHex(encryptedPassword);
        if (sourceBytes.length != encryptedBytes.length)
            return false;

        for (int i = 0; i < sourceBytes.length; i++)
        {
            if (encryptedBytes[i] != sourceBytes[i])
                return false;
        }
        return true;
    }

    public static String storePassword(String no, String password)
    {
        return MD5Utils.encrypt(no, password);
    }
}
