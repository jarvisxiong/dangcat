package org.dangcat.business.code.service;

import org.dangcat.business.code.DirectoryCodeGenerator;

/**
 * 业务层代码生成器。
 *
 * @author dangcat
 */
public class BusinessCodeGenerator extends DirectoryCodeGenerator {
    private static final String JNDI_NAME = "JndiName";
    private static final String MODULE_NAME = "ModuleName";

    public BusinessCodeGenerator(String moduleName, String jndiName) {
        this.setModuleName(moduleName);
        this.setJndiName(jndiName);
        this.addCodeGenerator(new ExceptionCodeGenerator());
        this.addCodeGenerator(new ExceptionResourcesGenerator());
        this.addCodeGenerator(new FilterCodeGenerator());
        this.addCodeGenerator(new ServiceResourcesGenerator());
        this.addCodeGenerator(new ServiceCodeGenerator());
        this.addCodeGenerator(new ServiceImplCodeGenerator());
        this.addCodeGenerator(new ValidatorCodeGenerator());
        this.addCodeGenerator(new TestServiceCodeGenerator());
        this.addCodeGenerator(new SimulatorCodeGenerator());
        this.addCodeGenerator(new QueryCodeGenerator());
        this.addCodeGenerator(new ViewCodeGenerator());
        this.addCodeGenerator(new CalculatorCodeGenerator());
        this.addCodeGenerator(new JsfCodeGenerator(moduleName, jndiName));
    }

    public String getJndiName() {
        return (String) this.getParams().get(JNDI_NAME);
    }

    public void setJndiName(String jndiName) {
        this.getParams().put(JNDI_NAME, jndiName);
    }

    public String getModuleName() {
        return (String) this.getParams().get(MODULE_NAME);
    }

    public void setModuleName(String moduleName) {
        this.getParams().put(MODULE_NAME, moduleName);
    }
}
