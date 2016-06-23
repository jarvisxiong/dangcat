package org.dangcat.boot.security.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.boot.security.exceptions.SecurityLoginException;
import org.dangcat.commons.io.FileActionMonitor;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.FileWatcherAdaptor;
import org.dangcat.commons.resource.Resources;
import org.dangcat.commons.utils.ValueUtils;

/**
 * 本地安全服务。
 * @author dangcat
 * 
 */
@Resources( { SecurityLoginException.class })
public class LocalSecurityServiceImpl extends LoginServiceBase
{
    private static final String SECURITY_NAME = "LocalSecurity";
    private String configName = null;
    private Map<String, LoginUser> localeUserMap = null;
    private LocalSignResolveProvider signResolveProvider = null;

    public LocalSecurityServiceImpl(String configName)
    {
        super(SECURITY_NAME);
        this.configName = configName;
    }

    private File getConfigFile()
    {
        return new File(ApplicationContext.getInstance().getContextPath().getConf() + File.separator + this.configName);
    }

    @Override
    protected SignResolveProvider getSignResolveProvider()
    {
        return this.signResolveProvider;
    }

    @Override
    public void initialize()
    {
        File configFile = this.getConfigFile();
        if (!configFile.exists())
        {
            this.logger.warn("The local security file is not exists : " + configFile.getAbsolutePath());
            return;
        }

        FileActionMonitor.getInstance().addFileWatcherAdaptor(new FileWatcherAdaptor(configFile)
        {
            @Override
            protected void onFileChange(File file)
            {
                LocalSecurityServiceImpl.this.loadConfig();
            }
        });
        this.loadConfig();
    }

    @Override
    public LoginUser load(String no) throws SecurityLoginException
    {
        LoginUser loginUser = null;
        if (this.localeUserMap != null)
            loginUser = this.localeUserMap.get(no);
        return loginUser;
    }

    private void loadConfig()
    {
        File configFile = this.getConfigFile();
        InputStream inputStream = null;
        try
        {
            Map<String, LoginUser> localeUserMap = new HashMap<String, LoginUser>();
            inputStream = new FileInputStream(configFile);
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Object user : properties.keySet())
            {
                String no = (String) user;
                String value = (String) properties.get(user);
                if (!ValueUtils.isEmpty(value))
                {
                    String[] values = value.split(";");
                    if (values.length == 2)
                    {
                        String role = values[0];
                        String password = values[1];
                        LoginUser localUser = new LoginUser(no, role, password, SECURITY_NAME);
                        Collection<Integer> permissions = PermissionManager.getInstance().getPermissions(role);
                        if (permissions != null)
                            localUser.getPermissions().addAll(permissions);
                        if (localUser.isValid())
                            localeUserMap.put(no, localUser);
                    }
                }
            }
            if (localeUserMap.size() > 0)
            {
                this.localeUserMap = localeUserMap;
                this.signResolveProvider = new LocalSignResolveProvider(this.localeUserMap);
            }
            this.logger.info("load the local security config : " + configFile.getAbsolutePath());
        }
        catch (Exception e)
        {
            this.logger.error(configFile, e);
        }
        finally
        {
            inputStream = FileUtils.close(inputStream);
        }
    }
}
