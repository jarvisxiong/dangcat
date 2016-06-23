package org.dangcat.net.jms.service;

import org.dangcat.boot.event.service.EventListenService;
import org.dangcat.boot.event.service.EventSendService;
import org.dangcat.boot.event.service.impl.EventSendServiceImpl;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.net.jms.activemq.JMSConnectionFactory;

/**
 * 消息服务。
 */
public class JMSServiceImpl extends ServiceBase {
    /**
     * 构建服务对象。
     *
     * @param parent 所属服务。
     */
    public JMSServiceImpl(ServiceProvider parent) {
        super(parent);
    }

    /**
     * 初始化服务。
     */
    @Override
    public void initialize() {
        super.initialize();

        EventListenService eventListenService = this.getService(EventListenService.class);
        EventSendServiceImpl eventSendService = (EventSendServiceImpl) this.getService(EventSendService.class);

        // 根据资源配置初始化消息组件。
        for (String name : JMSConnectionFactory.getInstance().getResourceNames()) {
            // 消息发送服务。
            JMSSender jmsSender = new JMSSender(name);
            jmsSender.initialize();
            eventSendService.addEventSender(jmsSender);

            // 消息侦听服务。
            JMSListener jmsListener = new JMSListener(name, eventListenService);
            jmsListener.initialize();
            eventListenService.addEventListener(jmsListener);
        }
    }
}
