package org.dangcat.net.jms.activemq;

import java.util.Map;

import org.dangcat.framework.pool.ConnectionFactory;
import org.dangcat.framework.pool.SessionException;

/**
 * JMS连接工厂。
 */
public class JMSConnectionFactory extends ConnectionFactory<JMSConnectionPool, JMSSession>
{
    private static JMSConnectionFactory instance = null;
    public static final String RESOURCETYPE = "jms";

    public static JMSConnectionFactory getInstance()
    {
        if (instance == null)
        {
            synchronized (JMSConnectionFactory.class)
            {
                try
                {
                    JMSConnectionFactory instance = new JMSConnectionFactory();
                    instance.initialize();
                    JMSConnectionFactory.instance = instance;
                }
                catch (SessionException e)
                {
                    logger.error("Create jms connection error!", e);
                }
            }
        }
        return instance;
    }

    /**
     * 私有构造函数。
     */
    private JMSConnectionFactory()
    {
    }

    @Override
    protected void close(JMSConnectionPool jmsConnectionPool)
    {
        jmsConnectionPool.close();
    }

    @Override
    protected JMSConnectionPool createConnectionPool(String name, Map<String, String> params) throws SessionException
    {
        JMSConnectionPool jmsConnectionPool = new JMSConnectionPool(name, params);
        jmsConnectionPool.initialize();
        return jmsConnectionPool;
    }

    @Override
    protected JMSSession createSession(JMSConnectionPool jmsConnectionPool)
    {
        return new JMSSession(jmsConnectionPool);
    }

    @Override
    public String getResourceType()
    {
        return RESOURCETYPE;
    }
}
