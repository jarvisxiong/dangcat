package org.dangcat.business.code.service;

import java.io.File;

import org.dangcat.business.code.CodeGenerator;

public class FilterCodeGenerator extends CodeGenerator
{
    private static final String TEMPLATE = "Filter.java.template";

    @Override
    protected String getOutputFile()
    {
        String outputFile = "main" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + "filter";
        outputFile += File.separator + this.getParams().get("JndiName") + "Filter.java";
        return outputFile;
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
