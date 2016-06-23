package org.dangcat.net.rfc.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.dangcat.commons.utils.ParseUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;

/**
 * 长整数类型属性模板。
 * @author dangcat
 * 
 */
public class BigIntegerAttributeTemplate extends NumberAttributeTemplate<Long>
{
    private static final int BIGINTEGER_ATTRIBUTE_LENGTH = 8;

    /**
     * 在本基线模板上建立新的属性对象。
     * @param value 属性值。
     * @return 属性对象。
     * @throws ProtocolParseException
     */
    public AttributeData createAttribute(Object value) throws ProtocolParseException
    {
        Long longValue = null;
        if (value instanceof String)
            longValue = ValueUtils.parseLong((String) value);
        else if (value instanceof Long)
            longValue = (Long) value;
        if (longValue == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_TYPEERROR, this.getFullName());
        return super.createAttribute(longValue);
    }

    /**
     * 属性类型。
     */
    @Override
    public AttributeDataType getDataType()
    {
        return AttributeDataType.bigInteger;
    }

    @Override
    public int getMaxLength()
    {
        return BIGINTEGER_ATTRIBUTE_LENGTH;
    }

    /**
     * 转换成字节数组。
     * @param outputStream 输出流对象。
     * @param attributeData 属性值。
     * @return 转换后的字节。
     * @throws IOException
     */
    protected void outputValue(ByteArrayOutputStream outputStream, AttributeData attributeData) throws IOException
    {
        long value = (Long) attributeData.getValue();
        byte[] bytes = ParseUtils.toBytes(value, this.getLength());
        outputStream.write(bytes);
    }

    /**
     * 由报文解析属性对象。
     * @throws ProtocolParseException
     */
    @Override
    public AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException
    {
        long value = ParseUtils.getLong(bytes, beginIndex, length);
        return this.createAttribute(value);
    }
}
