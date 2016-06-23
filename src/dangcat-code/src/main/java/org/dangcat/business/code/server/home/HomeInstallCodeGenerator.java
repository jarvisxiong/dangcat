package org.dangcat.business.code.server.home;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;

import java.io.File;

/**
 * 服务代码生成器。
 * @author dangcat
 * 
 */
public class HomeInstallCodeGenerator extends DirectoryCodeGenerator
{
    private static final String CODE_PATH = "home" + File.separator + "install";

    public HomeInstallCodeGenerator()
    {
        this.addCodeGenerator("linux", "install.sh.template", "install.sh");
        this.addCodeGenerator("windows", "install.bat.template", "install.bat");
    }

    private void addCodeGenerator(String path, String templateName, String outputFile)
    {
        String outputName = path + File.separator + outputFile;
        this.addCodeGenerator(new CodeGenerator(templateName, outputName));
    }

    @Override
    public String getCodePath()
    {
        return CODE_PATH;
    }
}
