package org.dangcat.net.rfc.template;

import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 字串类型属性模板。
 * @author dangcat
 * 
 */
public class TextAttributeTemplate extends AttributeTemplate
{
    private static final int TEXT_MAXLENGTH = 253;

    /**
     * 在本基线模板上建立新的属性对象。
     * @param value 属性值。
     * @return 属性对象。
     * @throws ProtocolParseException
     */
    public AttributeData createAttribute(Object value) throws ProtocolParseException
    {
        String text = null;
        if (value instanceof String)
            text = (String) value;
        if (text == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_TYPEERROR, this.getFullName());
        return super.createAttribute(text);
    }

    /**
     * 属性类型。
     */
    @Override
    public AttributeDataType getDataType()
    {
        return AttributeDataType.text;
    }

    @Override
    public int getMaxLength()
    {
        return TEXT_MAXLENGTH;
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
        String textValue = attributeData.getValue();
        byte[] bytes = textValue.getBytes();
        outputStream.write(bytes, 0, bytes.length > TEXT_MAXLENGTH ? TEXT_MAXLENGTH : bytes.length);
    }

    /**
     * 由报文解析属性对象。
     * @throws ProtocolParseException
     */
    @Override
    public AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException
    {
        byte[] value = new byte[length];
        System.arraycopy(bytes, beginIndex, value, 0, value.length);
        return this.createAttribute(new String(value));
    }

    @Override
    public void validate(Object value) throws ProtocolValidateException
    {
        super.validate(value);

        String textValue = (String) value;
        byte[] bytes = textValue.getBytes();
        if (bytes == null || bytes.length == 0 || bytes.length > TEXT_MAXLENGTH)
            throw new ProtocolValidateException(ProtocolValidateException.ATTRIBUTE_INVALID_LENGTH, this.getFullName(), bytes.length, 1, this.getMaxLength());
    }
}
