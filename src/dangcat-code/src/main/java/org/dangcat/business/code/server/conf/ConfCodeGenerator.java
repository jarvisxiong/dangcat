package org.dangcat.business.code.server.conf;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.commons.io.FileUtils;

/**
 * 服务代码生成器。
 *
 * @author dangcat
 */
public class ConfCodeGenerator extends DirectoryCodeGenerator {
    private static final String CODE_PATH = "conf";

    public ConfCodeGenerator(String serverName) {
        this.addCodeGenerator(new CodeGenerator("server.cache.xml.template", serverName + ".cache.xml", true));
        this.addCodeGenerator(new CodeGenerator("server.log4j.properties.template", serverName + ".log4j.properties"));
        this.addCodeGenerator(new CodeGenerator("server.menus.xml.template", serverName + ".menus.xml", true));
        this.addCodeGenerator(new CodeGenerator("server.permissions.xml.template", serverName + ".permissions.xml", true));
        this.addCodeGenerator(new CodeGenerator("server.resource.properties.template", serverName + ".resource.properties"));
        this.addCodeGenerator(new CodeGenerator("server.server.properties.template", serverName + ".server.properties"));
        this.addCodeGenerator(new CodeGenerator("server.server.xml.template", serverName + ".server.xml", FileUtils.ENCODING_UTF_8));
        this.addCodeGenerator(new CodeGenerator("server.servicebeans.xml.template", serverName + ".servicebeans.xml", true));
        this.addCodeGenerator(new CodeGenerator("server.users.properties.template", serverName + ".users.properties", true));
    }

    @Override
    public String getCodePath() {
        return CODE_PATH;
    }
}
