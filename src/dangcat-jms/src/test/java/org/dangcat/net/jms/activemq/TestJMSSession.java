package org.dangcat.net.jms.activemq;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.conf.ConfigureManager;
import org.dangcat.framework.pool.SessionException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJMSSession
{
    protected static final Logger logger = Logger.getLogger(JMSSession.class);
    private static final int TEST_COUNT = 1000;

    public static void configure() throws IOException, SessionException
    {
        Environment.setHomePath(TestJMSSession.class);
        String path = Environment.getHomePath() + "/test-classes/META-INF/resource.properties";
        File configFile = new File(FileUtils.decodePath(path));
        ConfigureManager.getInstance().configure(configFile);
    }

    public static File createTmpDirectory(String path)
    {
        File dir = new File("log" + File.separator + path);
        if (dir.exists())
            FileUtils.delete(dir);
        FileUtils.mkdir(dir.getAbsolutePath());
        return dir;
    }

    @AfterClass
    public static void destroy()
    {
        JMSConnectionFactory.getInstance().close();
        JMSEmbeddedService.getInstance().stop();
    }

    @BeforeClass
    public static void initialize() throws IOException, SessionException
    {
        configure();

        JMSEmbeddedService jmsEmbeddedService = JMSEmbeddedService.getInstance();
        jmsEmbeddedService.setTmpDataDirectory(createTmpDirectory("activemq-data"));
        jmsEmbeddedService.start();

        sleep(1000);
    }

    private static void sleep(long timeLength)
    {
        try
        {
            Thread.sleep(timeLength);
        }
        catch (InterruptedException e)
        {
            logger.error(null, e);
        }
    }

    public void testJMS(String name) throws JMSException
    {
        final List<Message> destMessageList = new ArrayList<Message>();
        JMSSession jmsSession = JMSConnectionFactory.getInstance().openSession(name);
        JMSConsumer jmsConsumer = jmsSession.createJMSConsumer();
        jmsConsumer.addMessageListener(new MessageListener()
        {
            @Override
            public void onMessage(Message message)
            {
                destMessageList.add(message);
            }
        });

        jmsConsumer.receive();

        List<Message> srcMessageList = new ArrayList<Message>();
        JMSProducer jmsProducer = jmsSession.createJMSProducer();
        for (int i = 0; i < TEST_COUNT; i++)
        {
            TextMessage textMessage = jmsSession.getSession().createTextMessage();
            textMessage.setText("Text Message " + i);
            jmsProducer.send(textMessage);
            srcMessageList.add(textMessage);
        }

        sleep(1000);

        Assert.assertEquals(TEST_COUNT, destMessageList.size());
        for (int index = 0; index < TEST_COUNT; index++)
        {
            TextMessage srcTextMessage = (TextMessage) srcMessageList.get(index);
            TextMessage destTextMessage = (TextMessage) destMessageList.get(index);
            Assert.assertEquals(srcTextMessage.getText(), destTextMessage.getText());
        }
    }

    /**
     * 测试JMS的Queue消息功能。
     * @throws FTPSessionException
     */
    @Test
    public void testJMSQueue() throws JMSException
    {
        testJMS("queue");
    }

    /**
     * 测试JMS的Topic消息功能。
     * @throws FTPSessionException
     */
    @Test
    public void testJMSTopic() throws JMSException
    {
        testJMS("topic");
    }
}
