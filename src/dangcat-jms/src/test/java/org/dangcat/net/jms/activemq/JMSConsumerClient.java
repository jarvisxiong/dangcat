package org.dangcat.net.jms.activemq;

import org.apache.log4j.Logger;
import org.dangcat.framework.pool.SessionException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JMSConsumerClient extends Thread
{
    protected static final Logger logger = Logger.getLogger(JMSSession.class);
    private final List<Message> messageList = new ArrayList<Message>();
    private JMSConsumer jmsConsumer = null;
    private JMSSession jmsSession = null;

    public static void main(String[] args) throws IOException, SessionException
    {
        TestJMSSession.configure();
        new JMSConsumerClient().start();
    }

    @Override
    public void run()
    {
        try
        {
            jmsSession = JMSConnectionFactory.getInstance().openSession("queue");
            jmsConsumer = jmsSession.createJMSConsumer();
            jmsConsumer.addMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    messageList.add(message);
                }
            });
            jmsConsumer.receive();
        }
        catch (JMSException e)
        {
        }
    }
}
