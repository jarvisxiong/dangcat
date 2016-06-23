package org.dangcat.install.frame;

import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.module.ProcessModule;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.swing.ConfigTabPanel;
import org.dangcat.install.swing.ToolBarPanel;
import org.dangcat.install.swing.panel.InstallBodyPanel;
import org.dangcat.install.swing.panel.InstallHeaderPanel;
import org.dangcat.install.task.ProcessTask;
import org.dangcat.install.task.ProcessTaskManager;
import org.dangcat.swing.JFrameExt;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.LinkedList;

public abstract class FrameBase extends JFrameExt implements ActionListener
{
    private static final String BOF = "Bof";
    private static final String EOF = "Eof";
    private static final String NEXT = "next";
    private static final String PRIOR = "prior";
    private static final long serialVersionUID = 1L;
    private static Dimension PERFECT_SIZE = new Dimension(800, 600);
    private InstallBodyPanel bodyContainer = null;
    private InstallHeaderPanel headerContainer = null;
    private Collection<ProcessModule> processModules = new LinkedList<ProcessModule>();
    private ProcessTaskManager processTaskManager = null;
    private ToolBarPanel toolBarContainer = null;

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(ToolBarPanel.CANCEL))
            this.cancel();

        InstallBodyPanel bodyContainer = this.getBodyContainer();
        ToolBarPanel toolBarContainer = this.getToolBarContainer();
        if (actionEvent.getActionCommand().equals(PRIOR))
            bodyContainer.prior();
        else if (actionEvent.getActionCommand().equals(NEXT))
            bodyContainer.next();

        toolBarContainer.addState(EOF, bodyContainer.isEof());
        toolBarContainer.addState(BOF, bodyContainer.isBof());
    }

    protected void addProcessModule(ProcessModule processModule)
    {
        if (processModule != null)
            this.processModules.add(processModule);
    }

    protected void cancel()
    {
        this.getProcessTaskManager().cancel();
        this.getToolBarContainer().removeState(ToolBarPanel.PROCESS);
        this.getBodyContainer().prior();
    }

    private InstallBodyPanel createBodyContainer()
    {
        InstallBodyPanel bodyContainer = new InstallBodyPanel();
        this.createBodyContent(bodyContainer);
        bodyContainer.initialize();
        return bodyContainer;
    }

    protected void createBodyContent(InstallBodyPanel bodyContainer)
    {
        Collection<ProcessModule> processModules = this.getProcessModules();
        if (processModules != null && !processModules.isEmpty())
        {
            for (ProcessModule processModule : processModules)
            {
                Collection<ConfigPanel> configPanels = processModule.getConfigPanels();
                if (configPanels != null)
                {
                    ConfigPanel configPanel = null;
                    if (configPanels.size() == 1)
                    {
                        configPanel = configPanels.iterator().next();
                        configPanel.setBorder(new TitledBorder(configPanel.getTitle()));
                    }
                    else if (configPanels.size() > 1)
                        configPanel = this.createConfigTabPanel(configPanels);
                    if (configPanel != null)
                    {
                        bodyContainer.addContainer(processModule.getName(), configPanel);
                        processModule.setContainer(configPanel);
                        configPanel.setVisible(false);
                    }
                }
            }
        }
    }

    private ConfigTabPanel createConfigTabPanel(Collection<ConfigPanel> configPanels)
    {
        ConfigTabPanel configTabPanel = new ConfigTabPanel();
        configTabPanel.setConfigPanels(configPanels);
        configTabPanel.initialize();
        return configTabPanel;
    }

    @Override
    protected Container createContentPane()
    {
        JPanel content = new JPanel(new BorderLayout());
        content.setPreferredSize(PERFECT_SIZE);

        this.headerContainer = this.createHeaderContainer();
        content.add(this.headerContainer, BorderLayout.PAGE_START);

        this.bodyContainer = this.createBodyContainer();
        content.add(this.bodyContainer, BorderLayout.CENTER);

        this.toolBarContainer = this.createToolBarContainer();
        this.toolBarContainer.addActionListener(this);
        this.toolBarContainer.addState(EOF, this.bodyContainer.isEof());
        this.toolBarContainer.addState(BOF, this.bodyContainer.isBof());
        this.toolBarContainer.initialize();
        content.add(this.toolBarContainer, BorderLayout.PAGE_END);
        return content;
    }

    private InstallHeaderPanel createHeaderContainer()
    {
        InstallHeaderPanel headerContainer = new InstallHeaderPanel();
        headerContainer.setTitle(this.getTitle());
        if (!ValueUtils.isEmpty(this.getBackgroundName()))
            headerContainer.setBackgroundName(this.getBackgroundName());
        headerContainer.initialize();
        return headerContainer;
    }

    protected abstract ToolBarPanel createToolBarContainer();

    protected void executeFinished()
    {
        this.getToolBarContainer().removeState(ToolBarPanel.PROCESS);
        this.processTaskManager = null;
    }

    protected void executeProcess()
    {
        Collection<ProcessModule> processModules = this.getProcessModules();
        if (processModules != null && !processModules.isEmpty())
        {
            for (ProcessModule processModule : processModules)
            {
                if (processModule.isEnabled())
                    processModule.prepare();
            }
        }

        ProcessTaskManager processTaskManager = this.getProcessTaskManager();
        processTaskManager.setExecuteCallback(new Runnable()
        {
            @Override
            public void run()
            {
                FrameBase.this.executeFinished();
            }
        });
        processTaskManager.prepare();
        processTaskManager.start();
        this.getToolBarContainer().addState(ToolBarPanel.PROCESS, true);
    }

    protected String getBackgroundName()
    {
        return null;
    }

    protected InstallBodyPanel getBodyContainer()
    {
        return this.bodyContainer;
    }

    protected InstallHeaderPanel getHeaderContainer()
    {
        return this.headerContainer;
    }

    protected Collection<ProcessModule> getProcessModules()
    {
        return this.processModules;
    }

    protected ProcessTaskManager getProcessTaskManager()
    {
        if (this.processTaskManager == null)
        {
            ProcessTaskManager processTaskManager = new ProcessTaskManager();
            processTaskManager.setClassType(this.getClass());
            Collection<ProcessModule> processModules = this.getProcessModules();
            if (processModules != null && !processModules.isEmpty())
            {
                for (ProcessModule processModule : processModules)
                {
                    Collection<ProcessTask> processTasks = processModule.getProcessTasks();
                    if (processTasks != null)
                    {
                        for (ProcessTask processTask : processTasks)
                            processTaskManager.addTask(processTask);
                    }
                }
            }
            this.processTaskManager = processTaskManager;
        }
        return this.processTaskManager;
    }

    protected ToolBarPanel getToolBarContainer()
    {
        return this.toolBarContainer;
    }

    public abstract void initialize();

    protected boolean isSuccessFull()
    {
        ProcessTaskManager processTaskManager = this.getProcessTaskManager();
        return !processTaskManager.isCancel() && processTaskManager.getException() == null;
    }

    @Override
    public void pack()
    {
        this.initialize();
        super.pack();
    }

    protected void prepare()
    {
        this.getProcessTaskManager().prepare();
        this.getToolBarContainer().removeState(ToolBarPanel.PROCESS);
    }

    protected boolean validateData()
    {
        Collection<ProcessModule> processModules = this.getProcessModules();
        if (processModules != null && !processModules.isEmpty())
        {
            for (ProcessModule processModule : processModules)
            {
                if (processModule.isEnabled())
                {
                    Collection<ConfigPanel> configPanels = processModule.getConfigPanels();
                    if (configPanels != null)
                    {
                        for (ConfigPanel configPanel : configPanels)
                        {
                            if (!configPanel.validateData())
                                return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
