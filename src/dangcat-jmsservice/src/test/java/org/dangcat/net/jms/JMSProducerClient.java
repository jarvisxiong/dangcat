package org.dangcat.net.jms;

import javax.jms.TextMessage;

import org.dangcat.net.jms.activemq.JMSConnectionFactory;
import org.dangcat.net.jms.activemq.JMSProducer;
import org.dangcat.net.jms.activemq.JMSSession;

public class JMSProducerClient extends JMSClientBase
{
    private static final String TOPIC = "topic";

    public static void main(String[] args)
    {
        JMSProducerClient jmsClient = new JMSProducerClient(TOPIC);
        jmsClient.configure();
        jmsClient.start();
    }

    public JMSProducerClient(String subject)
    {
        super(subject);
    }

    @Override
    public void run()
    {
        try
        {
            JMSSession jmsSession = JMSConnectionFactory.getInstance().openSession(this.getSubject());
            JMSProducer jmsProducer = jmsSession.createJMSProducer();
            int count = 0;
            while (true)
            {
                TextMessage textMessage = jmsSession.getSession().createTextMessage();
                textMessage.setText("Text Message " + count++);
                jmsProducer.send(textMessage);
                Thread.sleep(5000l);
            }
        }
        catch (Exception e)
        {
            logger.error(this, e);
        }
    }
}
