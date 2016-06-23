package org.dangcat.install.module;

import java.awt.Container;
import java.util.Collection;

import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.task.ProcessTask;


public interface ProcessModule
{
    public Collection<ConfigPanel> getConfigPanels();

    public Container getContainer();

    public String getName();

    public Collection<ProcessTask> getProcessTasks();

    public String getTitle();

    public boolean isEnabled();

    public void prepare();

    public void setContainer(Container container);
}
