package org.dangcat.net.rfc.template;

import org.dangcat.commons.utils.ParseUtils;
import org.dangcat.net.rfc.attribute.AttributeCollection;
import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.attribute.VendorAttribute;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 二进制类型属性模板。
 *
 * @author dangcat
 */
public class VendorAttributeTemplate extends AttributeTemplate {
    private static final int VENDORID_MAXLENGTH = 4;

    /**
     * 在本基线模板上建立新的属性对象。
     *
     * @param value 属性值。
     * @return 属性对象。
     */
    @Override
    public AttributeData createAttribute(Object value) {
        return value == null ? null : new VendorAttribute(this, (Integer) value);
    }

    /**
     * 属性类型。
     */
    @Override
    public AttributeDataType getDataType() {
        return AttributeDataType.string;
    }

    /**
     * 转换成字节数组。
     *
     * @param outputStream  输出流对象。
     * @param attributeData 属性值。
     * @return 转换后的字节。
     * @throws IOException
     */
    protected void outputValue(ByteArrayOutputStream outputStream, AttributeData attributeData) throws IOException {
        VendorAttribute vendorAttribute = (VendorAttribute) attributeData;
        AttributeCollection attributeCollection = vendorAttribute.getAttributeCollection();
        byte[] vendorBytes = ParseUtils.toBytes(attributeCollection.getVendorId(), VENDORID_MAXLENGTH);
        outputStream.write(vendorBytes);
        outputStream.write(attributeCollection.getAttributesBytes());
    }

    /**
     * 由报文解析属性对象。
     *
     * @throws ProtocolParseException
     */
    @Override
    public AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException {
        int vendorId = ParseUtils.getInt(bytes, beginIndex, VENDORID_MAXLENGTH);
        beginIndex += VENDORID_MAXLENGTH;

        VendorAttribute vendorAttribute = new VendorAttribute(this, vendorId);
        AttributeTemplateManager attributeTemplateManager = this.getVendorAttributeTemplateManager().getAttributeTemplateManager();
        VendorAttributeTemplateManager vendorAttributeTemplateManager = attributeTemplateManager.getVendorAttributeTemplateManager(vendorId);
        if (vendorAttributeTemplateManager == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_NOTSUPPORT, this.getFullName());
        vendorAttribute.getAttributeCollection().setVendorAttributeTemplateManager(vendorAttributeTemplateManager);
        vendorAttribute.getAttributeCollection().parse(bytes, beginIndex, length - VENDORID_MAXLENGTH);
        return vendorAttribute;
    }
}
