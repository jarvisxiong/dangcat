package org.dangcat.install.swing.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.dangcat.install.swing.ToolBarPanel;


public class UninstallToolBar extends ToolBarPanel
{
    private static final long serialVersionUID = 1L;
    public static final String UNINSTALL = "uninstall";

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
