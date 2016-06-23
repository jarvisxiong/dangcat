package org.dangcat.net.rfc.template;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.dangcat.net.rfc.attribute.AttributeData;
import org.dangcat.net.rfc.attribute.AttributeDataType;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

/**
 * 地址类型属性模板。
 * @author dangcat
 * 
 */
public class AddressAttributeTemplate extends AttributeTemplate
{
    /**
     * 在本基线模板上建立新的属性对象。
     * @param value 属性值。
     * @return 属性对象。
     * @throws ProtocolValidateException
     */
    public AttributeData createAttribute(Object value) throws ProtocolParseException
    {
        InetAddress inetAddress = null;
        if (value instanceof String)
        {
            try
            {
                inetAddress = InetAddress.getByName((String) value);
            }
            catch (UnknownHostException e)
            {
            }
        }
        else if (value instanceof InetAddress)
            inetAddress = (InetAddress) value;
        if (inetAddress == null)
            throw new ProtocolParseException(ProtocolParseException.ATTRIBUTE_TYPEERROR, this.getFullName());
        return super.createAttribute(inetAddress);
    }

    /**
     * 属性类型。
     */
    @Override
    public AttributeDataType getDataType()
    {
        return AttributeDataType.address;
    }

    /**
     * 转换成字节数组。
     * @param outputStream 输出流对象。
     * @param attribute 属性值。
     * @return 转换后的字节。
     * @throws IOException
     */
    protected void outputValue(ByteArrayOutputStream outputStream, AttributeData attributeData) throws IOException
    {
        Object value = attributeData.getValue();
        InetAddress inetAddress = null;
        if (value instanceof InetAddress)
            inetAddress = (InetAddress) value;
        else if (value instanceof String)
            inetAddress = InetAddress.getByName((String) value);
        byte[] addressBytes = inetAddress.getAddress();
        outputStream.write(addressBytes);
    }

    /**
     * 由报文解析属性对象。
     */
    @Override
    public AttributeData parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException
    {
        InetAddress inetAddress = null;
        try
        {
            byte[] addressBytes = new byte[length];
            System.arraycopy(bytes, beginIndex, addressBytes, 0, addressBytes.length);
            inetAddress = InetAddress.getByAddress(addressBytes);
        }
        catch (UnknownHostException e)
        {
            ProtocolParseException parseException = new ProtocolParseException(ProtocolParseException.ATTRIBUTE_ERROR, this.getFullName());
            parseException.setRootCause(e);
            throw parseException;
        }
        return this.createAttribute(inetAddress);
    }

    public String toString(Object value)
    {
        if (value instanceof InetAddress)
        {
            InetAddress inetAddress = (InetAddress) value;
            return inetAddress.getHostAddress();
        }
        return super.toString(value);
    }
}
