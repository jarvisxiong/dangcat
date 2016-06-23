package org.dangcat.install.module;

import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.task.ProcessTask;

import java.awt.*;
import java.util.Collection;


public interface ProcessModule {
    Collection<ConfigPanel> getConfigPanels();

    Container getContainer();

    void setContainer(Container container);

    String getName();

    Collection<ProcessTask> getProcessTasks();

    String getTitle();

    boolean isEnabled();

    void prepare();
}
