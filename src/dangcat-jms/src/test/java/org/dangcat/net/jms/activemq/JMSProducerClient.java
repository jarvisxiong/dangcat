package org.dangcat.net.jms.activemq;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.io.IOException;

public class JMSProducerClient extends Thread
{
    protected static final Logger logger = Logger.getLogger(JMSSession.class);

    public static void main(String[] args) throws IOException, SessionException
    {
        TestJMSSession.configure();
        new JMSProducerClient().start();
    }

    @Override
    public void run()
    {
        JMSProducer jmsProducer = null;
        try
        {
            JMSSession jmsSession = JMSConnectionFactory.getInstance().openSession("queue");
            jmsProducer = jmsSession.createJMSProducer();
            TextMessage textMessage = jmsSession.getSession().createTextMessage("Text Message");
            jmsProducer.send(textMessage);
        }
        catch (JMSException e)
        {
        }
        finally
        {
            jmsProducer.release();
            JMSConnectionFactory.getInstance().close();
        }
    }
}
