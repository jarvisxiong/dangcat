package org.dangcat.business.web;

import org.dangcat.boot.Launcher;
import org.dangcat.boot.security.annotation.ExtendSecurity;
import org.dangcat.boot.security.annotation.LocalSecurity;
import org.dangcat.boot.server.impl.ServerKeepLiveServiceImpl;
import org.dangcat.business.log.OperateLogSettle;
import org.dangcat.business.settle.SettleService;
import org.dangcat.business.settle.impl.SettleServiceImpl;
import org.dangcat.business.staff.security.OperatorSecurityServiceImpl;
import org.dangcat.business.systeminfo.ExtendSystemInfo;
import org.dangcat.framework.service.MainServiceBase;
import org.dangcat.framework.service.ServiceProvider;

@LocalSecurity
@ExtendSecurity(OperatorSecurityServiceImpl.class)
@ExtendSystemInfo(WebServerSystemInfoProvider.class)
public class WebServer extends MainServiceBase
{
    public static final String SERVICE_NAME = "dangcat";

    public static void main(String[] args)
    {
        Launcher.start(WebServer.class, SERVICE_NAME, false);
    }

    public WebServer(ServiceProvider parent)
    {
        super(parent);
    }

    /**
     * 初始化服务。
     */
    @Override
    public void initialize()
    {
        super.initialize();

        // 结算服务。
        SettleServiceImpl settleService = new SettleServiceImpl(this);
        settleService.addSettleUnit(new OperateLogSettle());
        this.addService(SettleService.class, settleService);
        settleService.initialize();

        // 服务器心跳管理。
        ServerKeepLiveServiceImpl serverKeepLiveService = new ServerKeepLiveServiceImpl(this);
        this.addService(ServerKeepLiveServiceImpl.class, serverKeepLiveService);
        serverKeepLiveService.initialize();
    }
}