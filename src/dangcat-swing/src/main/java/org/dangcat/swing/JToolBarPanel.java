package org.dangcat.swing;

import org.dangcat.commons.utils.ValueUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public abstract class JToolBarPanel extends JPanel implements ActionListener {
    private static final long serialVersionUID = 1L;

    private Map<String, JButton> buttonsMap = new HashMap<String, JButton>();
    private Map<String, Object> stateMap = new HashMap<String, Object>();

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        this.fireActionPerformed(actionEvent);
        this.checkButtonStates();
    }

    public void addActionListener(ActionListener actionListener) {
        this.listenerList.add(ActionListener.class, actionListener);
    }

    public void addButton(String name, String title, int mnemonic, String mnemonicChar, ImageIcon imageIcon) {
        JButton button = new JButton(title, imageIcon);
        button.setActionCommand(name);
        button.setMnemonic(mnemonic);
        button.setDisplayedMnemonicIndex(button.getText().indexOf(mnemonicChar));
        button.addActionListener(this);
        this.buttonsMap.put(name, button);
        this.add(button);
    }

    public void addSeparator(int width) {
        this.add(Box.createRigidArea(new Dimension(width, 0)));
    }

    public void addState(String key, Object value) {
        Object saveValue = this.getState(key);
        if (ValueUtils.compare(saveValue, value) != 0) {
            this.stateMap.put(key, value);
            this.checkButtonStates();
        }
    }

    public abstract void checkButtonStates();

    protected void fireActionPerformed(ActionEvent event) {
        Object[] listeners = this.listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ActionListener.class) {
                ActionListener actionListener = ((ActionListener) listeners[i + 1]);
                actionListener.actionPerformed(event);
            }
        }
    }

    public Object getState(String key) {
        return this.stateMap.get(key);
    }

    public void initialize() {
        this.setBorder(new TitledBorder(""));
        this.setMaximumSize(new Dimension(Short.MAX_VALUE, 100));
        this.setLayout(new FlowLayout(FlowLayout.RIGHT));
    }

    public void removeActionListener(ActionListener actionListener) {
        if (actionListener != null)
            this.listenerList.remove(ActionListener.class, actionListener);
    }

    public void removeState(String key) {
        this.stateMap.remove(key);
        this.checkButtonStates();
    }

    public void setEnabled(String name, boolean value) {
        JButton button = this.buttonsMap.get(name);
        if (button != null)
            button.setEnabled(value);
    }

    public void setVisible(String name, boolean value) {
        JButton button = this.buttonsMap.get(name);
        if (button != null)
            button.setVisible(value);
    }
}
