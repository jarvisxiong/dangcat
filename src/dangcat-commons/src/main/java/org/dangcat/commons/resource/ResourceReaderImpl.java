package org.dangcat.commons.resource;

import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ResourceReaderImpl extends ResourceReader {
    private Class<?> classType = null;
    private Map<Locale, ResourceHelper> localeResourceHelperMap = new HashMap<Locale, ResourceHelper>();

    public ResourceReaderImpl(Class<?> classType) {
        this.classType = classType;
    }

    public Class<?> getClassType() {
        return this.classType;
    }

    @Override
    public Object getObject(Locale locale, String key) {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        return this.getResourceHelper(locale).getObject(key);
    }

    public synchronized ResourceHelper getResourceHelper(Locale locale) {
        ResourceHelper resourceHelper = this.localeResourceHelperMap.get(locale);
        if (resourceHelper == null) {
            resourceHelper = new ResourceHelper(this.getClassType(), locale);
            this.localeResourceHelperMap.put(locale, resourceHelper);
        }
        return resourceHelper;
    }

    @Override
    public String getText(Locale locale, String key, Object... params) {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        String message = this.getResourceHelper(locale).getText(key);
        if (ValueUtils.isEmpty(message))
            return key;
        return this.format(message, params);
    }

    @Override
    public Map<Integer, String> getValueMap(Locale locale, String key) {
        if (locale == null)
            locale = Environment.getDefaultLocale();

        return this.getResourceHelper(locale).getValueMap(key);
    }
}
