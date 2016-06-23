package org.dangcat.business.code.service;

import java.io.File;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.commons.io.FileUtils;

public class JsfCodeGenerator extends DirectoryCodeGenerator
{
    public JsfCodeGenerator(String moduleName, String jndiName)
    {
        String outputFile = "main" + File.separator + "webapp" + File.separator + "Modules";
        outputFile += File.separator + moduleName;
        outputFile += File.separator + jndiName;
        outputFile += File.separator + jndiName;
        this.addCodeGenerator(new CodeGenerator("Edit.jsf.template", outputFile + "Main.jsf", FileUtils.ENCODING_UTF_8));
        this.addCodeGenerator(new CodeGenerator("Main.jsf.template", outputFile + "Edit.jsf", FileUtils.ENCODING_UTF_8));
    }
}
