package org.dangcat.commons.utils;

import java.awt.*;
import java.awt.font.TextAttribute;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 值转换工具。
 */
class TextParser {
    private static final String TEXT_TRUE = "true";
    private static final String TEXT_VALUE = "value";

    private static String checkEmpty(String text) {
        String value = null;
        if (text != null && !text.isEmpty()) {
            value = text.trim();
            if (value.isEmpty())
                value = null;
        }
        return value;
    }

    /**
     * 由字串解析布尔。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Boolean parseBoolean(String text, Boolean defaultValue) {
        Boolean value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null)
            value = TEXT_TRUE.equalsIgnoreCase(textValue);
        return value;
    }

    /**
     * 由字串解析浮点数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Double parseDouble(String text, Double defaultValue) {
        Double value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null) {
            try {
                value = Double.parseDouble(toPlainString(textValue));
            } catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 由枚举值解析枚举类型。
     *
     * @param <T>
     * @param classType 枚举类型。
     * @param value     枚举值。
     * @return 解析后的对象。
     */
    protected static Object parseEnum(Class<?> classType, int value) {
        if (classType.isEnum()) {
            Field field = null;
            try {
                field = classType.getDeclaredField(TEXT_VALUE);
                field.setAccessible(true);
            } catch (Exception e) {
            }
            Object[] instanceArray = classType.getEnumConstants();
            for (Object instance : instanceArray) {
                if (field != null) {
                    try {
                        Integer enumValue = (Integer) field.get(instance);
                        if (enumValue != null && enumValue.equals(value))
                            return instance;
                    } catch (Exception e) {
                    }
                } else {
                    Enum<?> enumInstance = (Enum<?>) instance;
                    if (enumInstance.ordinal() == value)
                        return instance;
                }
            }
        }
        return null;
    }

    /**
     * 解析枚举类型。
     *
     * @param <T>
     * @param classType 枚举类型。
     * @param text      字串。
     * @return 解析后的对象。
     */
    protected static Object parseEnum(Class<?> classType, String text) {
        if (classType.isEnum() && text != null) {
            text = text.replace("-", "_");
            Object[] enumArray = classType.getEnumConstants();
            for (Object instance : enumArray) {
                Enum<?> enumObject = (Enum<?>) instance;
                if (enumObject.name().equalsIgnoreCase(text))
                    return enumObject;
            }
        }
        return null;
    }

    /**
     * 由字串解析长整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Font parseFont(String text, Font defaultValue) {
        Font value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null) {
            try {
                String[] values = text.split(",");
                if (values != null && values.length >= 3) {
                    Map<TextAttribute, Object> fontProperties = new HashMap<TextAttribute, Object>();
                    fontProperties.put(TextAttribute.FAMILY, values[0].trim()); // 定义字体名
                    fontProperties.put(TextAttribute.WEIGHT, parseDouble(values[1], null)); // 定义字号
                    fontProperties.put(TextAttribute.SIZE, parseInt(values[2], null)); // 定义字号
                    if (values.length > 3)
                        fontProperties.put(TextAttribute.UNDERLINE, parseInt(values[3], null));
                    if (fontProperties.size() > 0)
                        value = new Font(fontProperties);
                }
            } catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 由字串解析整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Integer parseInt(String text, Integer defaultValue) {
        Integer value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null) {
            try {
                if (textValue.indexOf(".") != -1) {
                    Double doubleValue = parseDouble(textValue, null);
                    if (doubleValue != null)
                        value = doubleValue.intValue();
                } else
                    value = Integer.parseInt(toPlainString(textValue));
            } catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 由字串解析长整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Long parseLong(String text, Long defaultValue) {
        Long value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null) {
            try {
                value = Long.parseLong(toPlainString(textValue));
            } catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 由字串解析短整数。
     *
     * @param text         字串文字。
     * @param defaultValue 默认值。
     * @return 返回值。
     */
    protected static Short parseShort(String text, Short defaultValue) {
        Short value = defaultValue;
        String textValue = checkEmpty(text);
        if (textValue != null) {
            try {
                value = Short.parseShort(toPlainString(textValue));
            } catch (Exception e) {
            }
        }
        return value;
    }

    /**
     * 文字值转换成实际值对象。
     */
    protected static Object parseValue(Class<?> classType, String value) {
        Object result = null;
        try {
            if (classType == null)
                return result;

            String text = checkEmpty(value);
            if (text == null)
                return result;

            if (classType.isEnum())
                result = parseEnum(classType, value);
            else if (String.class.equals(classType))
                result = value;
            else if (Character[].class.equals(classType) || char[].class.equals(classType))
                result = text.toCharArray();
            else if (Integer.class.equals(classType) || int.class.equals(classType))
                result = parseInt(text, null);
            else if (Boolean.class.equals(classType) || boolean.class.equals(classType))
                result = parseBoolean(text, false);
            else if (Short.class.equals(classType) || short.class.equals(classType))
                result = parseShort(text, null);
            else if (Long.class.equals(classType) || long.class.equals(classType))
                result = parseLong(text, null);
            else if (Double.class.equals(classType) || Float.class.equals(classType) || double.class.equals(classType) || float.class.equals(classType))
                result = parseDouble(text, null);
            else if (byte[].class.equals(classType))
                result = ParseUtils.parseHex(text);
            else if (Date.class.isAssignableFrom(classType))
                result = DateUtils.parse(text, null);
            else if (Timestamp.class.equals(classType))
                result = DateUtils.parse(text, null);
            else if (Font.class.equals(classType))
                result = parseFont(text, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String toPlainString(String text) {
        if (text.indexOf('e') != -1 || text.indexOf('E') != -1) {
            BigDecimal value = new BigDecimal(text);
            return value.toPlainString();
        }
        return text;
    }
}
