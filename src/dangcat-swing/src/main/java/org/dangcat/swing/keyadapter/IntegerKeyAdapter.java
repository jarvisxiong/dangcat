package org.dangcat.swing.keyadapter;

import java.awt.*;
import java.awt.event.KeyEvent;

public class IntegerKeyAdapter extends MaxLengthKeyAdapter {
    private static final int MAX_LENGTH = 10;

    public IntegerKeyAdapter() {
        this(MAX_LENGTH);
    }

    public IntegerKeyAdapter(int maxLength) {
        super(maxLength);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        super.keyTyped(keyEvent);
        char keyChar = keyEvent.getKeyChar();
        if ((keyChar < '0' || keyChar > '9') && keyChar != ' ') {
            Toolkit.getDefaultToolkit().beep();
            keyEvent.setKeyChar('\0');
        }
    }
}
