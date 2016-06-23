package org.dangcat.business.code.server.home;

import java.io.File;

import org.dangcat.business.code.server.conf.ConfCodeGenerator;

/**
 * 服务代码生成器。
 * @author dangcat
 * 
 */
public class HomeConfCodeGenerator extends ConfCodeGenerator
{
    private static final String CODE_PATH = "home" + File.separator + "conf";

    public HomeConfCodeGenerator(String serverName)
    {
        super(serverName);
    }

    @Override
    public String getCodePath()
    {
        return CODE_PATH;
    }
}
