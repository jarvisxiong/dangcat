package org.dangcat.net.jms.service;

import org.dangcat.net.jms.activemq.JMSConnectionFactory;
import org.dangcat.net.jms.activemq.JMSSession;

/**
 * JMS消息服务。
 */
public abstract class JMSBase {
    private JMSSession jmsSession = null;
    private String name;

    /**
     * 构建服务
     *
     * @param parent 所属服务。
     * @param name   消息名称。
     */
    public JMSBase(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        JMSBase other = (JMSBase) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    protected JMSSession getJMSSession() {
        if (this.jmsSession == null)
            this.jmsSession = JMSConnectionFactory.getInstance().openSession(this.getName());
        return jmsSession;
    }

    /**
     * 侦听名称。
     */
    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public void initialize() {
    }

    /**
     * 停止服务。
     */
    public void stop() {
        if (this.jmsSession == null) {
            this.jmsSession.release();
            this.jmsSession = null;
        }
    }
}
