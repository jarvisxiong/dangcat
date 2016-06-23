package org.dangcat.commons.utils;

import org.dangcat.commons.formator.DateFormator;
import org.dangcat.commons.formator.DateType;

import java.lang.reflect.Array;
import java.util.Date;

/**
 * 值转换工具。
 */
public class ValueUtils {
    /**
     * 比较两个对象的大小。
     *
     * @param from 来源对象。
     * @param to   目标对象。
     * @return 比较大小。
     */
    public static int compare(Object from, Object to) {
        return ValueCompare.compare(from, to);
    }

    /**
     * 是否是布尔类型。
     */
    public static boolean isBoolean(Class<?> classType) {
        if (classType == null)
            return false;
        return boolean.class.equals(classType) || Boolean.class.equals(classType);
    }

    /**
     * 判断数值是否为空。
     *
     * @param value 数值对象。
     * @return 判断结果。
     */
    public static boolean isEmpty(Object value) {
        if (value == null)
            return true;
        if (value instanceof String) {
            String text = (String) value;
            return text.isEmpty() || text.trim().isEmpty();
        }
        if (value.getClass().isArray())
            return Array.getLength(value) == 0;
        return false;
    }

    /**
     * 是否是数字类型。
     */
    public static boolean isNumber(Class<?> classType) {
        if (classType == null)
            return false;
        return Number.class.isAssignableFrom(classType) || double.class.equals(classType) || short.class.equals(classType) || int.class.equals(classType) || float.class.equals(classType);
    }

    /**
     * 是否是字串类型。
     */
    public static boolean isText(Class<?> classType) {
        if (classType == null)
            return false;
        return String.class.equals(classType) || char[].class.equals(classType) || Character[].class.equals(classType) || byte[].class.equals(classType) || Byte[].class.equals(classType);
    }

    /**
     * 判断浮点数是否为0。
     *
     * @param value 浮点值。
     * @return 判断结果。
     */
    public static boolean isZero(double value) {
        return value == 0.0 || Math.abs(value) < 0.00001;
    }

    /**
     * 连接字符串数组。
     *
     * @param values 字符串数组。
     * @return 连接后的字串。
     */
    public static String join(String... values) {
        return join(values, ";");
    }

    /**
     * 连接字符串数组。
     *
     * @param values   字符串数组。
     * @param joinText 连接字符。
     * @return 连接后的字串。
     */
    public static String join(String[] values, String joinText) {
        String result = null;
        if (values != null && values.length > 0) {
            StringBuilder value = new StringBuilder();
            for (String text : values) {
                if (value.length() > 0)
                    value.append(joinText);
                value.append(text);
            }
            result = value.toString();
        }
        return result;
    }

    /**
     * 由字串解析布尔。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Boolean parseBoolean(String text) {
        return parseBoolean(text, false);
    }

    /**
     * 由字串解析布尔。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Boolean parseBoolean(String text, Boolean defaultValue) {
        return TextParser.parseBoolean(text, defaultValue);
    }

    /**
     * 由字串解析日期。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Date parseDate(String text) {
        return parseDate(text, null);
    }

    /**
     * 由字串解析日期。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Date parseDate(String text, Date defaultValue) {
        return DateUtils.parse(text, defaultValue);
    }

    /**
     * 由字串解析浮点数。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Double parseDouble(String text) {
        return parseDouble(text, null);
    }

    /**
     * 由字串解析浮点数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Double parseDouble(String text, Double defaultValue) {
        return TextParser.parseDouble(text, defaultValue);
    }

    /**
     * 解析枚举类型。
     *
     * @param <T>
     * @param classType 枚举类型。
     * @param text      字串。
     * @return 解析后的对象。
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseEnum(Class<?> classType, String text) {
        return (T) TextParser.parseEnum(classType, text);
    }

    /**
     * 由枚举值解析枚举类型。
     *
     * @param <T>
     * @param classType 枚举类型。
     * @param value     枚举值。
     * @return 解析后的对象。
     */
    @SuppressWarnings("unchecked")
    public static <T> T parseEnum(Class<T> classType, int value) {
        return (T) TextParser.parseEnum(classType, value);
    }

    /**
     * 由字串解析整数。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Integer parseInt(String text) {
        return parseInt(text, null);
    }

    /**
     * 由字串解析整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Integer parseInt(String text, Integer defaultValue) {
        return TextParser.parseInt(text, defaultValue);
    }

    /**
     * 由字串解析长整数。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Long parseLong(String text) {
        return parseLong(text, null);
    }

    /**
     * 由字串解析长整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Long parseLong(String text, Long defaultValue) {
        return TextParser.parseLong(text, defaultValue);
    }

    /**
     * 由字串解析短整数。
     *
     * @param text 字串文字。
     * @return 返回值。
     */
    public static Short parseShort(String text) {
        return parseShort(text, null);
    }

    /**
     * 由字串解析短整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    public static Short parseShort(String text, Short defaultValue) {
        return TextParser.parseShort(text, defaultValue);
    }

    /**
     * 文字值转换成实际值对象。
     */
    public static Object parseValue(Class<?> classType, String value) {
        return TextParser.parseValue(classType, value);
    }

    /**
     * 数值转换成字串值转。
     */
    public static String toString(Object value) {
        String text = null;
        try {
            if (value == null)
                return text;

            Class<?> classType = value.getClass();
            if (String.class.equals(classType))
                text = (String) value;
            else if (Character[].class.equals(classType) || char[].class.equals(classType))
                text = new String((char[]) value);
            else if (Date.class.isAssignableFrom(classType))
                text = DateFormator.getDateFormator(DateType.Full).format(value);
            else if (byte[].class.equals(classType))
                text = ParseUtils.toHex((byte[]) value);
            else
                text = value.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return text;
    }
}
