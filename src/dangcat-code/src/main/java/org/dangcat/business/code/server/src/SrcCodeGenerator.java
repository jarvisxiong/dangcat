package org.dangcat.business.code.server.src;

import org.dangcat.business.code.CodeGenerator;
import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.commons.io.FileUtils;

import java.io.File;

/**
 * 服务代码生成器。
 * @author dangcat
 * 
 */
public class SrcCodeGenerator extends DirectoryCodeGenerator
{
    private static final String CODE_PATH = "src";
    private String packageName = null;

    public SrcCodeGenerator(String packageName, String serverName)
    {
        this.packageName = packageName;

        String outputName = serverName.substring(0, 1).toUpperCase() + serverName.substring(1);
        this.addMainJavaCodeGenerator("Server.java.template", outputName + "Server.java", false);
        this.addMainResourcesCodeGenerator("Server_zh_CN.properties.template", outputName + "Server_zh_CN.properties", false);
        this.addMainResourcesCodeGenerator("Server_en_US.properties.template", outputName + "Server_en_US.properties", false);

        outputName = "install" + File.separator;
        this.addMainJavaCodeGenerator("ConfigFrame.java.template", outputName + "ConfigFrame.java", false);
        this.addMainJavaCodeGenerator("InstallFrame.java.template", outputName + "InstallFrame.java", false);
        this.addMainJavaCodeGenerator("UninstallFrame.java.template", outputName + "UninstallFrame.java", false);
        this.addMainResourcesCodeGenerator("ConfigFrame.png", outputName + "ConfigFrame.png", true);
        this.addMainResourcesCodeGenerator("InstallerFrame.png", outputName + "InstallerFrame.png", true);
        this.addMainResourcesCodeGenerator("UninstallerFrame.png", outputName + "UninstallerFrame.png", true);
        this.addMainResourcesCodeGenerator("install_zh_CN.properties", outputName + "package_zh_CN.properties", false);
        this.addMainResourcesCodeGenerator("install_en_US.properties", outputName + "package_en_US.properties", false);

        outputName = "META-INF" + File.separator;
        this.addTestResourcesCodeGenerator("test.resource.properties.template", null, outputName + "resource.properties", false);

        outputName = "scene" + File.separator;
        this.addWebAppResourceCodeGenerator("SceneContext.js.template", outputName + "SceneContext.js", false, FileUtils.ENCODING_UTF_8);
        this.addWebAppResourceCodeGenerator("SceneContext_zh_CN.properties.template", outputName + "SceneContext_zh_CN.properties", false, FileUtils.ENCODING_UTF_8);
    }

    private void addMainJavaCodeGenerator(String templateName, String outputName, boolean useCopy)
    {
        String outputPath = "main" + File.separator + "java" + File.separator + this.packageName.replace(".", File.separator);
        this.addCodeGenerator(new CodeGenerator(templateName, outputPath + File.separator + outputName, useCopy));
    }

    private void addMainResourcesCodeGenerator(String templateName, String outputName, boolean useCopy)
    {
        String outputPath = "main" + File.separator + "resources" + File.separator + this.packageName.replace(".", File.separator);
        this.addCodeGenerator(new CodeGenerator(templateName, outputPath + File.separator + outputName, useCopy));
    }

    private void addTestResourcesCodeGenerator(String templateName, String packageName, String outputName, boolean useCopy)
    {
        String outputPath = "test" + File.separator + "resources" + File.separator;
        if (packageName != null)
            outputPath += packageName.replace(".", File.separator);
        this.addCodeGenerator(new CodeGenerator(templateName, outputPath + File.separator + outputName, useCopy));
    }

    private void addWebAppResourceCodeGenerator(String templateName, String outputName, boolean useCopy, String encoding)
    {
        String outputPath = "main" + File.separator + "webapp" + File.separator;
        this.addCodeGenerator(new CodeGenerator(templateName, outputPath + File.separator + outputName, useCopy, encoding));
    }

    @Override
    public void generate() throws Exception
    {
        super.generate();
        FileUtils.mkdir(this.getOutputDir() + File.separator + this.getCodePath() + File.separator + "test" + File.separator + "java");
    }

    @Override
    public String getCodePath()
    {
        return CODE_PATH;
    }
}
