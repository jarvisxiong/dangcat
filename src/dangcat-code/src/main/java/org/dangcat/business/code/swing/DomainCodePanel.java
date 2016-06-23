package org.dangcat.business.code.swing;

import org.apache.log4j.Logger;
import org.dangcat.business.code.entity.DomainCodeGenerator;
import org.dangcat.business.code.entity.Table;
import org.dangcat.business.code.entity.TableManager;
import org.dangcat.business.code.entity.TextDocumentListener;
import org.dangcat.commons.database.Database;
import org.dangcat.commons.database.MySqlDatabase;
import org.dangcat.commons.database.OracleDatabase;
import org.dangcat.commons.database.SqlServerDatabase;
import org.dangcat.swing.JCheckBoxList;
import org.dangcat.swing.JLogPane;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;

public class DomainCodePanel extends CodePanel implements ActionListener
{
    private static final String BASE_CLASS = "BaseClass";
    private static final String BASECLASS = "org.dangcat.persistence.entity.EntityBase";
    private static final String DATABASE = "Database";
    private static final String DATABASE_NAME = "DatabaseName";
    private static final String DATABASE_TYPE = "DatabaseType";
    private static final String PACKAGE = "Package";
    private static final String PASSWORD = "Password";
    private static final String PATH = "Path";
    private static final String PORT = "Port";
    private static final long serialVersionUID = 1L;
    private static final String SERVER = "Server";
    private static final String USER = "User";
    private static Dimension MinimumSize = new Dimension(60, 22);
    private JTextField baseClassTextField = null;
    @SuppressWarnings("unchecked")
    private JComboBox databaseComboBox = null;
    private JTextField databaseTextField = null;
    private JLogPane logPane = null;
    private JTextField packageTextField = null;
    private JPasswordField passwordTextField = null;
    private JTextField pathTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JCheckBoxList tableList = null;
    private TextDocumentListener textDocumentListener = null;
    private JTextField userTextField = null;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        final Object source = actionEvent.getSource();
        if (source == this.databaseComboBox)
            this.initDatabaseConfig();
    }

    private void addComponents(JPanel panel, int y, JLabel label, Component component)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.2;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridy = y;
        panel.add(component, constraints);
    }

    private void addComponents(JPanel panel, int y, JLabel label, JTextField textField, JButton button)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.1;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        label.setMinimumSize(MinimumSize);
        panel.add(label, constraints);

        constraints.weightx = 0.8;
        constraints.gridx = 1;
        constraints.gridwidth = 3;
        constraints.gridy = y;
        panel.add(textField, constraints);

        if (button != null)
        {
            constraints.weightx = 0.1;
            constraints.gridx = 4;
            constraints.gridwidth = 1;
            constraints.gridy = y;
            button.setMinimumSize(MinimumSize);
            panel.add(button, constraints);
        }
    }

    private JPanel createCodeConfigPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        panel.setBorder(new TitledBorder(this.getText("Config")));

        this.baseClassTextField = new JTextField();
        this.baseClassTextField.setColumns(Short.MAX_VALUE);
        this.baseClassTextField.setText(BASECLASS);
        JLabel baseClassLabel = new JLabel(this.getText(BASE_CLASS), JLabel.RIGHT);
        baseClassLabel.setLabelFor(this.baseClassTextField);
        this.addComponents(panel, 0, baseClassLabel, this.baseClassTextField, null);

        this.pathTextField = new JTextField();
        this.pathTextField.setColumns(Short.MAX_VALUE);
        JLabel pathLabel = new JLabel(this.getText(PATH), JLabel.RIGHT);
        pathLabel.setLabelFor(this.pathTextField);
        JButton browseButton = new JButton(this.getText("Browse"));
        browseButton.setAlignmentX(CENTER_ALIGNMENT);
        browseButton.setMnemonic(KeyEvent.VK_B);
        browseButton.setDisplayedMnemonicIndex(browseButton.getText().indexOf("B"));
        browseButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DomainCodePanel.this.choicePath(DomainCodePanel.this.pathTextField);
            }
        });
        this.addComponents(panel, 1, pathLabel, this.pathTextField, browseButton);

        this.packageTextField = new JTextField();
        this.packageTextField.setColumns(Short.MAX_VALUE);
        JLabel packageLabel = new JLabel(this.getText(PACKAGE), JLabel.RIGHT);
        packageLabel.setLabelFor(this.packageTextField);
        JButton generateButton = new JButton(this.getText("Generate"));
        generateButton.setAlignmentX(CENTER_ALIGNMENT);
        generateButton.setMnemonic(KeyEvent.VK_G);
        generateButton.setDisplayedMnemonicIndex(generateButton.getText().indexOf("G"));
        generateButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DomainCodePanel.this.generate();
            }
        });
        this.addComponents(panel, 2, packageLabel, this.packageTextField, generateButton);

        return panel;
    }

    @SuppressWarnings("unchecked")
    private JPanel createDatabaseConfigPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
        panel.setBorder(new TitledBorder(this.getText(DATABASE)));

        this.databaseComboBox = new JComboBox(new Database[] { new MySqlDatabase(), new OracleDatabase(), new SqlServerDatabase() });
        this.databaseComboBox.setSize(Short.MAX_VALUE, 20);
        this.databaseComboBox.addActionListener(this);
        JLabel databaseTypeLabel = new JLabel(this.getText(DATABASE_TYPE), JLabel.RIGHT);
        databaseTypeLabel.setLabelFor(this.databaseComboBox);
        this.addComponents(panel, 0, databaseTypeLabel, this.databaseComboBox);

        this.serverTextField = new JTextField();
        this.serverTextField.setColumns(Short.MAX_VALUE);
        JLabel serverLabel = new JLabel(this.getText(SERVER), JLabel.RIGHT);
        serverLabel.setLabelFor(this.serverTextField);
        this.addComponents(panel, 1, serverLabel, this.serverTextField);

        this.portTextField = new JTextField();
        this.portTextField.setColumns(Short.MAX_VALUE);
        this.portTextField.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyTyped(KeyEvent keyEvent)
            {
                char keyChar = keyEvent.getKeyChar();
                if ((keyChar < '0' || keyChar > '9') && keyChar != ' ')
                {
                    Toolkit.getDefaultToolkit().beep();
                    keyEvent.setKeyChar('\0');
                }
            }
        });
        JLabel portLabel = new JLabel(this.getText(PORT), JLabel.RIGHT);
        portLabel.setLabelFor(this.portTextField);
        this.addComponents(panel, 2, portLabel, this.portTextField);

        this.databaseTextField = new JTextField();
        this.databaseTextField.setColumns(Short.MAX_VALUE);
        JLabel databaseLabel = new JLabel(this.getText(DATABASE_NAME), JLabel.RIGHT);
        databaseLabel.setLabelFor(this.databaseTextField);
        this.addComponents(panel, 3, databaseLabel, this.databaseTextField);

        this.userTextField = new JTextField();
        this.userTextField.setColumns(Short.MAX_VALUE);
        JLabel userLabel = new JLabel(this.getText(USER), JLabel.RIGHT);
        userLabel.setLabelFor(this.userTextField);
        this.addComponents(panel, 4, userLabel, this.userTextField);

        this.passwordTextField = new JPasswordField();
        this.passwordTextField.setColumns(Short.MAX_VALUE);
        JLabel passwordLabel = new JLabel(this.getText(PASSWORD), JLabel.RIGHT);
        passwordLabel.setLabelFor(this.passwordTextField);
        this.addComponents(panel, 5, passwordLabel, this.passwordTextField);

        JButton loadButton = new JButton(this.getText("LoadTables"));
        loadButton.setMargin(new Insets(0, 20, 0, 20));
        loadButton.setMnemonic(KeyEvent.VK_L);
        loadButton.setDisplayedMnemonicIndex(loadButton.getText().indexOf("L"));
        loadButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                DomainCodePanel.this.loadTables();
            }
        });
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.EAST;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.weightx = 0.2;
        constraints.gridx = 1;
        constraints.gridwidth = 1;
        constraints.gridy = 6;
        panel.add(loadButton, constraints);

        return panel;
    }

    private JPanel createDatabasePanel()
    {
        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.setPreferredSize(new Dimension(Short.MAX_VALUE, 210));
        panel.add(this.createDatabaseConfigPanel());

        this.tableList = new JCheckBoxList();
        this.tableList.setBorder(new TitledBorder(this.getText("Tables")));
        panel.add(this.tableList);
        return panel;
    }

    @Override
    protected Logger createLogger()
    {
        return new org.dangcat.commons.log.Logger(Logger.getLogger(DomainCodeGenerator.class), "EntityCallback", this.logPane);
    }

    private JPanel createLogPanel()
    {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(2, 2, 2, 2);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        this.logPane = new JLogPane();
        JScrollPane scrollPane = new JScrollPane(this.logPane);
        scrollPane.setBorder(new EtchedBorder());
        panel.add(scrollPane, constraints);
        return panel;
    }

    private void generate()
    {
        if (!this.validateEmpty(this.databaseTextField, DATABASE_NAME))
            return;
        if (!this.validateEmpty(this.serverTextField, SERVER))
            return;
        if (!this.validateEmpty(this.userTextField, USER))
            return;
        if (!this.validateEmpty(this.baseClassTextField, BASE_CLASS))
            return;
        if (!this.validateEmpty(this.pathTextField, PATH))
            return;
        if (!this.validateEmpty(this.packageTextField, PACKAGE))
            return;
        Collection<String> tableNames = this.tableList.getSelectedValues();
        if (tableNames == null || tableNames.isEmpty())
        {
            JOptionPane.showMessageDialog(this, this.getText("TablesBeEmpty"));
            return;
        }

        try
        {
            this.getLogger().info(this.getText("BeginGenerate") + ": " + this.pathTextField.getText());
            DomainCodeGenerator domainCodeGenerator = new DomainCodeGenerator();
            domainCodeGenerator.setParentComponent(this);
            domainCodeGenerator.setLogger(this.getLogger());
            domainCodeGenerator.setTableNames(tableNames);
            domainCodeGenerator.setBaseClassName(this.baseClassTextField.getText());
            domainCodeGenerator.setPackageName(this.packageTextField.getText());
            domainCodeGenerator.setOutputDir(this.pathTextField.getText());
            domainCodeGenerator.setDatabase((Database) this.databaseComboBox.getSelectedItem());
            domainCodeGenerator.generate();
            this.getLogger().info(this.getText("EndGenerate"));
        }
        catch (Exception e)
        {
            this.getLogger().error(this, e);
            JOptionPane.showMessageDialog(this, this.getText("GenerateError"));
        }
    }

    private void initDatabaseConfig()
    {
        if (this.textDocumentListener != null)
            this.textDocumentListener.setEnabled(false);

        Database database = (Database) this.databaseComboBox.getSelectedItem();
        this.databaseTextField.setText(database.getName());
        this.passwordTextField.setText(database.getPassword());
        this.portTextField.setText(database.getPort().toString());
        this.serverTextField.setText(database.getServer());
        this.userTextField.setText(database.getUser());

        if (this.textDocumentListener != null)
            this.textDocumentListener.setEnabled(true);
    }

    @Override
    public void initialize()
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.add(this.createDatabasePanel());
        this.add(this.createCodeConfigPanel());
        this.add(this.createLogPanel());
        this.initDatabaseConfig();

        this.textDocumentListener = new TextDocumentListener();
        this.textDocumentListener.setDatabaseComboBox(this.databaseComboBox);
        this.textDocumentListener.setDatabaseTextField(this.databaseTextField);
        this.textDocumentListener.setPasswordTextField(this.passwordTextField);
        this.textDocumentListener.setPortTextField(this.portTextField);
        this.textDocumentListener.setServerTextField(this.serverTextField);
        this.textDocumentListener.setUserTextField(this.userTextField);
        this.textDocumentListener.initialize();
    }

    private void loadTables()
    {
        try
        {
            Database database = (Database) this.databaseComboBox.getSelectedItem();
            TableManager tableManager = new TableManager(database);
            tableManager.load();
            this.tableList.clear();
            for (Table table : tableManager.getTables())
                this.tableList.addItem(table.getTableName());
        }
        catch (Exception e)
        {
            this.getLogger().error(this, e);
            JOptionPane.showMessageDialog(null, this.getText("LoadError"));
        }
    }
}
