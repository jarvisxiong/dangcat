package org.dangcat.business.code.service;

import org.dangcat.business.code.CodeGenerator;

import java.io.File;

public class ServiceImplCodeGenerator extends CodeGenerator
{
    private static final String TEMPLATE = "ServiceImpl.java.template";

    @Override
    protected String getOutputFile()
    {
        String outputFile = "main" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + "service";
        outputFile += File.separator + "impl";
        outputFile += File.separator + this.getParams().get("JndiName") + "ServiceImpl.java";
        return outputFile;
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
