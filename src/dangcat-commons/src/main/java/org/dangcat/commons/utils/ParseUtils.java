package org.dangcat.commons.utils;

/**
 * 报文解析工具。
 */
public class ParseUtils {
    private static final String HEX_SPACE = " ";
    private static final String HEX_TEXT = "0123456789ABCDEF";

    /**
     * 由二进制数组解析整数。
     *
     * @param bytes      二进制数组。
     * @param beginIndex 其实索引位置。
     * @param length     长度。
     * @return 解析后的整数。
     */
    public static final int getInt(byte[] bytes, int beginIndex, int length) {
        return (int) getLong(bytes, beginIndex, length);
    }

    /**
     * 由二进制数组解析长整数。
     *
     * @param bytes      二进制数组。
     * @param beginIndex 其实索引位置。
     * @param length     长度。
     * @return 解析后的整数。
     */
    public static final long getLong(byte[] bytes, int beginIndex, int length) {
        long value = 0;
        for (int i = 0; i < length; i++)
            value |= (long) (bytes[beginIndex + i] & 0xff) << (8 * (length - 1 - i));
        return value;
    }

    /**
     * 由字符串解析二进制数组。
     *
     * @param text 字符串。
     * @return 解析后的数组。
     */
    public static final byte[] parseHex(String text) {
        return parseHex(text, HEX_SPACE);
    }

    /**
     * 由字符串解析二进制数组。
     *
     * @param text  字符串。
     * @param space 分割字符。
     * @return 解析后的数组。
     */
    public static final byte[] parseHex(String text, String space) {
        if (ValueUtils.isEmpty(text))
            return null;

        String hexText = text.trim().toUpperCase();
        String[] hexArray = null;
        if (space != null && hexText.indexOf(space) > -1)
            hexArray = hexText.split(space);
        else {
            hexArray = new String[hexText.length() / 2];
            for (int i = 0; i < hexArray.length; i++)
                hexArray[i] = hexText.substring(i * 2, (i + 1) * 2);
        }
        byte[] bytes = new byte[hexArray.length];
        int index = 0;
        for (String hex : hexArray) {
            bytes[index] |= (byte) ((HEX_TEXT.indexOf(hex.substring(0, 1)) & 0xf) << 4);
            bytes[index] |= (byte) (HEX_TEXT.indexOf(hex.substring(1, 2)) & 0xf);
            index++;
        }
        return bytes;
    }

    /**
     * 由整数转换成指定长度的二进制数组。
     *
     * @param value  整数数值。
     * @param length 长度。
     * @return 转换后的二进制数组。
     */
    public static final byte[] toBytes(int value, int length) {
        return toBytes((long) value, length);
    }

    /**
     * 由长整数转换成指定长度的二进制数组。
     *
     * @param value  整数数值。
     * @param length 长度。
     * @return 转换后的二进制数组。
     */
    public static byte[] toBytes(long value, int length) {
        byte[] bytes = new byte[length];
        for (int i = 0; i < length; i++)
            bytes[i] = (byte) ((value >>> (8 * (length - 1 - i))) & 0xff);
        return bytes;
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param value 字节数据。
     * @return 转换后的字串。
     */
    public static final String toHex(byte value) {
        StringBuffer info = new StringBuffer(2);
        toHex(info, value);
        return info.toString();
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param value 字节数据。
     * @return 转换后的字串。
     */
    public static final String toHex(byte[] bytes) {
        return toHex(bytes, 0);
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param bytes      字节数据。
     * @param changeLine 换行码数。
     * @return 转换后的字串。
     */
    public static final String toHex(byte[] bytes, int changeLine) {
        return toHex(bytes, changeLine, HEX_SPACE);
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param bytes      字节数据。
     * @param changeLine 换行码数。
     * @param space      字符间隔。
     * @return 转换后的字串。
     */
    public static final String toHex(byte[] bytes, int changeLine, String space) {
        StringBuffer info = new StringBuffer(2);
        int count = 0;
        for (byte value : bytes) {
            if (count > 0) {
                if (changeLine > 0 && count > changeLine) {
                    count = 0;
                    info.append(Environment.LINE_SEPARATOR);
                } else if (space != null)
                    info.append(space);
            }
            toHex(info, value);
            count++;
        }
        return info.toString();
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param bytes 字节数据。
     * @param space 字符间隔。
     * @return 转换后的字串。
     */
    public static final String toHex(byte[] bytes, String space) {
        return toHex(bytes, 0, space);
    }

    /**
     * 转换字节为16进制表达字串。
     *
     * @param value 字节数据。
     * @return 转换后的字串。
     */
    private static void toHex(StringBuffer info, byte value) {
        info.append(HEX_TEXT.charAt(0xf & value >>> 4));
        info.append(HEX_TEXT.charAt(value & 0xf));
    }
}
