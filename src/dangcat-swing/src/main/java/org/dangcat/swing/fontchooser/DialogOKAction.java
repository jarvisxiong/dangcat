package org.dangcat.swing.fontchooser;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;

class DialogOKAction extends AbstractAction
{
    protected static final String ACTION_NAME = "OK";
    private static final long serialVersionUID = 1L;
    private JDialog dialog;
    private JFontChooser fontChooser;

    protected DialogOKAction(JDialog dialog, JFontChooser fontChooser)
    {
        this.dialog = dialog;
        this.fontChooser = fontChooser;
        this.putValue(Action.DEFAULT, ACTION_NAME);
        this.putValue(Action.ACTION_COMMAND_KEY, ACTION_NAME);
        this.putValue(Action.NAME, (ACTION_NAME));
    }

    public void actionPerformed(ActionEvent e)
    {
        this.fontChooser.dialogResultValue = JFontChooser.OK_OPTION;
        this.dialog.setVisible(false);
    }
}
