package org.dangcat.net.rfc;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;

import java.util.Map.Entry;

/**
 * Packet иообнд║ё
 * @author dangcat
 * 
 */
class PacketSessionUtils
{
    protected static String createDebugText(PacketSession<?> packetSession)
    {
        StringBuilder info = new StringBuilder();
        info.append(createInfoText(packetSession));
        info.append(Environment.LINE_SEPARATOR);
        if (packetSession.isCancel())
            info.append("Cancel: " + packetSession.isCancel());
        else
            info.append("Handled: " + packetSession.isHandle());
        if (packetSession.getParams().size() > 0)
        {
            StringBuilder paramsInfo = new StringBuilder();
            paramsInfo.append("Params : ");
            for (Object paramName : packetSession.getParams().keySet())
            {
                paramsInfo.append(Environment.LINE_SEPARATOR);
                paramsInfo.append("Param " + paramName + " = " + packetSession.getParams().get(paramName));
            }
            info.append(Environment.LINE_SEPARATOR);
            info.append(format(paramsInfo.toString()));
        }
        if (packetSession.getRequestEvent() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("RequestEvent : ");
            info.append(format(packetSession.getRequestEvent().toString()));
        }
        if (packetSession.getResponseEvent() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("ResponseEvent : ");
            info.append(format(packetSession.getResponseEvent().toString()));
        }
        if (packetSession.isNeedReply())
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("NeedReply: " + packetSession.isNeedReply());
        }
        if (packetSession.getReplyEvent() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("ReplyEvent: ");
            info.append(format(packetSession.getReplyEvent().toString()));
        }
        if (packetSession.getTimCostMap() != null)
        {
            for (Object timeCost : packetSession.getTimCostMap().entrySet())
            {
                Entry<?, ?> entry = (Entry<?, ?>) timeCost;
                info.append(Environment.LINE_SEPARATOR);
                info.append(entry.getKey());
                info.append(" cost ");
                info.append(entry.getValue());
                info.append("(ms)");
            }
        }
        return info.toString();
    }

    protected static String createInfoText(PacketSession<?> packetSession)
    {
        StringBuilder info = new StringBuilder();
        if (packetSession.getRequestPacket() != null)
        {
            info.append("Request: ");
            info.append(packetSession.getRequestPacket().getPacketName());
            info.append(" ");
        }
        if (packetSession.getResponsePacket() != null)
        {
            info.append("Response: ");
            info.append(packetSession.getResponsePacket().getPacketName());
            info.append(" ");
        }
        if (packetSession.getReplyPacket() != null)
        {
            info.append("Reply: ");
            info.append(packetSession.getReplyPacket().getPacketName());
            info.append(" ");
        }
        if (packetSession.getRequestEvent() != null)
        {
            info.append("From ");
            info.append(packetSession.getRequestEvent().getRemoteAddress().getHostAddress());
            info.append(":");
            info.append(packetSession.getRequestEvent().getRemotePort());
            info.append(" ");
        }
        if (packetSession.getResponseEvent() != null)
        {
            info.append("To ");
            info.append(packetSession.getResponseEvent().getRemoteAddress().getHostAddress());
            info.append(":");
            info.append(packetSession.getResponseEvent().getRemotePort());
            info.append(" ");
        }
        if (packetSession.getReplyEvent() != null)
        {
            info.append("Reply ");
            info.append(packetSession.getReplyEvent().getRemoteAddress().getHostAddress());
            info.append(":");
            info.append(packetSession.getReplyEvent().getRemotePort());
            info.append(" ");
        }
        if (packetSession.getId() != null)
        {
            info.append("Id:");
            info.append(packetSession.getId());
            info.append(" ");
        }
        if (packetSession.getTotalTimeCost() != 0)
        {
            info.append("TimeCost: ");
            info.append(packetSession.getTotalTimeCost());
            info.append("(ms) ");
        }
        if (packetSession.getRequestPacket() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("RequestPacket: ");
            info.append(format(packetSession.getRequestPacket().toString()));
        }
        if (packetSession.getResponsePacket() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("ResponsePacket: ");
            info.append(format(packetSession.getResponsePacket().toString()));
        }
        if (packetSession.getReplyPacket() != null)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append("ReplyPacket: ");
            info.append(format(packetSession.getReplyPacket().toString()));
        }
        return info.toString();
    }

    private static String format(String text)
    {
        return text.replace(Environment.LINE_SEPARATOR, "\t" + Environment.LINE_SEPARATOR);
    }

    private static StringBuilder getBaseInfo(PacketSession<?> packetSession, Object message)
    {
        StringBuilder info = new StringBuilder();
        info.append(packetSession);
        if (message != packetSession)
        {
            if (!(message instanceof String))
                info.append(Environment.LINE_SEPARATOR);
            info.append(message);
        }
        return info;
    }

    protected static void logDebug(PacketSession<?> packetSession, Object message)
    {
        Logger logger = packetSession.getLogger();
        if (logger.isDebugEnabled() && message != null)
            logger.debug(getBaseInfo(packetSession, message));
    }

    protected static void logError(PacketSession<?> packetSession, String message, Object source, Throwable throwable)
    {
        StringBuilder info = getBaseInfo(packetSession, message);
        if (source != null && source != packetSession)
        {
            info.append(Environment.LINE_SEPARATOR);
            info.append(source);
        }

        Logger logger = packetSession.getLogger();
        if (throwable != null)
            logger.error(info, throwable);
        else
            logger.error(info);
    }

    protected static void logInfo(PacketSession<?> packetSession, Object message)
    {
        Logger logger = packetSession.getLogger();
        if (message != null)
            logger.info(getBaseInfo(packetSession, message));
    }
}
