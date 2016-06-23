package org.dangcat.net.jms.activemq;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JMS消费对象。
 * 
 */
public class JMSConsumer implements MessageListener
{
    protected final Logger logger = Logger.getLogger(this.getClass());
    private String consumerName = null;
    private JMSSession jmsSession;
    private MessageConsumer messageConsumer = null;
    private List<MessageListener> messageListenerList = new ArrayList<MessageListener>();
    private long messagesReceived = 0;
    private long receiveTimeOut = 1000;
    private MessageProducer replyProducer;

    public JMSConsumer(JMSSession jmsSession)
    {
        this.jmsSession = jmsSession;
    }

    /**
     * 添加消息侦听对象。
     * @param messageListener 消息侦听器。
     */
    public void addMessageListener(MessageListener messageListener)
    {
        if (messageListener != null && !this.messageListenerList.contains(messageListener))
            this.messageListenerList.add(messageListener);
    }

    private void close()
    {
        try
        {
            if (this.messageConsumer != null)
            {
                this.messageConsumer.close();
                this.messageConsumer = null;
            }
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    public String getConsumerName()
    {
        return this.consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    /**
     * 初始化消费对象。
     */
    protected void initialize() throws JMSException
    {
        JMSConnectionPool jmsConnectionPool = this.jmsSession.getJMSConnectionPool();
        try
        {
            Session session = this.jmsSession.getSession();
            if (jmsConnectionPool.isDurable() && jmsConnectionPool.isTopic())
                this.messageConsumer = session.createDurableSubscriber((Topic) this.jmsSession.getDestination(), this.getConsumerName());
            else if (!ValueUtils.isEmpty(jmsConnectionPool.getSelector()))
                this.messageConsumer = session.createConsumer(this.jmsSession.getDestination(), jmsConnectionPool.getSelector());
            else
                this.messageConsumer = session.createConsumer(this.jmsSession.getDestination());

            this.replyProducer = session.createProducer(null);
            this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
        catch (JMSException e)
        {
            if (jmsConnectionPool.isCommunicationsException(e))
            {
                this.release();
                jmsConnectionPool.close();
            }
            throw e;
        }
    }

    @Override
    public void onMessage(Message message)
    {
        this.messagesReceived++;
        try
        {
            Session session = this.jmsSession.getSession();
            // 需要回复消息。
            if (message.getJMSReplyTo() != null)
            {
                TextMessage textMessage = session.createTextMessage("Reply: " + message.getJMSMessageID());
                this.replyProducer.send(message.getJMSReplyTo(), textMessage);
            }

            // 传递消息。
            for (MessageListener messageListener : this.messageListenerList)
                messageListener.onMessage(message);

            // 批量处理事务
            JMSConnectionPool jmsConnectionPool = this.jmsSession.getJMSConnectionPool();
            long batch = jmsConnectionPool.getBatch();
            if ((this.messagesReceived % batch) == 0)
            {
                if (jmsConnectionPool.isTransacted())
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Commiting transaction for last " + batch + " messages; messages so far = " + this.messagesReceived);
                    this.jmsSession.commit();
                }
                else if (jmsConnectionPool.getAcknownledge() == Session.CLIENT_ACKNOWLEDGE)
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Acknowledging last " + batch + " messages; messages so far = " + this.messagesReceived);
                    message.acknowledge();
                }
            }
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    /**
     * 启动侦听接收消息。
     */
    public void receive() throws JMSException
    {
        if (this.messageListenerList.size() > 0)
            this.messageConsumer.setMessageListener(this);
    }

    /**
     * 启动侦听接收消息。
     * @param maxiumMessages 最大接收数量。
     * @throws JMSException 运行异常。
     */
    public void receive(long maxiumMessages) throws JMSException
    {
        if (maxiumMessages > 0)
        {
            while (this.messagesReceived < maxiumMessages)
            {
                Message message = this.messageConsumer.receive(this.receiveTimeOut);
                if (message != null)
                    this.onMessage(message);
            }
        }
        else
            this.messageConsumer.setMessageListener(this);
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
     * 删除消息侦听对象。
     * @param messageListener 消息侦听对象。
     */
    public void removeMessageListener(MessageListener messageListener)
    {
        if (messageListener != null && this.messageListenerList.contains(messageListener))
            this.messageListenerList.remove(messageListener);
    }
}
