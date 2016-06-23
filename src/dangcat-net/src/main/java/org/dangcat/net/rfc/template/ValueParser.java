package org.dangcat.net.rfc.template;

import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.rfc.exceptions.ProtocolValidateException;

/**
 * 数据解析接口。
 * @author dangcat
 * 
 */
public interface ValueParser
{
    /**
     * 比较两个值是否相同。
     * @param value 目标值。
     * @return
     */
    int compareTo(Object value);

    /**
     * 由原始字节数组解析成对象。
     * @param bytes 字节数组。
     * @param beginIndex 起始位置。
     * @param length 长度。
     * @return 解析后的对象。
     * @throws ProtocolParseException 解析异常。
     */
    void parse(byte[] bytes, int beginIndex, int length) throws ProtocolParseException;

    /**
     * 由字串解析属性。
     * @param text 字节数组。
     */
    void parse(String text) throws ProtocolParseException;

    /**
     * 对象转换成协议数据。
     * @return 转换后的字节数组。
     */
    byte[] toBytes();

    /**
     * 校验数据是否合法。
     * @throws ProtocolValidateException 校验异常。
     */
    void validate() throws ProtocolValidateException;
}
