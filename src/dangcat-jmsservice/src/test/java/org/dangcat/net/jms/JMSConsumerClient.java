package org.dangcat.net.jms;

import org.dangcat.net.jms.activemq.JMSConnectionFactory;
import org.dangcat.net.jms.activemq.JMSConsumer;
import org.dangcat.net.jms.activemq.JMSSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

public class JMSConsumerClient extends JMSClientBase
{
    private static final String TOPIC = "topic";

    public JMSConsumerClient(String subject) {
        super(subject);
    }

    public static void main(String[] args)
    {
        JMSConsumerClient jmsClient = new JMSConsumerClient(TOPIC);
        jmsClient.configure();
        jmsClient.start();
    }

    @Override
    public void run()
    {
        try
        {
            JMSSession jmsSession = JMSConnectionFactory.getInstance().openSession(this.getSubject());
            JMSConsumer jmsConsumer = jmsSession.createJMSConsumer();
            jmsConsumer.addMessageListener(new MessageListener()
            {
                @Override
                public void onMessage(Message message)
                {
                    TextMessage textMessage = (TextMessage) message;
                    try
                    {
                        System.out.println("receive message " + textMessage.getText());
                    }
                    catch (JMSException e)
                    {
                        logger.error(this, e);
                    }
                }
            });
            jmsConsumer.receive();
        }
        catch (Exception e)
        {
            logger.error(this, e);
        }
    }
}
