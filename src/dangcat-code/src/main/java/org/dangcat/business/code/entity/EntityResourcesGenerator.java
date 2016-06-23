package org.dangcat.business.code.entity;

import org.dangcat.business.code.ResourceCodeGenerator;

public class EntityResourcesGenerator extends ResourceCodeGenerator
{
    private static final String TEMPLATE = "Entity.properties";

    @Override
    protected String getOutputFile(String local)
    {
        return this.getParams().get("entityName") + "_" + local + ".properties";
    }

    @Override
    protected String getTemplate()
    {
        return TEMPLATE;
    }
}
