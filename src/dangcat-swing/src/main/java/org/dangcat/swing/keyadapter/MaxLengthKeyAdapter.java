package org.dangcat.swing.keyadapter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MaxLengthKeyAdapter extends KeyAdapter {
    private int maxLength = 0;

    public MaxLengthKeyAdapter(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        int textLength = 0;
        if (this.getMaxLength() != 0) {
            if (keyEvent.getSource() instanceof JTextField) {
                JTextField textFiled = (JTextField) keyEvent.getSource();
                textLength = textFiled.getText().length();
            }
            if (textLength > this.getMaxLength()) {
                Toolkit.getDefaultToolkit().beep();
                keyEvent.setKeyChar('\0');
            }
        }
    }
}
