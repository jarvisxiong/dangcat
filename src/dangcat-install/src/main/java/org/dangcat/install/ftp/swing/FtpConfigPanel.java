package org.dangcat.install.ftp.swing;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.install.ftp.FtpParameter;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.swing.panel.ValidateUtils;
import org.dangcat.net.ftp.service.impl.FTPClientSession;
import org.dangcat.swing.keyadapter.MaxLengthKeyAdapter;
import org.dangcat.swing.keyadapter.PortKeyAdapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class FtpConfigPanel extends ConfigPanel
{
    protected static final Logger logger = Logger.getLogger(FtpConfigPanel.class);
    private static final String INITPATH = "InitPath";
    private static final String PASSWORD = "Password";
    private static final String PORT = "Port";
    private static final long serialVersionUID = 1L;
    private static final String SERVER = "Server";
    private static final String USERNAME = "UserName";
    private static Dimension MinimumSize = new Dimension(40, 22);
    private FtpConfigListener ftpConfigListener = null;
    private FtpParameter ftpParameter = null;
    private JTextField initpathTextField = null;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JTextField usernameTextField = null;

    private void addComponents(JPanel panel, int y, JLabel label, Component component)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 10);
        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.insets = new Insets(2, 2, 2, 40);
        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = y;
        panel.add(component, constraints);
    }

    private void checkDatabaseConnection()
    {
        if (!this.validateData())
            return;

        FTPClientSession ftpClientSession = new FTPClientSession();
        try
        {
            FtpParameter ftpParameter = this.getFtpParameter();
            ftpClientSession.setServer(ftpParameter.getServer());
            ftpClientSession.setUserName(ftpParameter.getUser());
            ftpClientSession.setPassword(ftpParameter.getPassword());
            ftpClientSession.setInitPath(ftpParameter.getInitPath());
            ftpClientSession.setPort(ftpParameter.getPort());
            ftpClientSession.setPoolEnabled(false);
            ftpClientSession.initialize();
            if (ftpClientSession.testConnect() != null)
                throw ftpClientSession.testConnect();
            JOptionPane.showMessageDialog(this, this.getText("ConnectionSuccess"));
        }
        catch (Exception e)
        {
            logger.error(this, e);
            JOptionPane.showMessageDialog(this, this.getText("ConnectionError") + e.getMessage());
        }
        finally
        {
            ftpClientSession.close();
        }
    }

    private JPanel createFtpConfigPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 160));

        int y = 1;
        this.serverTextField = new JTextField();
        this.serverTextField.setColumns(Short.MAX_VALUE);
        this.serverTextField.addKeyListener(new MaxLengthKeyAdapter(32));
        JLabel serverLabel = new JLabel(this.getText(SERVER), JLabel.RIGHT);
        serverLabel.setLabelFor(this.serverTextField);
        this.addComponents(panel, y++, serverLabel, this.serverTextField);

        this.portTextField = new JTextField();
        this.portTextField.setColumns(Short.MAX_VALUE);
        this.portTextField.addKeyListener(new PortKeyAdapter());
        JLabel portLabel = new JLabel(this.getText(PORT), JLabel.RIGHT);
        portLabel.setLabelFor(this.portTextField);
        this.addComponents(panel, y++, portLabel, this.portTextField);

        this.initpathTextField = new JTextField();
        this.initpathTextField.setColumns(Short.MAX_VALUE);
        this.initpathTextField.addKeyListener(new MaxLengthKeyAdapter(60));
        JLabel databaseLabel = new JLabel(this.getText(INITPATH), JLabel.RIGHT);
        databaseLabel.setLabelFor(this.initpathTextField);
        this.addComponents(panel, y++, databaseLabel, this.initpathTextField);

        this.usernameTextField = new JTextField();
        this.usernameTextField.setColumns(Short.MAX_VALUE);
        this.usernameTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel userLabel = new JLabel(this.getText(USERNAME), JLabel.RIGHT);
        userLabel.setLabelFor(this.usernameTextField);
        this.addComponents(panel, y++, userLabel, this.usernameTextField);

        this.passwordTextField = new JPasswordField();
        this.passwordTextField.setColumns(Short.MAX_VALUE);
        this.passwordTextField.addKeyListener(new MaxLengthKeyAdapter(20));
        JLabel passwordLabel = new JLabel(this.getText(PASSWORD), JLabel.RIGHT);
        passwordLabel.setLabelFor(this.passwordTextField);
        this.addComponents(panel, y++, passwordLabel, this.passwordTextField);

        this.createFtpTestButton(panel, y++);
        return panel;
    }

    private void createFtpTestButton(JPanel panel, int y)
    {
        ImageIcon testImage = ImageUtils.loadImageIcon(this.getClass(), "test.gif");
        JButton testButton = new JButton(this.getText("TestConnection"), testImage);
        testButton.setMargin(new Insets(0, 20, 0, 20));
        testButton.setMaximumSize(new Dimension(200, 0));
        testButton.setAlignmentX(CENTER_ALIGNMENT);
        testButton.setMnemonic(KeyEvent.VK_T);
        testButton.setDisplayedMnemonicIndex(testButton.getText().indexOf("T"));
        testButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                FtpConfigPanel.this.checkDatabaseConnection();
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 2, 2, 40);
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.LINE_END;
        panel.add(testButton, constraints);
    }

    public FtpParameter getFtpParameter()
    {
        return this.ftpParameter;
    }

    public void setFtpParameter(FtpParameter ftpParameter) {
        this.ftpParameter = ftpParameter;
    }

    @Override
    public void initialize()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createFtpConfigPanel());
        this.update();

        this.ftpConfigListener = new FtpConfigListener(this);
        this.ftpConfigListener.setFtpParameter(this.ftpParameter);
        this.ftpConfigListener.setInitPathTextField(this.initpathTextField);
        this.ftpConfigListener.setPasswordTextField(this.passwordTextField);
        this.ftpConfigListener.setPortTextField(this.portTextField);
        this.ftpConfigListener.setServerTextField(this.serverTextField);
        this.ftpConfigListener.setUserTextField(this.usernameTextField);
        this.ftpConfigListener.initialize();
    }

    public void update()
    {
        if (this.ftpConfigListener != null)
            this.ftpConfigListener.setEnabled(false);

        FtpParameter ftpParameter = this.getFtpParameter();
        this.initpathTextField.setText(ftpParameter.getInitPath());
        this.passwordTextField.setText(ftpParameter.getPassword());
        this.portTextField.setText(ftpParameter.getPort().toString());
        this.serverTextField.setText(ftpParameter.getServer());
        this.usernameTextField.setText(ftpParameter.getUser());

        if (this.ftpConfigListener != null)
            this.ftpConfigListener.setEnabled(true);
    }

    @Override
    public boolean validateData()
    {
        if (!ValidateUtils.validateServer(this, this.serverTextField, SERVER))
            return false;
        if (!ValidateUtils.validatePort(this, this.portTextField, PORT))
            return false;
        if (!ValidateUtils.validateUser(this, this.usernameTextField, USERNAME))
            return false;
        if (!ValidateUtils.validatePassword(this, this.passwordTextField, PASSWORD))
            return false;
        return ValidateUtils.validateInitPath(this, this.initpathTextField, INITPATH);

    }
}
