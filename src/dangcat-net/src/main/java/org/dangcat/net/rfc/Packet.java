package org.dangcat.net.rfc;

import org.dangcat.net.rfc.attribute.AttributeCollection;
import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.VendorAttribute;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;
import org.dangcat.net.rfc.template.AttributeTemplate;
import org.dangcat.net.rfc.template.AttributeTemplateManager;
import org.dangcat.net.rfc.template.VendorAttributeTemplateManager;
import org.dangcat.net.rfc.template.rules.PacketRuleValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 协议报文。
 * @author dangcat
 * 
 */
public abstract class Packet extends AttributeCollection implements Comparable<Packet>
{
    private static final long serialVersionUID = 1L;

    /** 解析后的字节数组。 */
    private byte[] bytes = null;

    /**
     * 添加厂商属性。
     * @param vendorId 厂商号。
     * @param type 属性类型。
     * @param value 属性值。
     * @throws ProtocolParseException
     */
    public boolean addVendorAttribute(Integer vendorId, Integer type, Object value) throws ProtocolParseException
    {
        if (vendorId == null || vendorId.equals(AttributeTemplateManager.DEFAULT_VENDORID))
            return this.addAttribute(type, value);

        VendorAttribute vendorAttribute = null;
        AttributeTemplate attributeTemplate = this.getAttributeTemplateManager().getAttributeTemplate(this.getVendorAttributeType());
        if (attributeTemplate != null)
            vendorAttribute = (VendorAttribute) attributeTemplate.createAttribute(vendorId);
        if (vendorAttribute == null)
            return false;
        this.add(vendorAttribute);
        return vendorAttribute.getAttributeCollection().addAttribute(type, value);
    }

    /**
     * 比较两个数据包是否相同。
     */
    @Override
    public int compareTo(Packet packet)
    {
        if (packet == null)
            return 1;

        int result = this.getPacketType() - packet.getPacketType();
        if (result != 0)
            return result;
        result = this.compare(packet);
        if (result != 0)
            return result;
        return 0;
    }

    public byte[] createBytes() throws ProtocolParseException
    {
        this.bytes = this.toBytes();
        return this.bytes;
    }

    /**
     * 查找指定厂商属性和属性号的对象。
     * @param vendorId 厂商编号。
     * @param type 属性类型。
     */
    public AttributeData findVendorAttribute(Integer vendorId, Integer type)
    {
        if (vendorId == null || vendorId == AttributeTemplateManager.DEFAULT_VENDORID)
            return this.findAttribute(type);

        AttributeData findAttributeData = null;
        List<VendorAttribute> vendorAttributeList = this.findVendorAttributes(vendorId);
        if (vendorAttributeList != null)
        {
            for (VendorAttribute vendorAttribute : vendorAttributeList)
            {
                AttributeData attributeData = vendorAttribute.getAttributeCollection().findAttribute(type);
                if (attributeData != null)
                {
                    findAttributeData = attributeData;
                    break;
                }
            }
        }
        return findAttributeData;
    }

    /**
     * 根据指定的厂商号找到厂商属性。
     * @param vendorId 厂商编号。
     */
    private List<VendorAttribute> findVendorAttributes(Integer vendorId)
    {
        List<VendorAttribute> vendorAttributeList = null;
        for (AttributeData attributeData : this)
        {
            if (attributeData instanceof VendorAttribute)
            {
                VendorAttribute vendorAttribute = (VendorAttribute) attributeData;
                if (vendorAttribute.getVendorId().equals(vendorId))
                {
                    if (vendorAttributeList == null)
                        vendorAttributeList = new ArrayList<VendorAttribute>();
                    vendorAttributeList.add(vendorAttribute);
                }
            }
        }
        return vendorAttributeList;
    }

    /**
     * 查找厂商属性。
     * @param vendorId 厂商编号。
     * @param type 属性类型。
     */
    public List<AttributeData> findVendorAttributes(Integer vendorId, Integer type)
    {
        if (vendorId == null || vendorId == AttributeTemplateManager.DEFAULT_VENDORID)
            return this.findAttributes(type);

        List<AttributeData> findAttributeDataList = null;
        List<VendorAttribute> vendorAttributeList = this.findVendorAttributes(vendorId);
        if (vendorAttributeList != null)
        {
            for (VendorAttribute vendorAttribute : vendorAttributeList)
            {
                List<AttributeData> attributeDataList = vendorAttribute.getAttributeCollection().findAttributes(type);
                if (attributeDataList != null)
                {
                    if (findAttributeDataList == null)
                        findAttributeDataList = new ArrayList<AttributeData>();
                    findAttributeDataList.addAll(attributeDataList);
                }
            }
        }
        return findAttributeDataList;
    }

    public AttributeTemplateManager getAttributeTemplateManager()
    {
        return this.getPacketMetaInfo().getAttributeTemplateManager();
    }

    public byte[] getBytes() throws ProtocolParseException
    {
        if (this.bytes == null)
            this.bytes = this.toBytes();
        return this.bytes;
    }

    public abstract PacketMetaInfo getPacketMetaInfo();

    public String getPacketName()
    {
        return this.getPacketMetaInfo().getPacketName(this.getPacketType());
    }

    public abstract Integer getPacketType();

    @Override
    public VendorAttributeTemplateManager getVendorAttributeTemplateManager()
    {
        if (super.getVendorAttributeTemplateManager() == null)
        {
            VendorAttributeTemplateManager vendorAttributeTemplateManager = this.getAttributeTemplateManager().getVendorAttributeTemplateManager(null);
            if (vendorAttributeTemplateManager != null)
                this.setVendorAttributeTemplateManager(vendorAttributeTemplateManager);
        }
        return super.getVendorAttributeTemplateManager();
    }

    /**
     * 读取厂商属性号。
     * @param radiusAttributeType 属性类型。
     * @param value 属性值。
     */
    public abstract Integer getVendorAttributeType();

    @Override
    public void onAttributeChanged(AttributeData attributeData)
    {
        this.bytes = null;
        super.onAttributeChanged(attributeData);
    }

    /**
     * 删除厂商属性。
     * @param vendorId 厂商号。
     * @param type 属性类型。
     */
    public int removeVendorAttribute(Integer vendorId, Integer type)
    {
        if (vendorId == null || vendorId == AttributeTemplateManager.DEFAULT_VENDORID)
            return this.removeAttribute(type);

        int result = 0;
        List<VendorAttribute> vendorAttributeList = this.findVendorAttributes(vendorId);
        if (vendorAttributeList != null)
        {
            for (VendorAttribute vendorAttribute : vendorAttributeList)
                result += vendorAttribute.getAttributeCollection().removeAttribute(type);
        }
        return result;
    }

    protected abstract byte[] toBytes() throws ProtocolParseException;

    /**
     * 验证包是否争取。
     * @throws ProtocolValidateException 验证异常。
     */
    public void validate() throws ProtocolValidateException
    {
        this.validateAttributeData();
        this.validatePacketRule();
    }

    public void validateAttributeData() throws ProtocolValidateException
    {
        Map<Integer, List<AttributeData>> attributeDataMap = this.getAttributeDataMap();
        for (Integer vendorId : attributeDataMap.keySet())
        {
            List<AttributeData> attributeDataList = attributeDataMap.get(vendorId);
            // 基本校验。
            for (AttributeData attributeData : attributeDataList)
                attributeData.validate();
        }
    }

    public void validatePacketRule() throws ProtocolValidateException
    {
        AttributeTemplateManager attributeTemplateManager = this.getAttributeTemplateManager();
        Map<Integer, List<AttributeData>> attributeDataMap = this.getAttributeDataMap();
        for (Integer vendorId : attributeDataMap.keySet())
        {
            List<AttributeData> attributeDataList = attributeDataMap.get(vendorId);
            // 规则校验。
            PacketRuleValidator packetRuleValidator = attributeTemplateManager.getPacketRuleValidator(vendorId, this.getPacketName());
            if (packetRuleValidator != null)
                packetRuleValidator.validate(attributeDataList);
        }
    }
}
