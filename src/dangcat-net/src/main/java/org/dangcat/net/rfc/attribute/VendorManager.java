package org.dangcat.net.rfc.attribute;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.rfc.template.AttributeTemplateManager;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 厂商管理。
 * @author dangcat
 * 
 */
public class VendorManager
{
    private static Map<Integer, String> vendorMap = new HashMap<Integer, String>();

    static
    {
        add(AttributeTemplateManager.DEFAULT_VENDORID, "RFC");
    }

    public static void add(Integer vendorId, String vendorName)
    {
        if (vendorId != null && !ValueUtils.isEmpty(vendorName))
            vendorMap.put(vendorId, vendorName.replace("_", "-"));
    }

    public static Collection<Integer> getAllVendorId()
    {
        return vendorMap.keySet();
    }

    public static String getDescription(Integer id)
    {
        String description = null;
        if (vendorMap.containsKey(id))
            description = vendorMap.get(id) + "(" + id + ")";
        else
            description = id + "";
        return description;
    }

    public static String getName(Integer vendorId)
    {
        return vendorMap.get(vendorId);
    }

    public static Integer parse(String text)
    {
        Integer vendorId = null;
        String[] names = text.split("-");
        if (names != null && names.length >= 2)
            vendorId = ValueUtils.parseInt(names[1]);
        add(vendorId, names[0]);
        return vendorId;
    }
}
