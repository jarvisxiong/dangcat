package org.dangcat.net.jms.service;

import org.apache.log4j.Logger;
import org.dangcat.boot.event.service.EventSender;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.net.jms.activemq.JMSProducer;
import org.dangcat.net.jms.activemq.JMSSession;

import javax.jms.JMSException;
import javax.jms.Message;

/**
 * JMS消息发送服务。
 * 
 */
public class JMSSender extends JMSBase implements EventSender
{
    protected static final Logger logger = Logger.getLogger(JMSSender.class);
    private JMSProducer jmsProducer = null;

    /**
     * 构建服务
     * @param parent 所属服务。
     * @param name 消息名称。
     */
    public JMSSender(String name)
    {
        super(name);
    }

    /**
     * 设置消息的绑定属性。
     * @param event 事件对象。
     * @param objectMessage 消息对象。
     * @throws JMSException
     */
    private void bindProperties(Event event, Message message) throws JMSException
    {
        String bindProperties = this.getJMSSession().getJMSConnectionPool().getBindProperties();
        if (!ValueUtils.isEmpty(bindProperties))
        {
            for (String bindProperty : bindProperties.split(";"))
            {
                Object value = event.getParams().get(bindProperty);
                if (value != null)
                    message.setObjectProperty(bindProperty, value);
            }
        }
    }

    private JMSProducer getJMSProducer()
    {
        if (this.jmsProducer == null)
            this.start();
        return this.jmsProducer;
    }

    @Override
    public void send(Event event)
    {
        try
        {
            if (logger.isDebugEnabled())
                logger.debug(event);

            JMSProducer jmsProducer = this.getJMSProducer();
            if (jmsProducer != null)
            {
                // 发送消息
                Message message = this.getJMSSession().getSession().createObjectMessage(event);
                if (message != null)
                {
                    this.bindProperties(event, message);
                    jmsProducer.send(message);
                }
            }
        }
        catch (JMSException e)
        {
            this.stop();
            if (logger.isDebugEnabled())
                logger.error(this, e);
            else
                logger.error("The jms " + this.getName() + " send error " + e);
        }
    }

    @Override
    public synchronized void start()
    {
        if (this.jmsProducer == null)
        {
            try
            {
                JMSSession jmsSession = this.getJMSSession();
                if (jmsSession != null)
                    this.jmsProducer = jmsSession.createJMSProducer();
            }
            catch (JMSException e)
            {
                logger.error(this.getName(), e);
            }
        }
    }

    /**
     * 释放资源。
     */
    @Override
    public synchronized void stop()
    {
        if (this.jmsProducer != null)
        {
            this.jmsProducer.release();
            this.jmsProducer = null;
        }
        super.stop();
    }
}
