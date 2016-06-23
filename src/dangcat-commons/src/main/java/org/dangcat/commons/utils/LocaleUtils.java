package org.dangcat.commons.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocaleUtils {
    private static Map<String, Locale> LocaleMap = new HashMap<String, Locale>();

    static {
        LocaleMap.put("zh_CN", Locale.SIMPLIFIED_CHINESE);
        LocaleMap.put("en_US", Locale.US);
    }

    public static Locale getDefaultLocale() {
        if (Locale.getDefault().equals(Locale.SIMPLIFIED_CHINESE))
            return Locale.SIMPLIFIED_CHINESE;
        return Locale.US;
    }

    public static Locale parse(String language) {
        Locale locale = LocaleMap.get(language);
        return locale == null ? getDefaultLocale() : locale;
    }
}