package org.dangcat.install.swing.toolbar;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JOptionPane;

import org.dangcat.install.swing.ToolBarPanel;


public class InstallToolBar extends ToolBarPanel
{
    public static final String INSTALL = "install";
    private static final String NEXT = "next";
    private static final String PRIOR = "prior";
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(INSTALL))
        {
            if (this.showConfirmDialog("PromptInstall") == JOptionPane.NO_OPTION)
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
            this.setEnabled(PRIOR, false);
            this.setEnabled(NEXT, false);
            this.setEnabled(EXIT, false);
            this.setVisible(INSTALL, false);
            this.setVisible(CANCEL, true);
        }
        else
        {
            Boolean isBof = (Boolean) this.getState("Bof");
            this.setEnabled(PRIOR, isBof == null ? false : !isBof);
            Boolean isEof = (Boolean) this.getState("Eof");
            this.setEnabled(NEXT, isEof == null ? true : !isEof);
            this.setEnabled(EXIT, true);
            this.setVisible(INSTALL, true);
            this.setVisible(CANCEL, false);
        }
        Boolean isFinished = (Boolean) this.getState(FINISHED);
        if (Boolean.TRUE.equals(isFinished))
        {
            this.setVisible(INSTALL, false);
            this.setVisible(CANCEL, false);
        }
    }

    @Override
    public void initialize()
    {
        this.addButton(PRIOR, KeyEvent.VK_P, "P");
        this.addButton(NEXT, KeyEvent.VK_N, "N");
        this.addSeparator(40);
        this.addButton(CANCEL, KeyEvent.VK_C, "C");
        this.addButton(INSTALL, KeyEvent.VK_I, "I");
        super.initialize();
        this.setVisible(CANCEL, false);
    }
}
