package org.dangcat.boot.service.impl;

import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.dangcat.boot.ApplicationContext;
import org.dangcat.boot.config.WebServiceConfig;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.commons.utils.NetUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.framework.service.ServiceProvider;
import org.dangcat.framework.service.impl.ServiceControlBase;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.xml.XmlConfiguration;

import java.io.File;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * Web 服务。
 *
 * @author dangcat
 */
public class WebService extends ServiceControlBase {
    private static final String SERVICE_NAME = "WebServiceThread";
    private Server server = null;

    /**
     * 构建服务。
     *
     * @param parent 所属父服务。
     */
    public WebService(ServiceProvider parent) {
        super(parent);
    }

    private void createSelectChannelConnector(Server server) throws Exception {
        Integer port = WebServiceConfig.getInstance().getPort();
        if (port != null) {
            if (!NetUtils.isPortValid(port))
                throw new SocketException("The port " + port + " is invalid.");

            ServerConnector serverConnector = new ServerConnector(server);
            serverConnector.setPort(WebServiceConfig.getInstance().getPort());
            server.addConnector(serverConnector);
        }
    }

    private Server createServerFromConfig() throws Exception {
        Server server = new Server(this.createThreadPool());

        this.createSelectChannelConnector(server);
        this.createSslConnector(server);

        HandlerList handlers = new HandlerList();
        if (!this.createWebAppHandler(handlers))
            this.createWebAppsHandlers(handlers);
        if (handlers.getChildHandlers().length > 0) {
            GzipHandler gzipHandler = new GzipHandler();
            gzipHandler.setHandler(handlers);
            server.setHandler(gzipHandler);
        }

        return server;
    }

    private Server createServerFromXml() throws Exception {
        Server server = null;
        File configFile = this.getConfigFile();
        if (configFile != null && configFile.exists()) {
            Resource resource = Resource.newResource(configFile);
            XmlConfiguration configuration = new XmlConfiguration(resource.getInputStream());
            server = (Server) configuration.configure();
        }
        return server;
    }

    private void createSslConnector(Server server) throws Exception {
        WebServiceConfig webServiceConfig = WebServiceConfig.getInstance();
        Integer sslPort = webServiceConfig.getSslPort();
        if (sslPort != null && !ValueUtils.isEmpty(webServiceConfig.getKeyStore())) {
            if (!NetUtils.isPortValid(sslPort))
                throw new SocketException("The sslPort " + sslPort + " is invalid.");


            SslContextFactory sslContextFactory = new SslContextFactory();
            sslContextFactory.setKeyStorePath(ApplicationContext.getInstance().getContextPath().getBaseDir() + File.separator + webServiceConfig.getKeyStore());
            sslContextFactory.setKeyStorePassword(webServiceConfig.getKeyStorePassword());
            sslContextFactory.setKeyManagerPassword(webServiceConfig.getKeyManagerPassword());

            HttpConfiguration http_config = new HttpConfiguration();
            http_config.setSecurePort(webServiceConfig.getSslPort());
            HttpConfiguration https_config = new HttpConfiguration(http_config);
            SecureRequestCustomizer src = new SecureRequestCustomizer();
            src.setStsMaxAge(2000);
            src.setStsIncludeSubDomains(true);
            https_config.addCustomizer(src);

            ServerConnector serverConnector = new ServerConnector(server,
                    new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                    new HttpConnectionFactory(https_config));
            serverConnector.setPort(webServiceConfig.getSslPort());
            server.addConnector(serverConnector);
        }
    }

    private ThreadPool createThreadPool() throws Exception {
        WebServiceConfig webServiceConfig = WebServiceConfig.getInstance();
        QueuedThreadPool queuedThreadPool = new QueuedThreadPool();
        queuedThreadPool.setName(SERVICE_NAME);
        queuedThreadPool.setMinThreads(webServiceConfig.getThreadPoolSize());
        queuedThreadPool.setMaxThreads(webServiceConfig.getThreadPoolSize());
        return queuedThreadPool;
    }

    private boolean createWebAppHandler(HandlerList handlers) {
        WebServiceConfig webServiceConfig = WebServiceConfig.getInstance();
        String webApp = webServiceConfig.getWebApp();
        File webAppPath = new File(webApp);
        if (!webAppPath.exists())
            webAppPath = new File(ApplicationContext.getInstance().getContextPath().getHome() + File.separator + webApp);
        return this.createWebContext(handlers, webServiceConfig.getContextRoot(), webAppPath);
    }

    private void createWebAppsHandlers(HandlerList handlers) {
        File webAppsPath = new File(ApplicationContext.getInstance().getContextPath().getWebApps());
        this.createWebAppsHandlers(handlers, webAppsPath);
    }

    private void createWebAppsHandlers(HandlerList handlers, File directory) {
        if (directory.exists() && directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory())
                    this.createWebContext(handlers, file.getName(), file);
                else if (file.isFile() && file.getName().toLowerCase().endsWith(".war")) {
                    WebAppContext webAppContext = new WebAppContext();
                    webAppContext.setContextPath("/" + file.getName().replaceAll(".war", ""));
                    webAppContext.setWar(file.getAbsolutePath());
                    handlers.addHandler(webAppContext);
                    this.logger.info("Create WebAppContext from " + file.getAbsolutePath());
                }
            }
        }
    }

    private boolean createWebContext(HandlerList handlers, String contextPath, File webAppPath) {
        Collection<File> resourceDirs = this.findResourceDirs(webAppPath);
        File webXmlFile = this.findWebXmlFile(resourceDirs);
        if (webXmlFile != null && webXmlFile.exists()) {
            WebAppContext webAppContext = new WebAppContext();
            webAppContext.setContextPath("/" + contextPath);
            webAppContext.setDescriptor(webXmlFile.getAbsolutePath());
            webAppContext.setDefaultsDescriptor(webXmlFile.getParent() + File.separator + "webdefault.xml");
            webAppContext.setBaseResource(this.getResourceCollection(resourceDirs));
            webAppContext.setAttribute("org.eclipse.jetty.containerInitializers", this.jspInitializers());
            webAppContext.setAttribute(InstanceManager.class.getName(),
                    new SimpleInstanceManager());
            webAppContext.addBean(new ServletContainerInitializersStarter(webAppContext),
                    true);

            handlers.addHandler(webAppContext);

            this.logger.info("Create WebAppContext from " + webXmlFile.getAbsolutePath());
        }
        return webXmlFile != null && webXmlFile.exists();
    }

    private List<ContainerInitializer> jspInitializers() {
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(new ContainerInitializer(new JettyJasperInitializer(), null));
        return initializers;
    }

    private Collection<File> findResourceDirs(File webAppPath) {
        Collection<File> resourceDirs = new LinkedHashSet<File>();
        resourceDirs.add(webAppPath);
        String resources = WebServiceConfig.getInstance().getResources();
        if (!ValueUtils.isEmpty(resources)) {
            String[] paths = resources.split(";");
            if (paths != null) {
                for (String path : paths) {
                    File resourcePath = new File(ApplicationContext.getInstance().getContextPath().getHome() + File.separator + path);
                    if (resourcePath.exists() && resourcePath.isDirectory())
                        resourceDirs.add(resourcePath);
                }
            }
        }
        return resourceDirs;
    }

    private File findWebXmlFile(Collection<File> resourceDirs) {
        File webXmlFile = null;
        for (File file : resourceDirs) {
            webXmlFile = new File(file.getAbsolutePath() + File.separator + "/WEB-INF/web.xml");
            if (webXmlFile.exists())
                break;
        }
        return webXmlFile;
    }

    private File getConfigFile() {
        File configFile = null;
        String configFileName = WebServiceConfig.getInstance().getConfigFile();
        if (!ValueUtils.isEmpty(configFileName))
            configFile = new File(ApplicationContext.getInstance().getContextPath().getConf() + File.separator + configFileName);
        return configFile;
    }

    private ResourceCollection getResourceCollection(Collection<File> resourceDirs) {
        List<String> resourceList = new ArrayList<String>();
        for (File file : resourceDirs) {
            if (file.exists() && file.isDirectory())
                resourceList.add(file.getAbsolutePath());
        }
        return new ResourceCollection(resourceList.toArray(new String[0]));
    }

    @Override
    public void initialize() {
        super.initialize();

        WebServiceConfig.getInstance().addChangeEventAdaptor(new ChangeEventAdaptor() {
            @Override
            public void afterChanged(Object sender, Event event) {
                WebService.this.restart();
            }
        });
    }

    @Override
    public void start() {
        super.start();

        synchronized (Server.class) {
            if (this.server == null && WebServiceConfig.getInstance().isEnabled()) {
                try {
                    System.setProperty("jetty.home", ApplicationContext.getInstance().getContextPath().getBaseDir());

                    Server server = this.createServerFromXml();
                    if (server == null)
                        server = this.createServerFromConfig();
                    if (server != null) {
                        server.start();
                        this.server = server;
                    }
                } catch (Exception e) {
                    this.logger.error("The web service start error.", e);
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();

        synchronized (Server.class) {
            if (this.server != null) {
                try {
                    this.server.stop();
                    this.server.join();
                    this.server = null;
                } catch (Exception e) {
                    this.logger.error(this, e);
                }
            }
        }
    }
}