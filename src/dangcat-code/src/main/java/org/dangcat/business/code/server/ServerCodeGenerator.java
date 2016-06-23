package org.dangcat.business.code.server;

import org.dangcat.business.code.DirectoryCodeGenerator;
import org.dangcat.business.code.server.conf.ConfCodeGenerator;
import org.dangcat.business.code.server.home.HomeBinCodeGenerator;
import org.dangcat.business.code.server.home.HomeConfCodeGenerator;
import org.dangcat.business.code.server.home.HomeInstallCodeGenerator;
import org.dangcat.business.code.server.pdm.PdmCodeGenerator;
import org.dangcat.business.code.server.src.SrcCodeGenerator;

/**
 * 服务代码生成器。
 *
 * @author dangcat
 */
public class ServerCodeGenerator extends DirectoryCodeGenerator {
    private static final String SERVER_NAME = "serverName";

    public ServerCodeGenerator(String packageName, String serverName) {
        this.setPackageName(packageName);
        this.setServerName(serverName);

        this.addCodeGenerator(new ConfCodeGenerator(this.getServerName()));
        this.addCodeGenerator(new HomeConfCodeGenerator(this.getServerName()));
        this.addCodeGenerator(new HomeBinCodeGenerator(this.getServerName()));
        this.addCodeGenerator(new HomeInstallCodeGenerator());
        this.addCodeGenerator(new PdmCodeGenerator(this.getServerName()));
        this.addCodeGenerator(new ProjectCodeGenerator());
        this.addCodeGenerator(new SrcCodeGenerator(this.getPackageName(), this.getServerName()));
    }

    public String getServerName() {
        return (String) this.getParams().get(SERVER_NAME);
    }

    public void setServerName(String serverName) {
        this.getParams().put(SERVER_NAME, serverName);
    }
}
