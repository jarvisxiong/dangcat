package org.dangcat.net.rfc;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.dangcat.net.rfc.template.AttributeTemplateManager;

/**
 * 协议包元数据。
 * @author dangcat
 * 
 */
public class PacketMetaInfo
{
    private static final String FILENAME_POSTFIX = ".xml";
    private static final String TEXT_VALUE = "value";
    private AttributeTemplateManager attributeTemplateManager = null;
    private String fileNamePrefix = null;
    private PacketAttributeManager packetAttributeManager = null;
    private Class<?> packetTypeClass = null;
    private Map<Integer, String> packetTypeMap = null;

    public PacketMetaInfo(Class<?> packetTypeClass, String fileNamePrefix)
    {
        this.packetTypeClass = packetTypeClass;
        this.fileNamePrefix = fileNamePrefix;
    }

    /**
     * 加载属性模板。
     */
    public AttributeTemplateManager getAttributeTemplateManager()
    {
        if (attributeTemplateManager == null)
        {
            synchronized (AttributeTemplateManager.class)
            {
                if (attributeTemplateManager == null)
                {
                    AttributeTemplateManager attributeTemplateManager = new AttributeTemplateManager(this);
                    attributeTemplateManager.initialize();
                    this.attributeTemplateManager = attributeTemplateManager;
                }
            }
        }
        return attributeTemplateManager;
    }

    public String getFileNamePostfix()
    {
        return FILENAME_POSTFIX;
    }

    public String getFileNamePrefix()
    {
        return fileNamePrefix;
    }

    public PacketAttributeManager getPacketAttributeManager()
    {
        if (this.packetAttributeManager == null)
            this.packetAttributeManager = new PacketAttributeManager(this);
        return this.packetAttributeManager;
    }

    /**
     * 根据包的ID读取名字。
     */
    public String getPacketName(Integer type)
    {
        return this.getPacketTypeMap().get(type);
    }

    /**
     * 根据包的名字读取ID。
     */
    public Integer getPacketType(String name)
    {
        Integer packTypeId = null;
        for (Entry<Integer, String> entry : this.getPacketTypeMap().entrySet())
        {
            if (entry.getValue().equalsIgnoreCase(name))
            {
                packTypeId = entry.getKey();
                break;
            }
        }
        return packTypeId;
    }

    public Class<?> getPacketTypeClass()
    {
        return packetTypeClass;
    }

    private Map<Integer, String> getPacketTypeMap()
    {
        if (this.packetTypeMap == null)
        {
            this.packetTypeMap = new HashMap<Integer, String>();
            if (this.packetTypeClass.isEnum())
            {
                Field field = null;
                try
                {
                    field = this.packetTypeClass.getDeclaredField(TEXT_VALUE);
                    field.setAccessible(true);
                }
                catch (Exception e)
                {
                }
                Object[] enumArray = this.packetTypeClass.getEnumConstants();
                for (Object instance : enumArray)
                {
                    Enum<?> enumObject = (Enum<?>) instance;
                    Integer value = null;
                    if (field != null)
                    {
                        try
                        {
                            value = (Integer) field.get(instance);
                        }
                        catch (Exception e)
                        {
                        }
                    }
                    else
                        value = enumObject.ordinal();
                    if (value != null)
                        this.packetTypeMap.put(value, enumObject.name().replace("_", "-"));
                }
            }
        }
        return this.packetTypeMap;
    }
}