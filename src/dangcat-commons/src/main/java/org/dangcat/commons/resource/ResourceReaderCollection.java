package org.dangcat.commons.resource;

import org.dangcat.commons.utils.ValueUtils;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;

public class ResourceReaderCollection extends ResourceReader
{
    private Collection<ResourceReader> resourceReaderCollection = new LinkedHashSet<ResourceReader>();

    public ResourceReaderCollection(Collection<Class<?>> classTypeCollection)
    {
        for (Class<?> classType : classTypeCollection)
        {
            ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(classType);
            if (resourceReader != null)
                this.resourceReaderCollection.add(resourceReader);
        }
    }

    @Override
    public Object getObject(Locale locale, String key)
    {
        Object value = null;
        for (ResourceReader resourceReader : this.resourceReaderCollection)
        {
            value = resourceReader.getObject(locale, key);
            if (value != null)
                break;
        }
        return value;
    }

    @Override
    public String getText(Locale locale, String key, Object... params)
    {
        String text = null;
        for (ResourceReader resourceReader : this.resourceReaderCollection)
        {
            text = resourceReader.getText(locale, key, params);
            if (!ValueUtils.isEmpty(text) && !text.equals(key))
                break;
        }
        return text;
    }

    @Override
    public Map<Integer, String> getValueMap(Locale locale, String key)
    {
        Map<Integer, String> valueMap = null;
        for (ResourceReader resourceReader : this.resourceReaderCollection)
        {
            valueMap = resourceReader.getValueMap(key);
            if (valueMap != null)
                break;
        }
        return valueMap;
    }
}
