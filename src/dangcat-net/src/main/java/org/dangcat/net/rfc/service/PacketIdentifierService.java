package org.dangcat.net.rfc.service;

import java.net.InetAddress;

/**
 * 包识别号服务。
 * @author dangcat
 * 
 */
public interface PacketIdentifierService
{
    /**
     * 添加包类型的识别号。
     * @param radiusPacketType 包类型。
     */
    public void addPacketType(Integer... packetTypes);

    /**
     * 添加指定包的处理识别号。。
     * @param packetType 包类型。
     * @param identifier 识别号。
     * @return 是否可以进行处理。
     */
    public boolean addProcess(InetAddress inetAddress, Integer packetType, Integer identifier);

    /**
     * 提取包类型的识别号。
     * @param packetType 包类型。
     * @return 识别号。
     */
    public int nextIdentifier(Integer packetType);

    /**
     * 删除指定包的处理识别号。。
     * @param packetType 包类型。
     * @param identifier 识别号。
     */
    public void removeProcess(InetAddress inetAddress, Integer packetType, Integer identifier);
}
