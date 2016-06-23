package org.dangcat.commons.crypto;

import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class AESUtils
{
    protected static final Logger logger = Logger.getLogger(AESUtils.class);
    private static final String AES = "AES";
    private static final String AESAlgorithm = "AES/CFB/NoPadding";
    private static final String MD5 = "MD5";

    private static Cipher createCipher(String password, int mode) throws Exception
    {
        Cipher cipher = Cipher.getInstance(AESAlgorithm);
        byte[] keyBytes = createKey(password);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, AES);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(keyBytes);
        cipher.init(mode, secretKeySpec, ivParameterSpec);
        return cipher;
    }

    private static byte[] createKey(String password) throws Exception
    {
        MessageDigest messageDigest = MessageDigest.getInstance(MD5);
        if (password != null)
            messageDigest.update(password.getBytes());
        return messageDigest.digest();
    }

    public static String decrypt(String content, String password)
    {
        String result = null;
        if (content != null)
        {
            try
            {
                Cipher cipher = createCipher(password, Cipher.DECRYPT_MODE);
                byte[] decodeBytes = BASE64Coder.decode(content);
                byte[] decryptBytes = cipher.doFinal(decodeBytes);
                if (decryptBytes != null)
                    result = new String(decryptBytes);
            }
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                    logger.error(e, e);
                else
                    logger.error(e);
            }
        }
        return result;
    }

    public static String encrypt(String content, String password)
    {
        String result = null;
        if (content != null)
        {
            try
            {
                Cipher cipher = createCipher(password, Cipher.ENCRYPT_MODE);
                byte[] encryptBytes = cipher.doFinal(content.getBytes());
                if (encryptBytes != null)
                    result = BASE64Coder.encode(encryptBytes);
            }
            catch (Exception e)
            {
                if (logger.isDebugEnabled())
                    logger.error(e, e);
                else
                    logger.error(e);
            }
        }
        return result;
    }
}
