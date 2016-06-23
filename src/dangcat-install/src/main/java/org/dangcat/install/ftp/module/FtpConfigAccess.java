package org.dangcat.install.ftp.module;

import org.dangcat.commons.crypto.CryptoUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.ftp.FtpParameter;
import org.dangcat.install.ftp.swing.FtpConfigPanel;
import org.dangcat.install.task.ConfigureAccess;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

public class FtpConfigAccess extends FtpAccess implements ConfigureAccess {
    private Map<String, FtpConfigPanel> ftpConfigPanels = new LinkedHashMap<String, FtpConfigPanel>();

    public void addConfigPanel(String name, FtpConfigPanel ftpConfigPanel) {
        this.ftpConfigPanels.put(name, ftpConfigPanel);
    }

    private void load(FtpConfigPanel configPanel, Properties properties) {
        FtpParameter ftpParameter = configPanel.getFtpParameter();
        String name = ftpParameter.getName();
        String server = properties.getProperty(this.getServerKey(name));
        ftpParameter.setServer(ValueUtils.isEmpty(server) ? ftpParameter.getDefaultServer() : server);
        Integer port = ValueUtils.parseInt(properties.getProperty(this.getPortKey(name)), ftpParameter.getDefaultPort());
        ftpParameter.setPort(port);
        ftpParameter.setName(properties.getProperty(this.getFtpName(name)));
        String user = properties.getProperty(this.getUserKey(name));
        ftpParameter.setUser(user);
        String password = properties.getProperty(this.getPasswordKey(name));
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.decrypt(password);
        ftpParameter.setPassword(password);
        ftpParameter.setInitPath(properties.getProperty(this.getInitPath(name)));
        configPanel.update();
    }

    @Override
    public void load(Properties properties) {
        for (FtpConfigPanel ftpConfigPanel : this.ftpConfigPanels.values())
            this.load(ftpConfigPanel, properties);
    }

    private void save(FtpConfigPanel configPanel, Properties properties) {
        FtpParameter ftpParameter = configPanel.getFtpParameter();
        String name = ftpParameter.getName();
        String server = ftpParameter.getServer() == null ? ftpParameter.getDefaultServer() : ftpParameter.getServer();
        properties.setProperty(this.getServerKey(name), server);
        Integer port = ftpParameter.getPort() == null ? ftpParameter.getDefaultPort() : ftpParameter.getPort();
        properties.setProperty(this.getPortKey(name), port.toString());
        properties.setProperty(this.getFtpName(name), ftpParameter.getName());
        properties.setProperty(this.getUserKey(name), ftpParameter.getUser());
        properties.setProperty(this.getInitPath(name), ftpParameter.getInitPath());
        String password = ftpParameter.getPassword();
        if (!ValueUtils.isEmpty(password))
            password = CryptoUtils.encrypt(password);
        properties.setProperty(this.getPasswordKey(name), password == null ? "" : password);
    }

    @Override
    public void save(Properties properties) {
        for (FtpConfigPanel ftpConfigPanel : this.ftpConfigPanels.values())
            this.save(ftpConfigPanel, properties);
    }
}
