package org.dangcat.install.swing.panel;

import org.dangcat.install.swing.ConfigPanel;
import org.dangcat.install.swing.event.ValueChangedListener;

import java.awt.*;
import java.util.*;


public class InstallBodyPanel extends ConfigPanel implements ValueChangedListener
{
    private static final long serialVersionUID = 1L;
    private CardLayout cardLayout = new CardLayout();
    private Map<String, Container> containers = new LinkedHashMap<String, Container>();
    private boolean containsProcessPanel = false;
    private int current = 0;

    public void addContainer(String name, Container container)
    {
        if (container != null)
        {
            this.containers.put(name, container);
            this.addValueChangedListener(container);
        }
    }

    private void addValueChangedListener(Component component)
    {
        if (component instanceof ConfigPanel)
        {
            ConfigPanel configPanel = (ConfigPanel) component;
            configPanel.addValueChangedListener(this);
        }
        if (component instanceof Container)
        {
            Container container = (Container) component;
            for (int i = 0; i < container.getComponentCount(); i++)
                this.addValueChangedListener(container.getComponent(i));
        }
    }

    public Container getContainer(String name)
    {
        return this.containers.get(name);
    }

    @Override
    public void initialize()
    {
        for (Container container : this.containers.values())
        {
            if (container.isEnabled())
                this.add(container);
        }
        this.setLayout(this.cardLayout);
    }

    public boolean isBof()
    {
        return this.current == 0;
    }

    @Override
    public boolean isChanged()
    {
        return super.isChanged(this);
    }

    public boolean isContainsProcessPanel()
    {
        return this.containsProcessPanel;
    }

    public void setContainsProcessPanel(boolean containsProcessPanel) {
        this.containsProcessPanel = containsProcessPanel;
    }

    public boolean isEof()
    {
        return this.current >= this.getComponentCount() - (this.isContainsProcessPanel() ? 2 : 1);
    }

    public void last()
    {
        this.cardLayout.last(this);
        this.current = this.getComponentCount() - 1;
    }

    public boolean next()
    {
        if (!this.isEof() && this.validateCurrentData())
        {
            this.cardLayout.next(this);
            this.current++;
            return true;
        }
        return false;
    }

    @Override
    public void onValueChanged(EventObject eventObject)
    {
        this.fireValueChanged(eventObject);
    }

    public boolean prior()
    {
        if (!this.isBof() && this.validateCurrentData())
        {
            this.cardLayout.previous(this);
            this.current--;
            return true;
        }
        return false;
    }

    public void removeContainer(String name)
    {
        Container container = this.containers.get(name);
        if (container != null)
        {
            this.remove(container);
            this.containers.remove(name);
        }
    }

    public void updateContent(Container position)
    {
        Container found = null;
        Collection<Container> containers = new LinkedList<Container>();
        for (Container container : this.containers.values())
        {
            if (found == null)
            {
                if (container == position)
                    found = position;
            }
            else
            {
                this.remove(container);
                containers.add(container);
            }
        }
        for (Container container : containers)
        {
            if (container.isEnabled())
                this.add(container);
        }
        this.doLayout();
    }

    public boolean validateCurrentData()
    {
        return this.validateData(this.getComponent(this.current));
    }

    @Override
    public boolean validateData()
    {
        boolean result = true;
        for (int i = this.current; result && i < this.getComponentCount(); i++)
        {
            result = this.validateCurrentData();
            if (result)
            {
                this.cardLayout.next(this);
                this.current++;
            }
        }
        return result;
    }
}
