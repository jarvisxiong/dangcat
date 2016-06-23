package org.dangcat.install.frame;

import org.dangcat.install.swing.ToolBarPanel;
import org.dangcat.install.swing.panel.InstallSelectPanel;
import org.dangcat.install.swing.panel.InstallSelectPathPanel;
import org.dangcat.install.swing.toolbar.InstallToolBar;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class InstallerFrame extends InstallProcessFrame {
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand().equals(InstallToolBar.INSTALL)) {
            if (this.getBodyContainer().validateData()) {
                this.getBodyContainer().last();
                this.executeInstall();
            }
        }
        super.actionPerformed(actionEvent);
    }

    @Override
    protected InstallSelectPanel createInstallSelectPanelInstancel() {
        return new InstallSelectPathPanel();
    }

    @Override
    protected ToolBarPanel createToolBarContainer() {
        return new InstallToolBar();
    }

    @Override
    protected void executeFinished() {
        String key = null;
        if (this.isSuccessFull()) {
            this.getToolBarContainer().addState(ToolBarPanel.FINISHED, true);
            key = "Install.Successfull";
        } else {
            this.getToolBarContainer().removeState(ToolBarPanel.FINISHED);
            key = "Install.Error";
        }
        String message = this.getText(key);
        JOptionPane.showMessageDialog(this, message);
        super.executeFinished();
    }

    protected void executeInstall() {
        this.logger.info("Install process begin execute.");
        super.executeProcess();
    }
}
