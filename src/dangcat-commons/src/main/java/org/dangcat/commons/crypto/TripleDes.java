package org.dangcat.commons.crypto;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * TripleDes加密工具的封装类
 */
class TripleDes {
    /**
     * 加密算法的名称
     */
    private static final String ALGORITHM = "DESede";

    /**
     * 常数不检查
     */
    private static final int BLOCK_SIZE = 8;

    // 密钥，长度为24字节
    // private static final byte[] KEYBYTES = { (byte) 0x43, (byte) 0x29,
    // (byte) 0x7F, (byte) 0xAD, (byte) 0x38, (byte) 0xE3, (byte) 0x73,
    // (byte) 0xFE, (byte) 0x1F, (byte) 0x08, (byte) 0x26, (byte) 0x0D,
    // (byte) 0x1A, (byte) 0xC2, (byte) 0x46, (byte) 0x5E, (byte) 0x58,
    // (byte) 0x40, (byte) 0x23, (byte) 0x64, (byte) 0x1A, (byte) 0xBA,
    // (byte) 0x61, (byte) 0x76 };
    private static final byte[] KEYBYTES = {(byte) 0x5A, (byte) 0x84, (byte) 0x78, (byte) 0x9F, (byte) 0xC7, (byte) 0x94, (byte) 0xC7, (byte) 0x48, (byte) 0xC0, (byte) 0x3A, (byte) 0x09,
            (byte) 0xB6, (byte) 0xB6, (byte) 0x05, (byte) 0xF6, (byte) 0xB9, (byte) 0x66, (byte) 0x09, (byte) 0x86, (byte) 0x6A, (byte) 0x3D, (byte) 0x7B, (byte) 0x3F, (byte) 0x4B};

    /**
     * 转换：包括加密算法的名称（例如，DES），后面可能跟有一个反馈模式和填充方案。 DESede/ECB/NoPadding
     */
    private static final String TRANSFORMATION = ALGORITHM + "/ECB/NoPadding";

    /**
     * 解密
     *
     * @param sourceBytes 密文
     * @return 原文，出错返回null
     */
    protected static byte[] decrypt(byte[] sourceBytes) {
        return decrypt(KEYBYTES, sourceBytes);
    }

    /**
     * 解密
     *
     * @param keys        密钥
     * @param sourceBytes 密文
     * @return 原文，出错返回null
     */
    private static byte[] decrypt(byte[] keys, byte[] sourceBytes) {
        if (keys == null || keys.length != 24)
            return null;

        // 输入长度必须是8的整数倍
        if (sourceBytes == null || sourceBytes.length % 8 != 0)
            return null;

        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keys, ALGORITHM);

            // 创建加密对象
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 解密
            cipher.init(Cipher.DECRYPT_MODE, deskey); // 解密模式

            byte[] decryptBytes = cipher.doFinal(sourceBytes);

            // 填充‘\0’，造成解密后的串带‘\0’,trim '\0'
            int index = findLastNotof(decryptBytes, (byte) 0);

            if (index == -1)
                return null;

            if (index == decryptBytes.length - 1)
                return decryptBytes;

            byte[] returnBytes = new byte[index + 1];
            System.arraycopy(decryptBytes, 0, returnBytes, 0, index + 1);
            return returnBytes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密
     *
     * @param sourceBytes 原文
     * @return 密文，出错返回null
     */
    protected static byte[] encrypt(byte[] sourceBytes) {
        return encrypt(KEYBYTES, sourceBytes);
    }

    /**
     * 加密
     *
     * @param keys        密钥
     * @param sourceBytes 原文
     * @return 密文，出错返回null
     */
    private static byte[] encrypt(byte[] keys, byte[] sourceBytes) {
        if (keys == null || keys.length != 24)
            return null;

        if (sourceBytes == null)
            return null;

        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keys, ALGORITHM);

            // 创建加密对象
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // 加密
            cipher.init(Cipher.ENCRYPT_MODE, deskey); // 加密模式

            // 如果不是BLOCK_SIZE的整数倍，末尾填充‘\0’
            int mod = sourceBytes.length % BLOCK_SIZE;
            if (mod == 0)
                return cipher.doFinal(sourceBytes);

            int lenngth = sourceBytes.length - mod + BLOCK_SIZE;
            byte[] srcPadded = new byte[lenngth]; // 初始为全0
            System.arraycopy(sourceBytes, 0, srcPadded, 0, sourceBytes.length);
            // 填充‘\0’造成解密后的串带‘\0’
            return cipher.doFinal(srcPadded);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * 找出最后一个不为指定字符的位置
     *
     * @param bytes     串
     * @param byteValue 指定字符
     * @return 索引, 找不到返回-1
     */
    private static int findLastNotof(byte[] bytes, byte byteValue) {
        for (int index = bytes.length - 1; index >= 0; --index) {
            if (bytes[index] != byteValue)
                return index;
        }
        return bytes.length - 1;
    }
}
