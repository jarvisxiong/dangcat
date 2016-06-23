package org.dangcat.commons.crypto;

import java.io.UnsupportedEncodingException;

import junit.framework.Assert;

import org.dangcat.commons.crypto.AESUtils;
import org.dangcat.commons.crypto.CryptoUtils;
import org.dangcat.commons.crypto.MD5Utils;
import org.junit.Test;

public class TestCrypto
{
    private static final String message = "this is a message for you!";
    private static final String password = "dangcat2014";

    @Test
    public void testAES() throws UnsupportedEncodingException
    {
        String encrypted = AESUtils.encrypt(message, password);
        Assert.assertEquals(message, AESUtils.decrypt(encrypted, password));
    }

    @Test
    public void testCrypto()
    {
        String sa = "sa";
        String encryptedsa = CryptoUtils.encrypt(sa);
        String encryptedPassword = CryptoUtils.encrypt(password);
        Assert.assertEquals(password, CryptoUtils.decrypt(encryptedPassword));
        Assert.assertEquals("sa", CryptoUtils.decrypt(encryptedsa));
        Assert.assertNull(CryptoUtils.decrypt(null));
        Assert.assertEquals("", CryptoUtils.decrypt(""));
    }

    @Test
    public void testMD5()
    {
        String encrypted = MD5Utils.encrypt(password, message);
        Assert.assertTrue(MD5Utils.isMatch(password, message, encrypted));
    }
}
