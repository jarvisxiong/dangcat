package org.dangcat.boot.server.event;

import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.event.Event;

public class ServerEvent extends Event
{
    public static final String KeepLive = "KeepLive";
    public static final String Register = "Register";
    private static final long serialVersionUID = 1L;

    private ServerInfo serverInfo = null;

    public ServerEvent(String Id, ServerInfo serverInfo)
    {
        super(Id);
        this.serverInfo = serverInfo;
    }

    public ServerInfo getServerInfo()
    {
        return this.serverInfo;
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        if (this.getServerInfo() != null)
            info.append(this.getServerInfo());
        info.append(Environment.LINE_SEPARATOR);
        info.append(super.toString());
        return info.toString();
    }
}
