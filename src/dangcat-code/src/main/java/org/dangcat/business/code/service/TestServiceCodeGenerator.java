package org.dangcat.business.code.service;

import org.dangcat.business.code.CodeGenerator;

import java.io.File;

public class TestServiceCodeGenerator extends CodeGenerator {
    private static final String TEMPLATE = "TestService.java.template";

    @Override
    protected String getOutputFile() {
        String outputFile = "test" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + "service";
        outputFile += File.separator + "Test" + this.getParams().get("JndiName") + "Service.java";
        return outputFile;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }
}
