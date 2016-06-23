package org.dangcat.business.code.service;

import org.dangcat.business.code.CodeGenerator;

import java.io.File;

public class ViewCodeGenerator extends CodeGenerator
{
    private static final String TEMPLATE = "View.java.template";

    @Override
    protected String getOutputFile()
    {
        String outputFile = "main" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + "domain";
        outputFile += File.separator + this.getParams().get("JndiName") + "View.java";
        return outputFile;
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
