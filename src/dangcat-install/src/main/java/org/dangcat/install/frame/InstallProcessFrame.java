package org.dangcat.install.frame;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import org.dangcat.install.swing.panel.InstallBodyPanel;
import org.dangcat.install.swing.panel.InstallProgressPanel;
import org.dangcat.install.swing.panel.InstallSelectPanel;
import org.dangcat.install.task.ProcessTaskManager;

public abstract class InstallProcessFrame extends FrameBase
{
    private static final long serialVersionUID = 1L;
    private boolean bodyContentChanged = false;
    private boolean cancel = false;
    private File currentPath = new File(".");
    private InstallProgressPanel installProgressPanel = null;
    private InstallSelectPanel installSelectPanel = null;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (this.bodyContentChanged)
            this.updateBodyContent();

        super.actionPerformed(actionEvent);
    }

    @Override
    protected void cancel()
    {
        this.cancel = true;
        super.cancel();
    }

    @Override
    protected void createBodyContent(InstallBodyPanel bodyContainer)
    {
        bodyContainer.setContainsProcessPanel(true);
        this.installSelectPanel = this.createInstallSelectPanel();
        bodyContainer.addContainer(InstallSelectPanel.class.getSimpleName(), this.installSelectPanel);

        super.createBodyContent(bodyContainer);

        this.installProgressPanel = this.createInstallProgressPanel();
        bodyContainer.addContainer(InstallProgressPanel.class.getSimpleName(), this.installProgressPanel);
    }

    @Override
    protected Container createContentPane()
    {
        Container container = super.createContentPane();
        this.onComponentsSelecedChanged();
        return container;
    }

    private InstallProgressPanel createInstallProgressPanel()
    {
        InstallProgressPanel installProgressPanel = new InstallProgressPanel();
        installProgressPanel.initialize();
        installProgressPanel.setMin(0);
        installProgressPanel.setMax(100);
        installProgressPanel.setValue(0);
        return installProgressPanel;
    }

    protected InstallSelectPanel createInstallSelectPanel()
    {
        InstallSelectPanel installSelectPanel = this.createInstallSelectPanelInstancel();
        installSelectPanel.initialize();
        installSelectPanel.getComponentList().addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                InstallProcessFrame.this.onComponentsSelecedChanged();
            }
        });
        return installSelectPanel;
    }

    protected InstallSelectPanel createInstallSelectPanelInstancel()
    {
        return new InstallSelectPanel();
    }

    @Override
    protected void executeProcess()
    {
        this.cancel = false;
        this.installProgressPanel.reset();
        super.executeProcess();
    }

    public File getCurrentPath()
    {
        return this.currentPath;
    }

    public InstallProgressPanel getInstallProgressPanel()
    {
        return this.installProgressPanel;
    }

    public InstallSelectPanel getInstallSelectPanel()
    {
        return this.installSelectPanel;
    }

    @Override
    protected ProcessTaskManager getProcessTaskManager()
    {
        ProcessTaskManager processTaskManager = super.getProcessTaskManager();
        processTaskManager.setProgressPanel(this.installProgressPanel);
        return processTaskManager;
    }

    public boolean isCancel()
    {
        return this.cancel;
    }

    protected void onComponentsSelecedChanged()
    {
        this.bodyContentChanged = true;
    }

    public void setCurrentPath(File currenPath)
    {
        this.currentPath = currenPath;
    }

    protected void updateBodyContent()
    {
        this.getBodyContainer().updateContent(this.installSelectPanel);
        this.bodyContentChanged = false;
    }
}