package org.dangcat.business.code.service;

import org.dangcat.business.code.CodeGenerator;

import java.io.File;

public class SimulatorCodeGenerator extends CodeGenerator
{
    private static final String TEMPLATE = "Simulator.java.template";

    @Override
    protected String getOutputFile()
    {
        String outputFile = "test" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + "simulate";
        outputFile += File.separator + this.getParams().get("JndiName") + "Simulator.java";
        return outputFile;
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
