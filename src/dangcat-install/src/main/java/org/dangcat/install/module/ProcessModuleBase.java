package org.dangcat.install.module;

import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.task.ProcessTask;

import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

public abstract class ProcessModuleBase implements ProcessModule
{
    private Collection<ConfigPanel> configPanels = new LinkedList<ConfigPanel>();
    private Container container = null;
    private File currentPath = null;
    private String displayName = null;
    private boolean enabled = true;
    private String name = null;
    private Collection<ProcessTask> processTasks = new LinkedList<ProcessTask>();
    private String title = null;

    public ProcessModuleBase(String name, String title)
    {
        this.name = name;
        this.title = title;
    }

    protected void addConfigPanel(ConfigPanel configPanel)
    {
        if (configPanel != null)
            this.configPanels.add(configPanel);
    }

    protected void addProcessTask(ProcessTask processTask)
    {
        if (processTask != null)
        {
            this.processTasks.add(processTask);
            processTask.setEnabled(this.isEnabled());
        }
    }

    public Collection<ConfigPanel> getConfigPanels()
    {
        return this.configPanels;
    }

    @Override
    public Container getContainer()
    {
        return this.container;
    }

    @Override
    public void setContainer(Container container) {
        this.container = container;
    }

    public File getCurrentPath()
    {
        return this.currentPath;
    }

    public void setCurrentPath(File currentPath) {
        this.currentPath = currentPath;
    }

    public String getDisplayName()
    {
        return this.displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName()
    {
        return this.name;
    }

    public Collection<ProcessTask> getProcessTasks()
    {
        return this.processTasks;
    }

    protected String getText(String key)
    {
        return ResourceUtils.getText(this.getClass(), key);
    }

    public String getTitle()
    {
        if (ValueUtils.isEmpty(this.title))
            return this.getText(this.name);
        return this.title;
    }

    public abstract void initialize();

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
        for (ProcessTask processTask : this.processTasks)
            processTask.setEnabled(this.isEnabled());
        if (this.container != null)
            this.container.setEnabled(this.isEnabled());
    }

    @Override
    public void prepare() {
    }
}
