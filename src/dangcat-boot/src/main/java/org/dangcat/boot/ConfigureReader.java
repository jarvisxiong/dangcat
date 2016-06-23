package org.dangcat.boot;

import java.io.File;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.service.impl.PropertiesManager;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

public class ConfigureReader
{
    public static final String KEY_SYSTEM_ID = "system.id";
    private Document document = null;
    private Integer id = null;
    private String logConfigFileName = null;
    private String moduleMenus = null;
    private String permissionConfigFileName = null;
    private String resourceConfigFileName = null;
    private String version = null;

    public String getAttributValue(String nodePath, String name)
    {
        String value = null;
        Node node = this.getXmlNode(nodePath);
        if (node != null)
        {
            Element element = (Element) node;
            value = element.attributeValue(name);
        }
        return PropertiesManager.getInstance().getValue(value);
    }

    /**
     * ≈‰÷√Œƒº˛°£
     */
    protected File getConfigFile()
    {
        String configFileName = ApplicationContext.getInstance().getName() + ".server.xml";
        return new File(ApplicationContext.getInstance().getContextPath().getConf() + File.separator + configFileName);
    }

    public Integer getId()
    {
        if (this.id == null)
            return ValueUtils.parseInt(System.getProperty(KEY_SYSTEM_ID));
        return this.id;
    }

    protected String getLogConfigFileName()
    {
        return this.logConfigFileName;
    }

    protected String getModuleMenus()
    {
        return this.moduleMenus;
    }

    public String getPermissionConfigFileName()
    {
        return this.permissionConfigFileName;
    }

    protected String getResourceConfigFileName()
    {
        return this.resourceConfigFileName;
    }

    public String getVersion()
    {
        return this.version;
    }

    private Node getXmlNode(String nodePath)
    {
        Node node = null;
        if (this.document != null)
        {
            String xpath = "//Configuration/";
            if (!ValueUtils.isEmpty(nodePath))
                xpath += nodePath;
            else
                xpath += ".";
            node = this.document.selectSingleNode(xpath);
        }
        return node;
    }

    /**
     * ∂¡»°≈‰÷√ƒ⁄»›°£
     * @param nodePath ≈‰÷√¬∑æ∂°£
     * @return ≈‰÷√µƒ÷µ°£
     */
    public String getXmlValue(String nodePath)
    {
        String value = null;
        Node node = this.getXmlNode(nodePath);
        if (node != null)
            value = node.getText().trim();
        return PropertiesManager.getInstance().getValue(value);
    }

    protected boolean isValid()
    {
        File configFile = this.getConfigFile();
        return configFile != null && configFile.exists();
    }

    /**
     * ‘ÿ»Î≈‰÷√ƒ⁄»›°£
     * @throws DocumentException
     */
    protected void load() throws DocumentException
    {
        File configFile = this.getConfigFile();
        if (!this.isValid())
        {
            System.err.println("The server config file is not exists: " + configFile.getAbsolutePath());
            System.exit(0);
            return;
        }

        String name = ApplicationContext.getInstance().getName();
        Logger logger = ApplicationContext.getInstance().logger;
        String info = "The server config file " + configFile.getAbsolutePath() + " is load.";
        if (logger != null)
            logger.info(info);
        else
            System.out.println(info);
        this.document = new SAXReader().read(configFile);
        this.id = ValueUtils.parseInt(this.getAttributValue(null, "Id"));
        this.version = this.getXmlValue("Version");
        this.logConfigFileName = this.getXmlValue("LogConfig");
        this.resourceConfigFileName = this.getXmlValue("Resource");
        this.permissionConfigFileName = this.getXmlValue("Permissions");
        if (ValueUtils.isEmpty(this.permissionConfigFileName))
            this.permissionConfigFileName = name + ".permissions.xml";
        this.moduleMenus = this.getXmlValue("ModuleMenus");
        if (ValueUtils.isEmpty(this.moduleMenus))
            this.moduleMenus = name + ".menus.xml";
    }

    protected void log()
    {
        String name = ApplicationContext.getInstance().getName();
        Logger logger = ApplicationContext.getInstance().logger;
        logger.info("The " + name + " (Id: " + this.getId() + " Version: " + this.getVersion() + ") is started.");
    }
}
