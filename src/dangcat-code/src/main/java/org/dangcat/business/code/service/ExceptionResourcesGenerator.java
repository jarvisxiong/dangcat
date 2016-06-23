package org.dangcat.business.code.service;

import org.dangcat.business.code.ResourceCodeGenerator;

import java.io.File;

public class ExceptionResourcesGenerator extends ResourceCodeGenerator {
    private static final String TEMPLATE = "Exception.properties";

    @Override
    protected String getOutputFile(String local) {
        return "exception" + File.separator + this.getParams().get("JndiName") + "Exception_" + local + ".properties";
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }
}
