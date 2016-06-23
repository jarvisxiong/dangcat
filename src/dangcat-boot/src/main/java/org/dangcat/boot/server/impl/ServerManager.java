package org.dangcat.boot.server.impl;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.server.config.ServerConfig;
import org.dangcat.boot.server.domain.ServerInfo;
import org.dangcat.boot.server.event.ServerEvent;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.NetUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.persistence.entity.EntityManagerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 主机信息。
 * @author dangcat
 * 
 */
public class ServerManager extends ServiceBase
{
    public static final String BIND_SERVER_ADDRESS = "dangcat.bind.serverip";
    public static final String BIND_SERVER_ID = "dangcat.bind.serverid";
    public static final String BIND_SERVER_NAME = "dangcat.bind.servername";
    public static final String DEFAULT_SERVER_ADDRESS = "127.0.0.1";
    public static final String DEFAULT_SERVER_NAME = "localhost";
    private static final String EQUALS = " = ";
    private static ServerManager instance = null;
    private InetAddress localhost = null;
    private InetAddress serverAddress = null;
    private Integer serverId = null;
    private ServerInfo serverInfo = new ServerInfo();
    private Map<String, ServerInfo> serverInfoMap = new HashMap<String, ServerInfo>();

    private ServerManager(ServiceProvider parent) {
        super(parent);
    }

    public static ServerManager createInstance(ServiceProvider parent)
    {
        instance = new ServerManager(parent);
        return instance;
    }

    public static ServerManager getInstance()
    {
        return instance;
    }

    private InetAddress getLocalHost()
    {
        if (this.localhost == null)
        {
            try
            {
                this.localhost = NetUtils.toInetAddress(DEFAULT_SERVER_ADDRESS);
            }
            catch (UnknownHostException e)
            {
            }
        }
        return this.localhost;
    }

    public InetAddress getServerAddress()
    {
        if (ValueUtils.isEmpty(System.getProperty(BIND_SERVER_ADDRESS)))
            return this.getLocalHost();

        if (this.serverAddress == null)
        {
            InetAddress serverAddress = null;
            try
            {
                serverAddress = NetUtils.toInetAddress(System.getProperty(BIND_SERVER_ADDRESS));
            }
            catch (UnknownHostException e)
            {
            }
            if (serverAddress == null)
                serverAddress = this.getLocalHost();
            this.serverAddress = serverAddress;
        }
        return this.serverAddress;
    }

    public Integer getServerId()
    {
        if (this.serverId == null)
            this.serverId = ValueUtils.parseInt(System.getProperty(BIND_SERVER_ID));
        return this.serverId;
    }

    private void setServerId(Integer serverId) {
        this.serverId = serverId;
        if (serverId != null)
            System.setProperty(BIND_SERVER_ID, serverId.toString());
        else
            System.setProperty(BIND_SERVER_ID, null);
    }

    public ServerInfo getServerInfo()
    {
        this.serverInfo.setId(this.getServerId());
        this.serverInfo.setName(this.getServerName());
        this.serverInfo.setType(ServerConfig.getInstance().getType());
        this.serverInfo.setIp(this.getServerAddress().getHostAddress());
        this.serverInfo.setStatus(ApplicationContext.getInstance().getServiceStatus().getValue());
        return this.serverInfo;
    }

    public ServerInfo getServerInfo(String ip)
    {
        return this.serverInfoMap.get(ip);
    }

    public String getServerName()
    {
        String serverName = System.getProperty(BIND_SERVER_NAME);
        if (ValueUtils.isEmpty(serverName))
            return DEFAULT_SERVER_NAME;
        return serverName;
    }

    @Override
    public Object handle(Event event)
    {
        // 在管理中心注册成功。
        if (event instanceof ServerEvent && ServerEvent.Register.equalsIgnoreCase(event.getId()))
        {
            ServerEvent serverEvent = (ServerEvent) event;
            ServerInfo serverInfo = serverEvent.getServerInfo();
            if (serverInfo != null)
                this.setServerId(serverInfo.getId());
            event.setHandled(true);
        }
        return super.handle(event);
    }

    public void load()
    {
        List<ServerInfo> serverInfos = EntityManagerFactory.getInstance().open().load(ServerInfo.class);
        if (serverInfos != null && !serverInfos.isEmpty())
        {
            Map<String, ServerInfo> serverInfoMap = new HashMap<String, ServerInfo>();
            for (ServerInfo serverInfo : serverInfos)
                serverInfoMap.put(serverInfo.getIp(), serverInfo);
            this.serverInfoMap = serverInfoMap;
        }
    }

    public void remove(ServerInfo serverInfo)
    {
        this.serverInfoMap.remove(serverInfo.getIp());
    }

    @Override
    public String toString()
    {
        StringBuilder info = new StringBuilder();
        info.append("LocalHost Info :");
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(BIND_SERVER_ADDRESS);
        info.append(EQUALS);
        info.append(this.getServerAddress().getHostAddress());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(BIND_SERVER_ID);
        info.append(EQUALS);
        info.append(this.getServerId());
        info.append(Environment.LINETAB_SEPARATOR);
        info.append(BIND_SERVER_NAME);
        info.append(EQUALS);
        info.append(this.getServerName());
        return info.toString();
    }
}
