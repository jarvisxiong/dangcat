package org.dangcat.net.rfc.template;

import org.apache.log4j.Logger;
import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 属性模板。
 *
 * @author dangcat
 */
public abstract class AttributeTemplate {
    public static final int OFFSET = 2;
    protected static final Logger logger = Logger.getLogger(AttributeTemplate.class);
    /**
     * 是否加密。
     */
    private boolean encrypt = false;
    /**
     * 属性名称。
     */
    private String name;
    /**
     * 参数列表。
     */
    private Map<String, Object> params = null;
    /**
     * 属性编号。
     */
    private Integer type;
    /**
     * 属性模板管理。
     */
    private VendorAttributeTemplateManager vendorAttributeTemplateManager = null;

    /**
     * 产生属性模板。
     *
     * @param dataType 数据类型。
     * @return 产生的属性模板。
     */
    public static AttributeTemplate createInstance(AttributeDataType dataType) {
        AttributeTemplate attributeTemplate = null;
        if (AttributeDataType.address.equals(dataType))
            attributeTemplate = new AddressAttributeTemplate();
        else if (AttributeDataType.integer.equals(dataType))
            attributeTemplate = new IntegerAttributeTemplate();
        else if (AttributeDataType.bigInteger.equals(dataType))
            attributeTemplate = new BigIntegerAttributeTemplate();
        else if (AttributeDataType.string.equals(dataType))
            attributeTemplate = new StringAttributeTemplate();
        else if (AttributeDataType.text.equals(dataType))
            attributeTemplate = new TextAttributeTemplate();
        else if (AttributeDataType.time.equals(dataType))
            attributeTemplate = new TimeAttributeTemplate();
        else if (AttributeDataType.vendor.equals(dataType))
            attributeTemplate = new VendorAttributeTemplate();
        return attributeTemplate;
    }

    /**
     * 在本基线模板上建立新的属性对象。
     *
     * @param value 属性值。
     * @return 属性对象。
     * @throws ProtocolValidateException
     */
    public AttributeData createAttribute(Object value) throws ProtocolParseException {
        if (value == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_ERROR, this.getFullName());
        return value == null ? null : new AttributeData(this, value);
    }

    /**
     * 属性类型。
     */
    public abstract AttributeDataType getDataType();

    /**
     * 属性全名称。
     */
    public String getFullName() {
        return this.name + "(" + this.type + ")";
    }

    /**
     * 属性最大长度。
     */
    public int getMaxLength() {
        return -1;
    }

    /**
     * 属性名称。
     */
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Object> getParams() {
        if (this.params == null)
            this.params = new HashMap<String, Object>();
        return this.params;
    }

    /**
     * 属性编号。
     */
    public Integer getType() {
        return this.type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public VendorAttributeTemplateManager getVendorAttributeTemplateManager() {
        return vendorAttributeTemplateManager;
    }

    protected void setVendorAttributeTemplateManager(VendorAttributeTemplateManager vendorAttributeTemplateManager) {
        this.vendorAttributeTemplateManager = vendorAttributeTemplateManager;
    }

    public boolean isEncrypt() {
        return encrypt;
    }

    public void setEncrypt(boolean encrypt) {
        this.encrypt = encrypt;
    }

    /**
     * 转换成字节数组。
     *
     * @param outputStream 输出流对象。
     * @param attribute    属性值。
     * @return 转换后的字节。
     */
    protected abstract void outputValue(ByteArrayOutputStream outputStream, AttributeData attribute) throws IOException;

    /**
     * 解析属性。
     *
     * @param bytes      原始数据。
     * @param beginIndex 起始索引位置。
     * @param length     长度。
     * @return 属性对象。
     */
    public abstract AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException;

    /**
     * 转换成字节数组。
     *
     * @param attributeData 属性值。
     * @return 转换后的字节。
     */
    public byte[] toBytes(AttributeData attributeData) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] header = new byte[2];
            header[0] = this.getType().byteValue();
            header[1] = (byte) 0;
            outputStream.write(header);
            this.outputValue(outputStream, attributeData);
            bytes = outputStream.toByteArray();
            bytes[1] = (byte) bytes.length;
        } catch (Exception e) {
        }
        return bytes;
    }

    public String toString(Object value) {
        return value.toString();
    }

    /**
     * 验证属性是否有效。
     */
    public void validate(Object value) throws ProtocolValidateException {
        if (value == null)
            throw new ProtocolValidateException(ProtocolValidateException.ATTRIBUTE_ISNULL, this.getFullName());
    }
}
