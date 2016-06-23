package org.dangcat.install.task;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.*;
import java.util.Map.Entry;

public class ConfigFileAccess {
    protected final Logger logger = Logger.getLogger(this.getClass());
    private File configFile = null;
    private Collection<ConfigureAccess> configureAccesses = new LinkedList<ConfigureAccess>();
    private boolean enabled = true;

    public void addConfigureAccess(ConfigureAccess configureAccess) {
        if (configureAccess != null)
            this.configureAccesses.add(configureAccess);
    }

    public File getConfigFile() {
        return this.configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void load() {
        File configFile = this.getConfigFile();
        if (configFile == null || !configFile.exists()) {
            if (configFile != null)
                System.err.println("The config file " + configFile.getAbsolutePath() + " is not exists!");
            return;
        }

        FileInputStream inputStream = null;
        try {
            Properties properties = new Properties();
            inputStream = new FileInputStream(configFile);
            properties.load(inputStream);
            for (ConfigureAccess configureAccess : this.configureAccesses) {
                if (configureAccess.isEnabled())
                    configureAccess.load(properties);
            }
        } catch (Exception e) {
            this.logger.error("Load the resource file error : " + configFile.getAbsolutePath(), e);
        } finally {
            FileUtils.close(inputStream);
        }
    }

    public void save() {
        File configFile = this.getConfigFile();
        this.logger.info("Save config data: " + configFile.getAbsolutePath());
        PrintWriter writer = null;
        try {
            Properties properties = new Properties();
            for (ConfigureAccess configureAccess : this.configureAccesses) {
                if (configureAccess.isEnabled())
                    configureAccess.save(properties);
            }
            Map<String, String> propertyMap = new TreeMap<String, String>();
            for (Object key : properties.keySet())
                propertyMap.put((String) key, properties.getProperty((String) key));
            writer = new PrintWriter(this.getConfigFile());
            for (Entry<String, String> entry : propertyMap.entrySet()) {
                writer.print(entry.getKey());
                writer.print("=");
                writer.println(entry.getValue());
            }
            this.load();
        } catch (Exception e) {
            this.logger.error("Save the resource file error : " + configFile.getAbsolutePath(), e);
        } finally {
            FileUtils.close(writer);
        }
    }
}
