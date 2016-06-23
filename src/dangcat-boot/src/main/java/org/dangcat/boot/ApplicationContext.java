package org.dangcat.boot;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.boot.menus.MenusManager;
import org.dangcat.boot.permission.PermissionManager;
import org.dangcat.boot.security.SecurityLoginService;
import org.dangcat.boot.server.impl.ServerManager;
import org.dangcat.commons.io.FileActionMonitor;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.io.FileWatcherAdaptor;
import org.dangcat.commons.io.ResourceLoader;
import org.dangcat.commons.reflect.ReflectUtils;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.resource.ResourceReaderImpl;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.conf.ConfigureManager;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceBase;
import org.dangcat.framework.service.ServiceHelper;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.ServiceStatus;
import org.dangcat.framework.service.impl.ExtensionClassLoader;
import org.dangcat.framework.service.impl.PropertiesManager;
import org.dangcat.framework.service.impl.ServiceControlBase;
import org.dangcat.framework.service.impl.ServiceFactory;
import org.dangcat.persistence.orm.SessionFactory;
import org.dom4j.DocumentException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 系统上下文。
 * @author dangcat
 * 
 */
public class ApplicationContext extends ServiceControlBase
{
    private static ApplicationContext instance = new ApplicationContext();
    protected Logger logger = null;
    private List<ChangeEventAdaptor> changeEventAdaptorList = new ArrayList<ChangeEventAdaptor>();
    private ConfigureReader configureReader = new ConfigureReader();
    private ContextPath contextPath = new ContextPath();
    private ServiceBase mainService;
    private String name = null;
    private ResourceReader resourceReader = null;
    public ApplicationContext()
    {
        super(null);
    }

    public static ApplicationContext getInstance() {
        return instance;
    }

    /**
     * 添加侦听对象。
     * @param changedListener
     */
    public void addChangeEventAdaptor(ChangeEventAdaptor changeEventAdaptor)
    {
        if (changeEventAdaptor != null && !this.changeEventAdaptorList.contains(changeEventAdaptor))
            this.changeEventAdaptorList.add(changeEventAdaptor);
    }

    /**
     * 增加配置文件监视。
     */
    private void addConfigFileWatcher()
    {
        if (!this.configureReader.isValid())
            return;

        FileActionMonitor.getInstance().addFileWatcherAdaptor(new FileWatcherAdaptor(this.configureReader.getConfigFile())
        {
            @Override
            protected void onFileChange(File file)
            {
                ApplicationContext.this.loadConfig();
            }
        });
    }

    public ConfigureReader getConfigureReader()
    {
        return this.configureReader;
    }

    public ContextPath getContextPath()
    {
        return this.contextPath;
    }

    public ServiceBase getMainService()
    {
        return this.mainService;
    }

    public String getName()
    {
        return this.name;
    }

    protected void setName(String name) {
        this.name = name;
    }

    public ResourceReader getResourceReader()
    {
        if (this.resourceReader == null && this.getMainService() != null)
            this.resourceReader = new ResourceReaderImpl(this.getMainService().getClass());
        return this.resourceReader;
    }

    /**
     * 初始化服务。
     */
    @Override
    public void initialize()
    {
        try
        {
            this.addConfigFileWatcher();
            this.loadConfig();
            this.loadProperties();
            this.loadLogger();
            this.loadExtension();
            this.log();
            this.loadResource();

            super.initialize();

            this.loadCoreServices();
            this.loadMainService();
        }
        catch (Exception e)
        {
            if (this.logger != null)
                this.logger.error("loadMainService exception:", e);
            else
                e.printStackTrace();
            System.exit(0);
        }
    }

    /**
     * 载入配置内容。
     */
    private void loadConfig()
    {
        try
        {
            this.configureReader.load();
            if (this.changeEventAdaptorList.size() > 0)
            {
                Event event = new Event();
                for (ChangeEventAdaptor changeEventAdaptor : this.changeEventAdaptorList)
                    changeEventAdaptor.afterChanged(this, event);
            }
        }
        catch (DocumentException e)
        {
            this.logger.error(this, e);
        }
    }

    /**
     * 载入核心服务。
     */
    private void loadCoreServices()
    {
        // 配置目录设为资源扩展路径
        File confPath = new File(this.getContextPath().getConf());
        ResourceLoader.addExtendDirectory(confPath);
        File dataPath = new File(this.getContextPath().getData());
        ResourceLoader.addExtendDirectory(dataPath);

        CoreServiceLoader.load(this);
    }

    /**
     * 载入扩展目录。
     */
    private void loadExtension()
    {
        this.getContextPath().initSystemProperties();

        PropertiesManager propertiesManager = PropertiesManager.getInstance();
        String classPathConfig = this.getConfigureReader().getXmlValue("ClassPath");
        propertiesManager.setSystemProperty(ContextPath.DANGCAT_CLASSPATH, classPathConfig);
        String classPath = propertiesManager.getPropertyValue(ContextPath.DANGCAT_CLASSPATH);
        this.getContextPath().setClassPath(classPath);

        ExtensionClassLoader extensionClassLoader = ExtensionClassLoader.getInstance();
        extensionClassLoader.addClassPath(classPath);
        ClassLoader classLoader = extensionClassLoader.load(ApplicationContext.class.getClassLoader());
        if (classLoader != null)
            Thread.currentThread().setContextClassLoader(classLoader);
    }

    /**
     * 初始化日志配置。
     */
    private void loadLogger()
    {
        String logConfigFileName = this.configureReader.getLogConfigFileName();
        if (ValueUtils.isEmpty(logConfigFileName))
            logConfigFileName = this.getName() + ".log4j.properties";
        File logConfigFile = new File(this.getContextPath().getConf() + File.separator + logConfigFileName);
        if (!logConfigFile.exists())
            System.err.println("The log config file " + logConfigFile.getAbsolutePath() + " is not exist.");
        else
        {
            PropertyConfigurator.configureAndWatch(logConfigFile.getAbsolutePath(), 1000);
            this.logger = Logger.getLogger(ApplicationContext.class);
            this.logger.info("The log config file " + logConfigFile.getAbsolutePath() + " is load.");
        }
    }

    /**
     * 载入模块主服务。
     */
    private void loadMainService()
    {
        // 主服务。
        String mainServiceClass = this.getConfigureReader().getXmlValue("MainService");
        ServiceFactory serviceFactory = ServiceFactory.getInstance();
        ServiceBase mainService = (ServiceBase) ReflectUtils.newInstance(mainServiceClass, new Class<?>[] { ServiceProvider.class }, new Object[] { serviceFactory });
        if (mainService == null)
        {
            this.logger.error("The main service is not found.");
            this.stop();
            System.exit(0);
            return;
        }

        this.addService(mainService.getClass(), mainService);
        mainService.initialize();
        this.mainService = mainService;

        serviceFactory.load(mainService.getClass());
        PermissionManager.getInstance().load();
        MenusManager.getInstance().load(mainService.getClass(), this.getConfigureReader().getModuleMenus());

        // 初始化安全服务。
        ServiceBase securityService = (ServiceBase) ServiceFactory.getInstance().getService(SecurityLoginService.class);
        if (securityService != null)
            securityService.initialize();
    }

    /**
     * 载入属性配置。
     */
    private void loadProperties()
    {
        PropertiesManager propertiesManager = PropertiesManager.getInstance();
        // 解析系统属性。
        String propertiesConfig = this.getConfigureReader().getXmlValue("Properties");
        propertiesManager.loadSystemProperties(propertiesConfig);
        // 外接属性配置。
        String propertiesFileConfig = this.getConfigureReader().getXmlValue("PropertiesFile");
        if (ValueUtils.isEmpty(propertiesFileConfig))
            propertiesFileConfig = this.getName() + ".server.properties";
        propertiesManager.load(new File(this.configureReader.getConfigFile().getParent(), propertiesFileConfig));
    }

    /**
     * 初始化数据库。
     */
    private void loadResource()
    {
        final String defaultResourceFileName = "resource.properties";
        String resourceConfigFileName = this.configureReader.getResourceConfigFileName();
        if (ValueUtils.isEmpty(resourceConfigFileName))
            resourceConfigFileName = this.getName() + "." + defaultResourceFileName;
        File resourceConfigFile = new File(this.getContextPath().getConf() + File.separator + resourceConfigFileName);
        if (Environment.isTestEnabled())
        {
            File testResourceConfigFile = new File(this.getContextPath().getBaseDir() + File.separator + "target/test-classes/META-INF" + File.separator + defaultResourceFileName);
            if (testResourceConfigFile.exists())
                resourceConfigFile = testResourceConfigFile;
        }
        if (!resourceConfigFile.exists())
            this.logger.warn("The resource file is not exists : " + resourceConfigFile.getAbsolutePath());
        else
        {
            // 加载资源配置
            if (!ConfigureManager.getInstance().configure(resourceConfigFile))
                System.exit(0);
        }
    }

    /**
     * 输出启动日志。
     */
    private void log()
    {
        FileUtils.mkdir(System.getProperty("java.io.tmpdir"));
        this.configureReader.log();
        this.logger.info(ServerManager.getInstance());
        this.logger.info(this.getContextPath());
        this.logger.info(PropertiesManager.getInstance().toText());
    }

    /**
     * 删除侦听对象。
     * @param changedListener 侦听对象。
     */
    public void removeChangeEventAdaptor(ChangeEventAdaptor changeEventAdaptor)
    {
        if (changeEventAdaptor != null && this.changeEventAdaptorList.contains(changeEventAdaptor))
            this.changeEventAdaptorList.remove(changeEventAdaptor);
    }

    /**
     * 启动服务。
     */
    @Override
    public void start()
    {
        if (this.getServiceStatus().equals(ServiceStatus.Stopped))
        {
            this.setServiceStatus(ServiceStatus.Starting);
            // 服务注入。
            this.inject();
            // 启动服务。
            ServiceHelper.start(this);
            super.start();
        }
    }

    /**
     * 停止服务。
     */
    @Override
    public void stop()
    {
        if (this.getServiceStatus().equals(ServiceStatus.Started))
        {
            this.setServiceStatus(ServiceStatus.Stopping);
            FileActionMonitor.getInstance().stop();
            ServiceHelper.stop(this);
            SessionFactory.getInstance().close();
            PermissionManager.stop();
            Environment.clearHomePath();
            super.stop();
        }
    }
}
