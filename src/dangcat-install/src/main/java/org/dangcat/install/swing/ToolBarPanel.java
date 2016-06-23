package org.dangcat.install.swing;

import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ImageUtils;
import org.dangcat.swing.JToolBarPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public abstract class ToolBarPanel extends JToolBarPanel
{
    public static final String CANCEL = "cancel";
    public static final String FINISHED = "Finished";
    public static final String PROCESS = "Process";
    protected static final String EXIT = "exit";
    private static final long serialVersionUID = 1L;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());

    @Override
    public void actionPerformed(ActionEvent actionEvent)
    {
        if (actionEvent.getActionCommand().equals(CANCEL))
        {
            if (this.showConfirmDialog("PromptCancel") == JOptionPane.NO_OPTION)
                return;
        }
        if (actionEvent.getActionCommand().equals(EXIT))
        {
            if (this.showConfirmDialog("PromptExit") == JOptionPane.YES_OPTION)
                System.exit(0);
        }
        super.actionPerformed(actionEvent);
    }

    public void addButton(String name, int mnemonic, String mnemonicChar)
    {
        String title = this.resourceReader.getText("Button." + name);
        ImageIcon imageIcon = ImageUtils.loadImageIcon(this.getClass(), name + ".png");
        this.addButton(name, title, mnemonic, mnemonicChar, imageIcon);
    }

    @Override
    public void initialize()
    {
        super.initialize();
        this.addButton(EXIT, KeyEvent.VK_E, "E");
        this.checkButtonStates();
    }

    protected int showConfirmDialog(String key)
    {
        String title = this.resourceReader.getText("PromptTitle");
        String message = this.resourceReader.getText(key);
        return JOptionPane.showConfirmDialog(null, message, title, JOptionPane.YES_NO_OPTION);
    }
}
