package org.dangcat.install.swing.panel;

import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.InetAddressUtils;
import org.dangcat.commons.utils.NetUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.commons.validate.NameValidator;
import org.dangcat.commons.validate.PortValidator;
import org.dangcat.install.swing.ConfigPanel;

import javax.swing.*;
import java.io.File;

public class ValidateUtils
{
    private static NameValidator nameValidator = new NameValidator();
    private static PortValidator portValidator = new PortValidator();

    public static boolean validateBaseDir(ConfigPanel owner, JTextField textField, String name)
    {
        return validateEmpty(owner, textField, name);
    }

    public static boolean validateDatabaseName(ConfigPanel owner, JTextField textField, String name)
    {
        return validateName(owner, textField, name);
    }

    public static boolean validateDataDir(ConfigPanel owner, JTextField textField, String name)
    {
        return validateEmpty(owner, textField, name);
    }

    public static boolean validateEmpty(ConfigPanel owner, JTextField textField, String name)
    {
        boolean result = !ValueUtils.isEmpty(textField.getText());
        if (!result)
            validateError(owner, textField, name, "CanNotBeEmpty");
        return result;
    }

    private static void validateError(ConfigPanel owner, JTextField textField, String name, String key)
    {
        String message = owner.getText(name) + owner.getText(key);
        JOptionPane.showMessageDialog(owner, message);
        textField.requestFocus();
    }

    public static boolean validateInitPath(ConfigPanel owner, JTextField textField, String name)
    {
        boolean result = true;
        String initPath = textField.getText();
        if (!ValueUtils.isEmpty(initPath) && initPath.indexOf("\\") != -1)
            result = false;
        if (!result)
            validateError(owner, textField, name, "InitPathNotBeBackSlash");
        return result;
    }

    public static boolean validateInstallPath(ConfigPanel owner, JTextField textField, String name)
    {
        return validateInstallPath(owner, textField, textField.getText(), name);
    }

    public static boolean validateInstallPath(ConfigPanel owner, JTextField textField, String value, String name)
    {
        boolean result = FileUtils.isEmptyDirectory(new File(value));
        if (!result)
            validateError(owner, textField, name, "InstallNotBeEmpty");
        return result;
    }

    public static boolean validateName(ConfigPanel owner, JTextField textField, String name)
    {
        boolean result = validateEmpty(owner, textField, name);
        if (result)
        {
            result = nameValidator.isValid(textField.getText());
            if (!result)
                validateError(owner, textField, name, "NameError");
        }
        return result;
    }

    public static boolean validatePassword(ConfigPanel owner, JTextField textField, String name)
    {
        return validateEmpty(owner, textField, name);
    }

    public static boolean validatePassword2(ConfigPanel owner, JTextField textField1, JTextField textField2, String name)
    {
        boolean result = ValueUtils.compare(textField1.getText(), textField2.getText()) == 0;
        if (!result)
        {
            JOptionPane.showMessageDialog(owner, owner.getText(name));
            textField2.requestFocus();
        }
        return result;
    }

    public static boolean validatePort(ConfigPanel owner, JTextField textField, String name)
    {
        boolean result = validateEmpty(owner, textField, name);
        if (result)
        {
            result = portValidator.isValid(textField.getText());
            if (!result)
                validateError(owner, textField, name, "PortError");
        }
        return result;
    }

    public static boolean validatePortValid(ConfigPanel owner, JTextField textField, String name)
    {
        Integer port = ValueUtils.parseInt(textField.getText());
        boolean result = true;
        if (port != null)
        {
            result = NetUtils.isPortValid(port);
            if (!result)
                validateError(owner, textField, name, "PortValid");
        }
        return result;
    }

    public static boolean validateServer(ConfigPanel owner, JTextField textField, String name)
    {
        boolean result = validateEmpty(owner, textField, name);
        if (result)
        {
            result = InetAddressUtils.isInetAddress(textField.getText());
            if (!result)
            {
                result = nameValidator.isValid(textField.getText());
                if (!result)
                    validateError(owner, textField, name, "InetAddressError");
            }
        }
        return result;
    }

    public static boolean validateUser(ConfigPanel owner, JTextField textField, String name)
    {
        return validateName(owner, textField, name);
    }
}
