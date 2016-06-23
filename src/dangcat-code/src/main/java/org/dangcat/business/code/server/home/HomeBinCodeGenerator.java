package org.dangcat.business.code.server.home;

import java.io.File;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;

/**
 * 服务代码生成器。
 * @author dangcat
 * 
 */
public class HomeBinCodeGenerator extends DirectoryCodeGenerator
{
    private static final String CODE_PATH = "home" + File.separator + "bin";
    private String serverName = null;

    public HomeBinCodeGenerator(String serverName)
    {
        this.serverName = serverName;
        this.addCodeGenerator("linux", "server-config.sh.template", "config.sh");
        this.addCodeGenerator("linux", "server-service.sh.template", "service.sh");
        this.addCodeGenerator("linux", "server-uninstall.sh.template", "uninstall.sh");
        this.addCodeGenerator("windows", "server-config.bat.template", "config.bat");
        this.addCodeGenerator("windows", "server-service.bat.template", "service.bat");
        this.addCodeGenerator("windows", "server-uninstall.bat.template", "uninstall.bat");
    }

    private void addCodeGenerator(String path, String templateName, String outputFile)
    {
        String outputName = path + File.separator + this.serverName + "-" + outputFile;
        this.addCodeGenerator(new CodeGenerator(templateName, outputName));
    }

    @Override
    public String getCodePath()
    {
        return CODE_PATH;
    }
}
