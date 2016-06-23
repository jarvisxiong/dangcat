package org.dangcat.net.rfc.attribute;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.AttributeTemplateManager;
import org.dangcat.net.rfc.template.VendorAttributeTemplateManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 属性集合。
 * @author dangcat
 * 
 */
public class AttributeCollection extends ArrayList<AttributeData>
{
    protected static final Logger logger = Logger.getLogger(AttributeCollection.class);
    private static final long serialVersionUID = 1L;
    /** 解析后的字节数组。 */
    private byte[] attributeBytes = null;
    /** 属性变化通知。 */
    private NotifyAttributeChanged notifyAttributeChanged = null;
    /** 厂商属性模板管理。 */
    private VendorAttributeTemplateManager vendorAttributeTemplateManager = null;

    @Override
    public boolean add(AttributeData attributeData)
    {
        attributeData.setParent(this);
        this.onAttributeChanged(attributeData);
        return super.add(attributeData);
    }

    /**
     * 添加属性。
     * @param type 属性类型。
     * @param value 属性值。
     * @throws ProtocolParseException
     */
    public boolean addAttribute(Integer type, Object value) throws ProtocolParseException
    {
        AttributeTemplate attributeTemplate = this.getVendorAttributeTemplateManager().getAttributeTemplate(type);
        if (value == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_TYPEERROR, attributeTemplate.getFullName());
        AttributeData attributeData = attributeTemplate.createAttribute(value);
        return this.add(attributeData);
    }

    /**
     * 比较属性集合。
     */
    public int compare(AttributeCollection attributeCollection)
    {
        if (attributeCollection == null)
            return 1;

        int result = this.getVendorId() - attributeCollection.getVendorId();
        if (result != 0)
            return result;

        result = this.size() - attributeCollection.size();
        if (result != 0)
            return result;

        for (AttributeData srcAttributeData : this)
        {
            List<AttributeData> attributeDataList = attributeCollection.findAttributes(srcAttributeData.getType());
            if (attributeDataList == null)
                return 1;

            for (AttributeData dstAttributeData : attributeDataList)
            {
                if (srcAttributeData == dstAttributeData)
                    break;
                result = srcAttributeData.compareTo(dstAttributeData);
                if (result == 0)
                    break;
            }
        }
        return 0;
    }

    /**
     * 找到指定类型的属性对象。
     * @param type 属性类型。
     */
    public AttributeData findAttribute(Integer type)
    {
        AttributeData findAttributeData = null;
        for (AttributeData attributeData : this)
        {
            if (attributeData.getType().equals(type))
            {
                findAttributeData = attributeData;
                break;
            }
        }
        return findAttributeData;
    }

    /**
     * 找到指定类型的属性对象。
     * @param vendorId 厂商编号。
     * @param type 属性类型。
     */
    public List<AttributeData> findAttributes(Integer type)
    {
        List<AttributeData> attributeDataList = null;
        for (AttributeData attributeData : this)
        {
            if (attributeData.getType().equals(type))
            {
                if (attributeDataList == null)
                    attributeDataList = new ArrayList<AttributeData>();
                attributeDataList.add(attributeData);
            }
        }
        return attributeDataList;
    }

    /**
     * 根据厂商类型分类。
     * @return 按厂商类型分类。
     */
    public Map<Integer, List<AttributeData>> getAttributeDataMap()
    {
        List<AttributeData> attributeDataList = null;
        Map<Integer, List<AttributeData>> attributeDataMap = new HashMap<Integer, List<AttributeData>>();
        for (AttributeData attributeData : this)
        {
            if (attributeData instanceof VendorAttribute)
            {
                VendorAttribute vendorAttribute = (VendorAttribute) attributeData;
                Map<Integer, List<AttributeData>> vendorAttributeDataMap = vendorAttribute.getAttributeCollection().getAttributeDataMap();
                for (Entry<Integer, List<AttributeData>> entry : vendorAttributeDataMap.entrySet())
                {
                    List<AttributeData> entryAttributeDataList = attributeDataMap.get(entry.getKey());
                    if (entryAttributeDataList == null)
                    {
                        entryAttributeDataList = new ArrayList<AttributeData>();
                        attributeDataMap.put(entry.getKey(), entryAttributeDataList);
                    }
                    entryAttributeDataList.addAll(entry.getValue());
                }
            }
            else
            {
                if (attributeDataList == null)
                {
                    attributeDataList = new ArrayList<AttributeData>();
                    attributeDataMap.put(this.getVendorId(), attributeDataList);
                }
                attributeDataList.add(attributeData);
            }
        }
        return attributeDataMap;
    }

    /**
     * 转换成字节数组。
     * @param outputStream 输出流对象。
     * @param AttributeData 属性值。
     * @return 转换后的字节。
     * @throws IOException
     */
    public byte[] getAttributesBytes() throws IOException
    {
        if (this.attributeBytes == null)
        {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            for (AttributeData attributeData : this)
            {
                if (!attributeData.isEmpty())
                    outputStream.write(attributeData.toBytes());
            }
            this.attributeBytes = outputStream.toByteArray();
        }
        return this.attributeBytes;
    }

    protected NotifyAttributeChanged getNotifyAttributeChanged()
    {
        return notifyAttributeChanged;
    }

    protected void setNotifyAttributeChanged(NotifyAttributeChanged notifyAttributeChanged) {
        this.notifyAttributeChanged = notifyAttributeChanged;
    }

    public VendorAttributeTemplateManager getVendorAttributeTemplateManager()
    {
        return this.vendorAttributeTemplateManager;
    }

    public void setVendorAttributeTemplateManager(VendorAttributeTemplateManager vendorAttributeTemplateManager) {
        this.vendorAttributeTemplateManager = vendorAttributeTemplateManager;
    }

    public Integer getVendorId()
    {
        return this.getVendorAttributeTemplateManager().getVendorId();
    }

    @Override
    public int indexOf(Object o)
    {
        if (o instanceof Integer)
        {
            for (int index = 0; index < this.size(); index++)
            {
                AttributeData attributeData = this.get(index);
                if (attributeData.getType().equals(o))
                    return index;
            }
        }
        return super.indexOf(o);
    }

    /**
     * 是否是厂商属性。
     */
    public boolean isVendor()
    {
        return this.getVendorId() != AttributeTemplateManager.DEFAULT_VENDORID;
    }

    public void onAttributeChanged(AttributeData attributeData)
    {
        this.attributeBytes = null;
        if (this.notifyAttributeChanged != null)
            this.notifyAttributeChanged.onAttributeChanged(this);
    }

    /**
     * 由报文解析属性对象。
     * @throws ProtocolParseException
     */
    public void parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException
    {
        int index = 0;
        while (index < length)
        {
            // 属性类型。
            int attributeType = bytes[beginIndex++];
            // 属性长度。
            int attributeLength = bytes[beginIndex++];
            AttributeTemplate attributeTemplate = this.getVendorAttributeTemplateManager().getAttributeTemplate(attributeType);
            if (attributeTemplate != null)
            {
                if (attributeLength <= AttributeTemplate.OFFSET)
                    throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_ERROR, attributeTemplate.getFullName());

                AttributeData attributeData = attributeTemplate.parse(bytes, beginIndex, attributeLength - AttributeTemplate.OFFSET);
                attributeData.setLength(attributeLength);
                this.add(attributeData);
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.warn("The Attribute " + attributeType + " is not found.");
            }
            beginIndex += attributeLength - AttributeTemplate.OFFSET;
            index += attributeLength;
        }
    }

    @Override
    public AttributeData remove(int index)
    {
        this.onAttributeChanged(this.get(index));
        return super.remove(index);
    }

    @Override
    public boolean remove(Object attributeData)
    {
        this.onAttributeChanged((AttributeData) attributeData);
        return super.remove(attributeData);
    }

    /**
     * 删除指定类型的属性对象。
     * @param type 属性类型。
     */
    public int removeAttribute(Integer type)
    {
        int result = 0;
        for (int index = this.size() - 1; index >= 0; index--)
        {
            AttributeData attributeData = this.get(index);
            if (attributeData.getType().equals(type))
            {
                this.remove(index);
                result++;
            }
        }
        return result;
    }

    /**
     * 删除属性对象。
     * @param attributeDatas 属性对象。
     */
    public void removeAttributes(AttributeData[] attributeDatas)
    {
        for (AttributeData destAttributeData : attributeDatas)
        {
            this.remove(destAttributeData);
            for (AttributeData attributeData : this)
            {
                if (attributeData instanceof VendorAttribute)
                {
                    VendorAttribute vendorAttribute = (VendorAttribute) attributeData;
                    vendorAttribute.getAttributeCollection().remove(destAttributeData);
                }
            }
        }
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        for (AttributeData attributeData : this)
        {
            if (this.size() > 1)
            {
                info.append(Environment.LINETAB_SEPARATOR);
                if (this.isVendor())
                    info.append("\t");
            }
            info.append(attributeData);
        }
        return info.toString();
    }
}
