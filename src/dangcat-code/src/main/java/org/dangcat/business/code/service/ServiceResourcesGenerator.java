package org.dangcat.business.code.service;

import java.io.File;

import org.dangcat.business.code.ResourceCodeGenerator;

public class ServiceResourcesGenerator extends ResourceCodeGenerator
{
    private static final String TEMPLATE = "Service.properties";

    @Override
    protected String getOutputFile(String local)
    {
        return "service" + File.separator + this.getParams().get("JndiName") + "Service_" + local + ".properties";
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
