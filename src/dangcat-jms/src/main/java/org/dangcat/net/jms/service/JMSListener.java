package org.dangcat.net.jms.service;

import org.apache.log4j.Logger;
import org.dangcat.boot.event.service.EventListener;
import org.dangcat.boot.event.service.EventReceiveListener;
import org.dangcat.framework.event.Event;
import org.dangcat.net.jms.activemq.JMSConsumer;
import org.dangcat.net.jms.activemq.JMSSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

/**
 * 消息侦听服务。
 * 
 */
public class JMSListener extends JMSBase implements MessageListener, EventListener
{
    protected static final Logger logger = Logger.getLogger(JMSListener.class);
    private EventReceiveListener eventReceiveListener = null;
    private JMSConsumer jmsConsumer = null;

    /**
     * 构建服务
     * @param parent 所属服务。
     * @param name 消息名称。
     */
    public JMSListener(String name, EventReceiveListener eventReceiveListener)
    {
        super(name);
        this.eventReceiveListener = eventReceiveListener;
    }

    private JMSConsumer getJMSConsumer() throws JMSException
    {
        if (this.jmsConsumer == null)
        {
            JMSSession jmsSession = this.getJMSSession();
            if (jmsSession != null)
            {
                this.jmsConsumer = jmsSession.createJMSConsumer();
                this.jmsConsumer.addMessageListener(this);
            }
        }
        return this.jmsConsumer;
    }

    /**
     * 接收处理消息。
     */
    @Override
    public void onMessage(Message message)
    {
        try
        {
            Event event = this.resolveEvent(message);
            if (event != null)
            {
                logger.info(this.getName() + " recived event: " + event.getId());
                if (logger.isDebugEnabled())
                    logger.debug(event);
                if (this.eventReceiveListener != null)
                    this.eventReceiveListener.onReceive(event);
            }
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    /**
     * 解析消息中的事件。
     * @param message 消息对象。
     * @return 解析后的有效事件对象。
     * @throws JMSException
     */
    private Event resolveEvent(Message message) throws JMSException
    {
        Event event = null;
        if (message instanceof ObjectMessage)
        {
            ObjectMessage objectMessage = (ObjectMessage) message;
            if (objectMessage.getObject() instanceof Event)
                event = (Event) objectMessage.getObject();
        }
        return event;
    }

    /**
     * 启动服务。
     */
    @Override
    public void start()
    {
        try
        {
            JMSConsumer jmsConsumer = this.getJMSConsumer();
            if (jmsConsumer != null)
                jmsConsumer.receive();
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    /**
     * 释放资源。
     */
    @Override
    public void stop()
    {
        if (this.jmsConsumer != null)
        {
            this.jmsConsumer.release();
            this.jmsConsumer = null;
        }
        super.stop();
    }
}
