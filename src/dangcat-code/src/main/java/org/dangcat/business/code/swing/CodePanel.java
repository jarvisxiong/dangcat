package org.dangcat.business.code.swing;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;

public abstract class CodePanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private Logger logger = null;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());

    protected void choicePath(JTextField pathTextField)
    {
        File currentDirectory = new File(pathTextField.getText());
        if (!currentDirectory.exists())
            currentDirectory = new File(".");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(this.getText("PathTitle"));
        fileChooser.setCurrentDirectory(currentDirectory);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
        {
            File file = fileChooser.getSelectedFile();
            if (!file.isDirectory())
            {
                JOptionPane.showMessageDialog(this, this.getText("PathNotExists"));
                return;
            }
            pathTextField.setText(file.getAbsolutePath());
        }
    }

    protected abstract Logger createLogger();

    protected Logger getLogger()
    {
        if (this.logger == null)
            this.logger = this.createLogger();
        return this.logger;
    }

    protected String getText(String key)
    {
        return this.resourceReader.getText(key);
    }

    public String getTitle()
    {
        return this.resourceReader.getText(this.getClass().getSimpleName());
    }

    public abstract void initialize();

    protected boolean validateEmpty(JTextField textField, String name)
    {
        boolean result = true;
        if (ValueUtils.isEmpty(textField.getText()))
        {
            String message = this.getText(name) + this.getText("CanNotBeEmpty");
            JOptionPane.showMessageDialog(this, message);
            result = false;
            textField.requestFocus();
        }
        return result;
    }
}
