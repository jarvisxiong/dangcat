package org.dangcat.commons.crypto;

import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 平台的加密解密工具 http://zh.wikipedia.org/wiki/Base64
 * base64使用的字符包括大小写字母各26个，加上10个数字，和加号「+」，斜杠「/」， 一共64个字符，等号「=」用来作为后缀用途。
 * 完整的base64定义可见 RFC 1421和 RFC 2045。编码后的数据比原始数据略长，为原来的4/3
 */
public class CryptoUtils {
    private static final String DANGCAT_CRYPTO_PROVIDER = "dangcat.Crypto.Provider";
    private static final String DEFAULT_CHARSETNAME = "UTF-8";
    private static CryptoProvider cryptoProvider = null;

    /**
     * 解密
     *
     * @param src 密文
     * @return 原文，出错返回null
     */
    public static String decrypt(String sourceText) {
        return decrypt(sourceText, DEFAULT_CHARSETNAME);
    }

    /**
     * 解密
     *
     * @param src 密文
     * @return 原文，出错返回null
     */
    public static String decrypt(String sourceText, String charsetName) {
        return getCryptoProvider().decrypt(sourceText, charsetName);
    }

    /**
     * 加密,不支持'\0'结尾的串
     *
     * @param text 原文
     * @return 密文，出错返回null
     */
    public static String encrypt(String sourceText) {
        return encrypt(sourceText, DEFAULT_CHARSETNAME);
    }

    /**
     * 加密,不支持'\0'结尾的串
     *
     * @param text 原文
     * @return 密文，出错返回null
     */
    public static String encrypt(String sourceText, String charsetName) {
        return getCryptoProvider().encrypt(sourceText, charsetName);
    }

    private static CryptoProvider getCryptoProvider() {
        if (cryptoProvider == null) {
            String cryptoProviderClass = System.getProperty(DANGCAT_CRYPTO_PROVIDER);
            if (!ValueUtils.isEmpty(cryptoProviderClass))
                cryptoProvider = (CryptoProvider) ReflectUtils.newInstance(cryptoProviderClass);
        }
        if (cryptoProvider == null)
            cryptoProvider = new DefaultCryptoProvider();
        return cryptoProvider;
    }

    public static void main(String[] args) {
        System.out.println(encrypt(args[0]));
    }
}
