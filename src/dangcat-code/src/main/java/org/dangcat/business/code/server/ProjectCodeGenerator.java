package org.dangcat.business.code.server;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.commons.io.FileUtils;

/**
 * 服务代码生成器。
 *
 * @author dangcat
 */
public class ProjectCodeGenerator extends DirectoryCodeGenerator {
    public ProjectCodeGenerator() {
        this.addCodeGenerator(new CodeGenerator(".classpath.template", ".classpath", true));
        this.addCodeGenerator(new CodeGenerator(".project.template", ".project", FileUtils.ENCODING_UTF_8));
        this.addCodeGenerator(new CodeGenerator("assembly.xml.template", "assembly.xml", true));
        this.addCodeGenerator(new CodeGenerator("pom.xml.template", "pom.xml", FileUtils.ENCODING_UTF_8));
    }
}
