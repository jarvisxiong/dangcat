package org.dangcat.net.jms.activemq;

import org.apache.log4j.Logger;

import javax.jms.*;

/**
 * JMS生产对象。
 * 
 */
public class JMSProducer
{
    protected static final Logger logger = Logger.getLogger(JMSProducer.class);
    private JMSSession jmsSession;
    private MessageProducer messageProducer = null;
    private long messagesSended = 0;

    public JMSProducer(JMSSession jmsSession)
    {
        this.jmsSession = jmsSession;
    }

    private void close()
    {
        try
        {
            if (this.messageProducer != null)
            {
                this.messageProducer.close();
                this.messageProducer = null;
            }
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    /**
     * 初始化消息生产对象。
     */
    protected void initialize() throws JMSException
    {
        JMSConnectionPool jmsConnectionPool = this.jmsSession.getJMSConnectionPool();
        try
        {
            Session session = this.jmsSession.getSession();
            this.messageProducer = session.createProducer(this.jmsSession.getDestination());
            // 是否持久化消息。
            if (jmsConnectionPool.isPersistent())
                this.messageProducer.setDeliveryMode(DeliveryMode.PERSISTENT);
            else
                this.messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
            // 消息存活时间。
            if (jmsConnectionPool.getTimeToLive() != 0)
                this.messageProducer.setTimeToLive(jmsConnectionPool.getTimeToLive());
        }
        catch (JMSException e)
        {
            if (jmsConnectionPool.isCommunicationsException(e))
            {
                this.release();
                jmsConnectionPool.closePooled();
            }
            throw e;
        }
    }

    /**
     * 释放会话对象。
     */
    public void release()
    {
        this.close();
        this.jmsSession.release();
    }

    /**
     * 发送JMS消息。
     * @param message 消息对象。
     * @throws JMSException 发送异常。
     */
    public void send(Message message) throws JMSException
    {
        if (message != null)
        {
            this.messagesSended++;
            this.messageProducer.send(message);
            this.jmsSession.commit();
        }
    }
}
