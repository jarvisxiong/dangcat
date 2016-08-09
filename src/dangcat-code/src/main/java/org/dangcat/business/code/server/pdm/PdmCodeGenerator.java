package org.dangcat.business.code.server.pdm;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;

/**
 * 服务代码生成器。
 *
 * @author dangcat
 */
public class PdmCodeGenerator extends DirectoryCodeGenerator {
    private static final String CODE_PATH = "pdm";

    public PdmCodeGenerator(String serverName) {
        this.addCodeGenerator(new CodeGenerator("server-service.pdm.template", serverName + "-service.pdm", true));
    }

    @Override
    public String getCodePath() {
        return CODE_PATH;
    }
}
