package org.dangcat.business.code.entity;

import org.dangcat.business.code.CodeGenerator;

import java.io.File;

/**
 * 实体对象代码产生工具。
 *
 * @author dangcat
 */
public class EntityCodeGenerator extends CodeGenerator {
    private static final String TEMPLATE = "Entity.java.template";

    @Override
    protected String getOutputFile() {
        String outputFile = "main" + File.separator + "java";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + this.getParams().get("entityName") + ".java";
        return outputFile;
    }

    @Override
    protected String getTemplate() {
        return TEMPLATE;
    }
}
