package org.dangcat.net.rfc;

import org.dangcat.commons.utils.ParseUtils;
import org.dangcat.net.event.DatagramEvent;
import org.dangcat.net.process.service.SessionBase;
import org.dangcat.net.rfc.exceptions.ProtocolParseException;
import org.dangcat.net.udp.conf.ListenerConfig;

/**
 * Packet 上下文。
 * @author dangcat
 * 
 */
public abstract class PacketSession<T extends Packet> extends SessionBase
{
    /** 识别号。 */
    private Long id = null;
    /** 发送报文是否需要回应。 */
    private boolean isNeedReply = false;
    /** 回应的原始报文。 */
    private DatagramEvent replyEvent = null;
    /** 回复的报文对象。 */
    private T replyPacket = null;
    /** 收到的原始报文。 */
    private DatagramEvent requestEvent = null;
    /** 收到的报文对象。 */
    private T requestPacket = null;
    /** 回复的原始报文。 */
    private DatagramEvent responseEvent = null;
    /** 回应的报文对象。 */
    private T responsePacket = null;

    public PacketSession()
    {
    }

    public PacketSession(DatagramEvent requestEvent)
    {
        this.requestEvent = requestEvent;
    }

    public PacketSession(T requestPacket)
    {
        this.requestPacket = requestPacket;
    }

    public void createResponseEvent()
    {
        try
        {
            Packet responseRadiusPacket = this.getResponsePacket();
            if (responseRadiusPacket != null)
            {
                byte[] dataBuffer = responseRadiusPacket.getBytes();
                if (dataBuffer != null && dataBuffer.length > 0)
                {
                    DatagramEvent responseEvent = this.getRequestEvent().createReply(dataBuffer);
                    this.setResponseEvent(responseEvent);
                }
            }
        }
        catch (ProtocolParseException e)
        {
            this.getLogger().error(this, e);
        }
    }

    @Override
    public String getDebugInfo()
    {
        return PacketSessionUtils.createDebugText(this);
    }

    public Long getId()
    {
        if (this.id == null && this.getRequestEvent() != null)
        {
            long id = (long) (Math.random() * 10000);
            byte[] dataBuffer = this.getRequestEvent().getDataBuffer();
            if (dataBuffer != null && dataBuffer.length > 2)
                id = id << 16 | ParseUtils.getInt(dataBuffer, 0, 2);
            this.id = id;
        }
        return this.id;
    }

    public abstract ListenerConfig getListenerConfig();

    public DatagramEvent getReplyEvent()
    {
        return this.replyEvent;
    }

    public void setReplyEvent(DatagramEvent replyEvent) {
        this.replyEvent = replyEvent;
    }

    public T getReplyPacket()
    {
        return this.replyPacket;
    }

    public void setReplyPacket(T replyPacket) {
        this.replyPacket = replyPacket;
    }

    public DatagramEvent getRequestEvent()
    {
        return this.requestEvent;
    }

    public T getRequestPacket()
    {
        return this.requestPacket;
    }

    protected void setRequestPacket(T requestPacket) {
        this.requestPacket = requestPacket;
    }

    public DatagramEvent getResponseEvent()
    {
        return this.responseEvent;
    }

    public void setResponseEvent(DatagramEvent responseEvent) {
        this.responseEvent = responseEvent;
    }

    public T getResponsePacket()
    {
        return this.responsePacket;
    }

    public void setResponsePacket(T responsePacket) {
        this.responsePacket = responsePacket;
    }

    @Override
    public long getTimeCostThreshold()
    {
        if (this.getListenerConfig() != null)
            return this.getListenerConfig().getTimeCostThreshold();
        return super.getTimeCostThreshold();
    }

    public boolean isNeedReply()
    {
        return this.isNeedReply;
    }

    public void setNeedReply(boolean isNeedReply) {
        this.isNeedReply = isNeedReply;
    }

    public void logDebug(Object message)
    {
        PacketSessionUtils.logDebug(this, message);
    }

    public void logError(String message, Object source, Throwable throwable)
    {
        PacketSessionUtils.logError(this, message, source, throwable);
    }

    public void logInfo(Object message)
    {
        PacketSessionUtils.logInfo(this, message);
    }

    public abstract void parse() throws ProtocolParseException;

    @Override
    public String toString()
    {
        return PacketSessionUtils.createInfoText(this);
    }
}