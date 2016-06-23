package org.dangcat.net.jms.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.pool.ConnectionPool;

import javax.jms.Connection;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Session;
import java.util.Map;

/**
 * JMS连接池。
 * 
 */
public class JMSConnectionPool extends ConnectionPool<Connection> implements ExceptionListener
{
    private int acknownledge = Session.AUTO_ACKNOWLEDGE;
    private long batch = 10;
    private String bindProperties = null;
    private String clientId;
    private boolean durable;
    private String password = null;
    private boolean persistent = false;
    private String selector = null;
    private String subject = null;
    private long timeToLive = 5 * 60 * 1000;
    private boolean topic = true;
    private boolean transacted = false;
    private String url = null;
    private String user = null;

    public JMSConnectionPool(String name, Map<String, String> params)
    {
        super(name, params);
    }

    @Override
    protected void close(Connection connection)
    {
        try
        {
            connection.close();
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
    }

    @Override
    protected Connection create()
    {
        Connection connection = null;
        try
        {
            ActiveMQConnectionFactory connectionFactory = this.createConnectionFactory();
            connection = connectionFactory.createConnection();
            if (this.durable && !ValueUtils.isEmpty(this.clientId))
                connection.setClientID(this.clientId);
            connection.setExceptionListener(this);
            connection.start();
        }
        catch (JMSException e)
        {
            logger.error(this, e);
        }
        return connection;
    }

    /**
     * 产生连接工厂。
     */
    private ActiveMQConnectionFactory createConnectionFactory()
    {
        ActiveMQConnectionFactory connectionFactory = null;
        if (!ValueUtils.isEmpty(this.getUser()))
            connectionFactory = new ActiveMQConnectionFactory(this.getUser(), this.getPassword(), this.getUrl());
        else
            connectionFactory = new ActiveMQConnectionFactory(this.getUrl());
        return connectionFactory;
    }

    public int getAcknownledge()
    {
        return acknownledge;
    }

    public void setAcknownledge(int acknownledge) {
        this.acknownledge = acknownledge;
    }

    public long getBatch()
    {
        return batch;
    }

    public void setBatch(long batch) {
        this.batch = batch;
    }

    public String getBindProperties()
    {
        return bindProperties;
    }

    public void setBindProperties(String bindProperties)
    {
        this.bindProperties = bindProperties;
    }

    public String getClientId()
    {
        return clientId;
    }

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getSelector()
    {
        return selector;
    }

    public void setSelector(String selector)
    {
        this.selector = selector;
    }

    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public long getTimeToLive()
    {
        return timeToLive;
    }

    public void setTimeToLive(long timeToLive)
    {
        this.timeToLive = timeToLive;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUser()
    {
        return user;
    }

    public void setUser(String user)
    {
        this.user = user;
    }

    public boolean isDurable()
    {
        return durable;
    }

    public void setDurable(boolean durable)
    {
        this.durable = durable;
    }

    public boolean isPersistent()
    {
        return persistent;
    }

    public void setPersistent(boolean persistent)
    {
        this.persistent = persistent;
    }

    public boolean isTopic()
    {
        return topic;
    }

    public void setTopic(boolean topic)
    {
        this.topic = topic;
    }

    public boolean isTransacted()
    {
        return transacted;
    }

    public void setTransacted(boolean transacted)
    {
        this.transacted = transacted;
    }

    @Override
    public void onException(JMSException exception)
    {
        logger.error(this, exception);
    }
}
