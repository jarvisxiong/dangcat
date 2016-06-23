package org.dangcat.commons.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ParseUtils;

public class MD5Utils
{
    protected static final Logger logger = Logger.getLogger(MD5Utils.class);
    private static final String MD5 = "MD5";

    /**
     * 用MD5算法加密。
     * @param messages 信息。
     * @return 加密后的字串。
     */
    public static String encrypt(String... messages)
    {
        String result = null;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance(MD5);
            if (messages != null)
            {
                for (int i = 0; i < messages.length; i++)
                {
                    if (messages[i] != null)
                        messageDigest.update(messages[i].getBytes());
                }
            }

            byte[] encryptedBytes = messageDigest.digest();
            result = ParseUtils.toHex(encryptedBytes, 0, null);
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error(e, e);
        }
        return result;
    }

    /**
     * 是否符合MD5加密。
     * @param password 口令。
     * @param message 信息。
     * @param encrypted 加密字串。
     * @return 是否匹配。
     */
    public static boolean isMatch(String password, String message, String encrypted)
    {
        if (encrypted == null)
            return false;

        boolean result = true;
        try
        {
            MessageDigest messageDigest = MessageDigest.getInstance(MD5);
            if (password != null)
                messageDigest.update(password.getBytes());
            if (message != null)
                messageDigest.update(message.getBytes());

            byte[] sourceBytes = messageDigest.digest();
            byte[] encryptedBytes = ParseUtils.parseHex(encrypted);
            if (sourceBytes.length != encryptedBytes.length)
                return false;

            for (int i = 0; i < sourceBytes.length; i++)
            {
                if (encryptedBytes[i] != sourceBytes[i])
                    return false;
            }
        }
        catch (NoSuchAlgorithmException e)
        {
            logger.error(e, e);
            result = false;
        }
        return result;
    }
}
