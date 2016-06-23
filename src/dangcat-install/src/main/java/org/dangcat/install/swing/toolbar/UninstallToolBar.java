package org.dangcat.install.swing.toolbar;

import org.dangcat.install.swing.ToolBarPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


public class UninstallToolBar extends ToolBarPanel
{
    public static final String UNINSTALL = "uninstall";
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(UNINSTALL))
        {
            if (this.showConfirmDialog("PromptUninstall") == JOptionPane.NO_OPTION)
                return;
        }
        super.actionPerformed(actionEvent);
    }

    @Override
    public void checkButtonStates()
    {
        Boolean isProcess = (Boolean) this.getState(PROCESS);
        if (Boolean.TRUE.equals(isProcess))
        {
            this.setEnabled(EXIT, false);
            this.setVisible(UNINSTALL, false);
            this.setVisible(CANCEL, true);
        }
        else
        {
            this.setEnabled(EXIT, true);
            this.setVisible(UNINSTALL, true);
            this.setVisible(CANCEL, false);
        }
        Boolean isFinished = (Boolean) this.getState(FINISHED);
        if (Boolean.TRUE.equals(isFinished))
        {
            this.setVisible(UNINSTALL, false);
            this.setVisible(CANCEL, false);
        }
    }

    @Override
    public void initialize()
    {
        this.addButton(CANCEL, KeyEvent.VK_C, "C");
        this.addButton(UNINSTALL, KeyEvent.VK_I, "U");
        super.initialize();
        this.setVisible(CANCEL, false);
    }
}
