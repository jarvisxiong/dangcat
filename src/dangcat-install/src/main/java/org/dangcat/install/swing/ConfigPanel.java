package org.dangcat.install.swing;

import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.install.swing.event.ValueChangedListener;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

public abstract class ConfigPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private boolean changed = false;
    private String name = null;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());
    private String title = null;

    private Collection<ValueChangedListener> valueChangedListeners = new LinkedList<ValueChangedListener>();

    protected void addGridBagSpace(JPanel panel, int y) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        constraints.gridy = y;
        constraints.weighty = 1.0;
        panel.add(new JLabel(), constraints);
    }

    public void addValueChangedListener(ValueChangedListener valueChangedListener) {
        if (valueChangedListener != null) {
            synchronized (this.valueChangedListeners) {
                this.valueChangedListeners.add(valueChangedListener);
            }
        }
    }

    protected void choicePath(JTextField pathTextField) {
        File currentDirectory = new File(pathTextField.getText());
        if (!currentDirectory.exists())
            currentDirectory = new File(".");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(this.getText("PathTitle"));
        fileChooser.setCurrentDirectory(currentDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.isDirectory()) {
                JOptionPane.showMessageDialog(this, this.getText("PathNotExists"));
                return;
            }
            pathTextField.setText(file.getAbsolutePath());
        }
    }

    public void fireValueChanged(EventObject eventObject) {
        if (eventObject != null) {
            synchronized (this.valueChangedListeners) {
                for (ValueChangedListener valueChangedListener : this.valueChangedListeners)
                    valueChangedListener.onValueChanged(eventObject);
            }
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    protected String getText(Class<?> classType) {
        return this.getText(classType.getSimpleName());
    }

    public String getText(String key) {
        return this.resourceReader.getText(key);
    }

    public String getTitle() {
        if (ValueUtils.isEmpty(this.title) && !ValueUtils.isEmpty(this.name))
            return this.getText(this.name);
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public abstract void initialize();

    public boolean isChanged() {
        return this.changed;
    }

    public void setChanged(boolean changed) {
        this.changed = changed;
        this.fireValueChanged(new EventObject(this));
    }

    protected boolean isChanged(Component component) {
        if (component instanceof ConfigPanel) {
            ConfigPanel configPanel = (ConfigPanel) component;
            if (configPanel != this && configPanel.isChanged())
                return true;
        }
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (this.isChanged(container.getComponent(i)))
                    return true;
            }
        }
        return false;
    }

    public void removeValueChangedListener(ValueChangedListener valueChangedListener) {
        if (valueChangedListener != null) {
            synchronized (this.valueChangedListeners) {
                this.valueChangedListeners.remove(valueChangedListener);
            }
        }
    }

    public void setChanged(Component component, boolean changed) {
        if (component instanceof ConfigPanel) {
            ConfigPanel configPanel = (ConfigPanel) component;
            configPanel.setChanged(changed);
        }
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++)
                this.setChanged(container.getComponent(i), changed);
        }
    }

    public boolean validateData() {
        return true;
    }

    protected boolean validateData(Component component) {
        if (component instanceof ConfigPanel) {
            ConfigPanel configPanel = (ConfigPanel) component;
            if (!configPanel.validateData())
                return false;
        }
        if (component instanceof Container) {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++) {
                if (!this.validateData(container.getComponent(i)))
                    return false;
            }
        }
        return true;
    }
}
