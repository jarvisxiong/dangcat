package org.dangcat.business.code;

import java.util.Collection;
import java.util.LinkedHashSet;

public class DirectoryCodeGenerator extends CodeGenerator
{
    private Collection<CodeGenerator> codeGenerators = new LinkedHashSet<CodeGenerator>();

    protected void addCodeGenerator(CodeGenerator codeGenerator)
    {
        if (codeGenerator != null)
            this.codeGenerators.add(codeGenerator);
    }

    @Override
    public void generate() throws Exception
    {
        for (CodeGenerator codeGenerator : this.codeGenerators)
        {
            codeGenerator.setClassType(this.getClass());
            codeGenerator.setLogger(this.getLogger());
            codeGenerator.setOutputDir(this.getOutputDir());
            codeGenerator.setCodePath(this.getCodePath());
            codeGenerator.getParams().putAll(this.getParams());
            codeGenerator.generate();
        }
    }
}
