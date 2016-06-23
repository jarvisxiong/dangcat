package org.dangcat.commons.crypto;

/**
 * 加密和解密算法。
 * @author dangcat
 * 
 */
public interface CryptoProvider
{
    /**
     * 解密算法。
     * @param sourceText 源文字。
     * @param charsetName 字符集。
     * @return 解密文字。
     */
    String decrypt(String sourceText, String charsetName);

    /**
     * 加密算法。
     * @param sourceText 源文字。
     * @param charsetName 字符集。
     * @return 加密文字。
     */
    String encrypt(String sourceText, String charsetName);
}
