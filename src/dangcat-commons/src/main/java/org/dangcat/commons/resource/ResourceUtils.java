package org.dangcat.commons.resource;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourceUtils {
    public static Map<Integer, String> createValueMap(String valueText) {
        Map<Integer, String> valueMap = null;
        if (!ValueUtils.isEmpty(valueText)) {
            String[] options = valueText.split(";");
            if (options != null && options.length > 0) {
                valueMap = new HashMap<Integer, String>();
                for (String option : options) {
                    if (!ValueUtils.isEmpty(option)) {
                        String[] valuePaire = option.split(":");
                        if (valuePaire != null && valuePaire.length > 1) {
                            Integer key = ValueUtils.parseInt(valuePaire[0]);
                            if (key != null && !ValueUtils.isEmpty(valuePaire[1]))
                                valueMap.put(key, valuePaire[1].trim());
                        }
                    }
                }
            }
        }
        return valueMap;
    }

    public static Object getObject(Class<?> classType, Locale locale, String key) {
        if (classType == null || Object.class.equals(classType))
            return null;

        if (locale == null)
            locale = Environment.getCurrentLocale();

        ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(classType);
        String resourceKey = classType.getSimpleName();
        if (!ValueUtils.isEmpty(key))
            resourceKey += "." + key;
        Object value = resourceReader.getObject(locale, resourceKey);
        if (value == null)
            value = resourceReader.getObject(locale, key);
        if (value == null)
            value = getObject(classType.getSuperclass(), locale, key);
        return value;
    }

    public static Object getObject(Class<?> classType, String key) {
        return getObject(classType, null, key);
    }

    public static String getText(Class<?> classType, Locale locale, String key, Object... params) {
        if (classType == null || Object.class.equals(classType))
            return null;

        if (locale == null)
            locale = Environment.getCurrentLocale();

        ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(classType);
        String resourceKey = classType.getSimpleName();
        if (!ValueUtils.isEmpty(key))
            resourceKey += "." + key;
        String text = resourceReader.getText(locale, resourceKey, params);
        if (text == resourceKey || ValueUtils.isEmpty(text) && !ValueUtils.isEmpty(key))
            text = resourceReader.getText(locale, key, params);
        if (text == key || ValueUtils.isEmpty(text))
            text = getText(classType.getSuperclass(), locale, key);
        return text;
    }

    public static String getText(Class<?> classType, String key, Object... params) {
        return getText(classType, null, key, params);
    }

    public static Map<Integer, String> getValueMap(Class<?> classType, Locale locale, String key) {
        if (classType == null || Object.class.equals(classType))
            return null;

        if (locale == null)
            locale = Environment.getCurrentLocale();

        ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(classType);
        String resourceKey = classType.getSimpleName();
        if (!ValueUtils.isEmpty(key))
            resourceKey += "." + key;
        Map<Integer, String> valueMap = resourceReader.getValueMap(locale, resourceKey);
        if (valueMap == null)
            valueMap = resourceReader.getValueMap(locale, key);
        if (valueMap == null)
            valueMap = getValueMap(classType.getSuperclass(), locale, key);
        return valueMap;
    }

    public static Map<Integer, String> getValueMap(Class<?> classType, String key) {
        return getValueMap(classType, null, key);
    }
}
