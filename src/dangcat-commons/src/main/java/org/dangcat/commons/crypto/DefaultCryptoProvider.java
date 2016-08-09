package org.dangcat.commons.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DefaultCryptoProvider implements CryptoProvider {
    @Override
    public String decrypt(String sourceText, String charsetName) {
        if (sourceText == null)
            return null;

        if (sourceText.length() == 0)
            return "";

        try {
            // Base64解密
            byte[] decodeBytes = BASE64Coder.decode(sourceText);

            // TripleDes解密
            byte[] decryptBytes = TripleDes.decrypt(decodeBytes);

            // 解密错误
            if (decryptBytes == null)
                return null;

            return new String(decryptBytes, charsetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String encrypt(String sourceText, String charsetName) {
        if (sourceText == null)
            return null;

        if (sourceText.length() == 0)
            return "";

        try {
            // TripleDES加密
            byte[] encryptBytes = TripleDes.encrypt(sourceText.getBytes(charsetName));
            // 加密错误
            if (encryptBytes == null)
                return null;
            // Base64加密
            return new String(BASE64Coder.encode(encryptBytes));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
