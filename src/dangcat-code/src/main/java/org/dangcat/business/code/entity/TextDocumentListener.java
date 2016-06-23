package org.dangcat.business.code.entity;

import org.dangcat.commons.database.Database;
import org.dangcat.commons.utils.ValueUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TextDocumentListener implements DocumentListener {
    @SuppressWarnings("unchecked")
    private JComboBox databaseComboBox = null;
    private JTextField databaseTextField = null;
    private boolean enabled = true;
    private JPasswordField passwordTextField = null;
    private JTextField portTextField = null;
    private JTextField serverTextField = null;
    private JTextField userTextField = null;

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.changedValue();
    }

    public void changedValue() {
        if (this.isEnabled()) {
            Database database = (Database) this.databaseComboBox.getSelectedItem();
            database.setName(this.databaseTextField.getText());
            database.setPassword(new String(this.passwordTextField.getPassword()));
            database.setPort(ValueUtils.parseInt(this.portTextField.getText()));
            database.setServer(this.serverTextField.getText());
            database.setUser(this.userTextField.getText());
        }
    }

    @SuppressWarnings("unchecked")
    public JComboBox getDatabaseComboBox() {
        return databaseComboBox;
    }

    @SuppressWarnings("unchecked")
    public void setDatabaseComboBox(JComboBox databaseComboBox) {
        this.databaseComboBox = databaseComboBox;
    }

    public JTextField getDatabaseTextField() {
        return databaseTextField;
    }

    public void setDatabaseTextField(JTextField databaseTextField) {
        this.databaseTextField = databaseTextField;
    }

    public JPasswordField getPasswordTextField() {
        return passwordTextField;
    }

    public void setPasswordTextField(JPasswordField passwordTextField) {
        this.passwordTextField = passwordTextField;
    }

    public JTextField getPortTextField() {
        return portTextField;
    }

    public void setPortTextField(JTextField portTextField) {
        this.portTextField = portTextField;
    }

    public JTextField getServerTextField() {
        return serverTextField;
    }

    public void setServerTextField(JTextField serverTextField) {
        this.serverTextField = serverTextField;
    }

    public JTextField getUserTextField() {
        return userTextField;
    }

    public void setUserTextField(JTextField userTextField) {
        this.userTextField = userTextField;
    }

    public void initialize() {
        this.databaseTextField.getDocument().addDocumentListener(this);
        this.passwordTextField.getDocument().addDocumentListener(this);
        this.portTextField.getDocument().addDocumentListener(this);
        this.serverTextField.getDocument().addDocumentListener(this);
        this.userTextField.getDocument().addDocumentListener(this);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.changedValue();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.changedValue();
    }
}
