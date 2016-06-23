package org.dangcat.net.jms;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.framework.conf.ConfigureManager;

import java.io.File;

public class JMSClientBase extends Thread {
    protected static final Logger logger = Logger.getLogger(JMSClientBase.class);

    private String subject = null;

    public JMSClientBase(String subject) {
        this.subject = subject;
    }

    protected void configure() {
        Environment.setHomePath(JMSClientBase.class);
        String path = Environment.getHomePath() + "/test-classes/META-INF/resource.properties";
        File configFile = new File(FileUtils.decodePath(path));
        ConfigureManager.getInstance().configure(configFile);
    }

    public String getSubject() {
        return this.subject;
    }
}
