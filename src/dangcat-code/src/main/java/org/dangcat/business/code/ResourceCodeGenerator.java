package org.dangcat.business.code;

import java.io.File;

public abstract class ResourceCodeGenerator extends CodeGenerator
{
    private static final String[] Locals = { "zh_CN", "en_US" };
    private String outputFile = null;

    @Override
    public void generate() throws Exception
    {
        for (String local : Locals)
        {
            this.outputFile = this.getOutputFile(local);
            super.generate();
        }
    }

    @Override
    public String getEncoding()
    {
        return null;
    }

    @Override
    protected String getOutputFile()
    {
        String outputFile = "main" + File.separator + "resources";
        outputFile += File.separator + this.getPackageName().replace(".", File.separator);
        outputFile += File.separator + this.outputFile;
        return outputFile;
    }

    protected abstract String getOutputFile(String local);
}
