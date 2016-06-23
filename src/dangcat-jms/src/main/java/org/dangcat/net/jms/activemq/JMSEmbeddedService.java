package org.dangcat.net.jms.activemq;

import java.io.File;

import org.apache.activemq.broker.BrokerService;
import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 嵌入式JMS服务。
 * 
 */
public class JMSEmbeddedService
{
    private static final String DEFAULT_CONNECTOR = "tcp://localhost:61616";
    private static JMSEmbeddedService instance = new JMSEmbeddedService();
    protected static final Logger logger = Logger.getLogger(JMSEmbeddedService.class);

    public static JMSEmbeddedService getInstance()
    {
        return instance;
    }

    private BrokerService brokerService = null;
    private String connector = DEFAULT_CONNECTOR;
    private boolean isPersistent = false;
    private boolean isSupportFailOver = true;
    private boolean isUseJmx = true;
    private String name = null;
    private File tmpDataDirectory = null;

    private JMSEmbeddedService()
    {

    }

    public BrokerService getBrokerService()
    {
        return this.brokerService;
    }

    public String getName()
    {
        return this.name;
    }

    public File getTmpDataDirectory()
    {
        return this.tmpDataDirectory;
    }

    public boolean isPersistent()
    {
        return this.isPersistent;
    }

    public boolean isRunning()
    {
        return this.brokerService != null;
    }

    public boolean isSupportFailOver()
    {
        return this.isSupportFailOver;
    }

    public boolean isUseJmx()
    {
        return this.isUseJmx;
    }

    public void setConnector(String connector)
    {
        this.connector = connector;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPersistent(boolean isPersistent)
    {
        this.isPersistent = isPersistent;
    }

    public void setSupportFailOver(boolean isSupportFailOver)
    {
        this.isSupportFailOver = isSupportFailOver;
    }

    public void setTmpDataDirectory(File tmpDataDirectory)
    {
        this.tmpDataDirectory = tmpDataDirectory;
    }

    public void setUseJmx(boolean isUseJmx)
    {
        this.isUseJmx = isUseJmx;
    }

    /**
     * 启动服务。
     */
    public synchronized void start()
    {
        if (!this.isRunning())
        {
            try
            {
                BrokerService brokerService = new BrokerService();
                brokerService.setUseJmx(this.isUseJmx());
                brokerService.setPersistent(this.isPersistent);
                brokerService.setSupportFailOver(this.isSupportFailOver);
                if (!ValueUtils.isEmpty(this.name))
                    brokerService.setBrokerName(this.name);
                if (this.tmpDataDirectory != null)
                    brokerService.setTmpDataDirectory(this.tmpDataDirectory);
                if (!ValueUtils.isEmpty(this.connector))
                    brokerService.addConnector(DEFAULT_CONNECTOR);
                brokerService.start();
                this.brokerService = brokerService;
            }
            catch (Exception e)
            {
                logger.error(this, e);
            }
        }
    }

    /**
     * 停止服务。
     */
    public synchronized void stop()
    {
        if (this.isRunning())
        {
            try
            {
                this.brokerService.stop();
                this.brokerService = null;
            }
            catch (Exception e)
            {
                logger.error(this, e);
            }
        }
    }
}
