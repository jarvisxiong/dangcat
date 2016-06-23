package org.dangcat.swing.fontchooser;

import javax.swing.*;
import java.awt.event.ActionEvent;

class DialogCancelAction extends AbstractAction {
    protected static final String ACTION_NAME = "Cancel";
    private static final long serialVersionUID = 1L;
    private JDialog dialog;
    private JFontChooser fontChooser;

    protected DialogCancelAction(JDialog dialog, JFontChooser fontChooser) {
        this.dialog = dialog;
        this.fontChooser = fontChooser;
        this.putValue(Action.DEFAULT, ACTION_NAME);
        this.putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
        this.putValue(Action.NAME, (ACTION_NAME));
    }

    public void actionPerformed(ActionEvent e) {
        this.fontChooser.dialogResultValue = JFontChooser.CANCEL_OPTION;
        this.dialog.setVisible(false);
    }
}
