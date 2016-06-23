package org.dangcat.net.rfc.service.impl;

import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.rfc.service.PacketIdentifierService;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

/**
 * 包识别号服务。
 * @author dangcat
 * 
 */
public class PacketIdentifierManager extends ServiceBase implements PacketIdentifierService
{
    private static PacketIdentifierManager instance = null;
    private Map<Integer, PacketIdentifier> packetIdentifierMap = new HashMap<Integer, PacketIdentifier>();

    public PacketIdentifierManager(ServiceProvider parent) {
        super(parent);
    }

    /**
     * 建立服务实例。
     * @param parent 所属父服务。
     * @return
     */
    public static synchronized PacketIdentifierService createInstance(ServiceProvider parent)
    {
        if (instance == null)
        {
            instance = new PacketIdentifierManager(parent);
            instance.initialize();
        }
        return instance;
    }

    /**
     * 添加包类型的识别号。
     * @param radiusPacketType 包类型。
     */
    public void addPacketType(Integer... packetTypes)
    {
        Map<Integer, PacketIdentifier> packetIdentifierMap = new HashMap<Integer, PacketIdentifier>();
        packetIdentifierMap.putAll(this.packetIdentifierMap);
        for (Integer packetType : packetTypes)
            packetIdentifierMap.put(packetType, new PacketIdentifier(packetType));
        this.packetIdentifierMap = packetIdentifierMap;
    }

    /**
     * 添加指定包的处理识别号。。
     * @param packetType 包类型。
     * @param identifier 识别号。
     * @return 是否可以进行处理。
     */
    public boolean addProcess(InetAddress inetAddress, Integer packetType, Integer identifier)
    {
        return this.packetIdentifierMap.get(packetType).addProcess(inetAddress, identifier);
    }

    /**
     * 提取包类型的识别号。
     * @param packetType 包类型。
     * @return 识别号。
     */
    public int nextIdentifier(Integer packetType)
    {
        return this.packetIdentifierMap.get(packetType).nextIdentifier();
    }

    /**
     * 删除指定包的处理识别号。。
     * @param packetType 包类型。
     * @param identifier 识别号。
     */
    public void removeProcess(InetAddress inetAddress, Integer packetType, Integer identifier)
    {
        this.packetIdentifierMap.get(packetType).removeProcess(inetAddress, identifier);
    }
}
