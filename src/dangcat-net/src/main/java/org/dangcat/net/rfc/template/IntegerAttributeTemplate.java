package org.dangcat.net.rfc.template;

import org.dangcat.commons.utils.ParseUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 整数类型属性模板。
 *
 * @author dangcat
 */
public class IntegerAttributeTemplate extends NumberAttributeTemplate<Integer> {
    private static final int INTEGER_ATTRIBUTE_LENGTH = 4;

    /**
     * 在本基线模板上建立新的属性对象。
     *
     * @param value 属性值。
     * @return 属性对象。
     * @throws ProtocolParseException
     */
    @Override
    public AttributeData createAttribute(Object value) throws ProtocolParseException {
        Integer intValue = null;
        if (value instanceof String)
            intValue = ValueUtils.parseInt((String) value);
        else if (value instanceof Integer)
            intValue = (Integer) value;
        if (intValue == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_TYPEERROR, this.getFullName());
        return super.createAttribute(intValue);
    }

    /**
     * 属性类型。
     */
    @Override
    public AttributeDataType getDataType() {
        return AttributeDataType.integer;
    }

    @Override
    public int getMaxLength() {
        return INTEGER_ATTRIBUTE_LENGTH;
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
        int value = attributeData.getValue();
        byte[] bytes = ParseUtils.toBytes(value, this.getLength());
        outputStream.write(bytes);
    }

    /**
     * 由报文解析属性对象。
     *
     * @throws ProtocolParseException
     */
    @Override
    public AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException {
        int value = ParseUtils.getInt(bytes, beginIndex, length);
        return this.createAttribute(value);
    }
}
