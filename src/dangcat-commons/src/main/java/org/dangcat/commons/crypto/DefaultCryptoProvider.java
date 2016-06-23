package org.dangcat.commons.crypto;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class DefaultCryptoProvider implements CryptoProvider
{
    @Override
    public String decrypt(String sourceText, String charsetName)
    {
        if (sourceText == null)
            return null;

        if (sourceText.length() == 0)
            return "";

        try
        {
            // Base64Ω‚√‹
            byte[] decodeBytes = BASE64Coder.decode(sourceText);

            // TripleDesΩ‚√‹
            byte[] decryptBytes = TripleDes.decrypt(decodeBytes);

            // Ω‚√‹¥ÌŒÛ
            if (decryptBytes == null)
                return null;

            return new String(decryptBytes, charsetName);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String encrypt(String sourceText, String charsetName)
    {
        if (sourceText == null)
            return null;

        if (sourceText.length() == 0)
            return "";

        try
        {
            // TripleDESº”√‹
            byte[] encryptBytes = TripleDes.encrypt(sourceText.getBytes(charsetName));
            // º”√‹¥ÌŒÛ
            if (encryptBytes == null)
                return null;
            // Base64º”√‹
            return new String(BASE64Coder.encode(encryptBytes));
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
