package org.dangcat.install;

import org.apache.log4j.Logger;
import org.dangcat.commons.os.service.SystemServiceUtils;
import org.dangcat.commons.resource.ResourceUtils;
import org.dangcat.commons.utils.CommandExecutor;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public abstract class Installer
{
    private static final String INSTALL = "install";
    private static final String RUNAS = "runAs";
    private static final String SERVICE_DISPLAYNAME = "serviceDisplayName";
    private static final String SERVICE_NAME = "serviceName";
    private static final String START = "start";
    private static final String STOP = "stop";
    private static final String UNINSTALL = "uninstall";
    protected Logger logger = Logger.getLogger(this.getClass());
    private boolean cancel = false;
    private OutputStream outputStream = null;
    private Map<String, Object> params = new HashMap<String, Object>();

    public Installer()
    {
        this.setRunAs(System.getProperty("user.name"));
    }

    public void cancel()
    {
        this.cancel = true;
    }

    protected void execute(File exeFile, String... params)
    {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.setLogger(this.getLogger());
        commandExecutor.setOutputStream(this.getOutputStream());
        commandExecutor.setExeFile(exeFile);
        commandExecutor.exec(params);
    }

    public boolean exists()
    {
        return SystemServiceUtils.exists(this.getServiceDisplayName());
    }

    public Logger getLogger()
    {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public OutputStream getOutputStream()
    {
        return this.outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public Map<String, Object> getParams()
    {
        return this.params;
    }

    public String getRunAs()
    {
        return (String) this.params.get(RUNAS);
    }

    public void setRunAs(String runAs) {
        this.params.put(RUNAS, runAs);
    }

    public String getServiceDisplayName()
    {
        String serviceDisplayName = (String) this.params.get(SERVICE_DISPLAYNAME);
        return ValueUtils.isEmpty(serviceDisplayName) ? this.getServiceName() : serviceDisplayName;
    }

    public void setServiceDisplayName(String serviceName) {
        this.params.put(SERVICE_DISPLAYNAME, serviceName);
    }

    protected abstract File getServiceFile();

    public String getServiceName()
    {
        return (String) this.params.get(SERVICE_NAME);
    }

    public void setServiceName(String serviceName) {
        this.params.put(SERVICE_NAME, serviceName);
    }

    protected String getText(String key, Object... params)
    {
        return ResourceUtils.getText(this.getClass(), key, params);
    }

    public void install()
    {
        if (this.exists())
            this.logService("service.exists");
        else
        {
            this.logService("service.install");
            this.execute(this.getServiceFile(), INSTALL);
        }
    }

    public boolean isCancel()
    {
        return this.cancel;
    }

    public boolean isRunning()
    {
        return SystemServiceUtils.isRunning(this.getServiceDisplayName());
    }

    protected void log(String key, String name, Object... params)
    {
        Collection<Object> paramCollection = new LinkedList<Object>();
        paramCollection.add(name);
        if (params != null && params.length > 0)
        {
            for (Object param : params)
                paramCollection.add(param);
        }
        this.logger.info(this.getText(key, paramCollection.toArray()));
    }

    protected void logService(String key, Object... params)
    {
        this.log(key, this.getServiceName(), params);
    }

    public void prepare()
    {
    }

    public void start()
    {
        if (this.exists())
        {
            this.logService("service.start");
            this.execute(this.getServiceFile(), START);
        }
    }

    public void stop()
    {
        if (this.exists() && this.isRunning())
        {
            this.logService("service.stop");
            this.execute(this.getServiceFile(), STOP);
        }
    }

    public void uninstall()
    {
        if (this.exists())
        {
            this.logService("service.uninstall");
            this.execute(this.getServiceFile(), UNINSTALL);
        }
    }
}
