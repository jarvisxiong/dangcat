package org.dangcat.business.code;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileMarker;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.resource.ResourceManager;
import org.dangcat.commons.resource.ResourceReader;
import org.dangcat.commons.utils.ValueUtils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {
    private static final String DEFAULT_ENCODING = "utf-8";
    private static final String PACKAGE_NAME = "packageName";
    protected Logger logger = Logger.getLogger(this.getClass());
    private Class<?> classType = null;
    private String codePath = null;
    private String encoding = DEFAULT_ENCODING;
    private String outputDir = null;
    private String outputFile = null;
    private Map<String, Object> params = new HashMap<String, Object>();
    private Component parentComponent = null;
    private ResourceReader resourceReader = ResourceManager.getInstance().getResourceReader(this.getClass());
    private String template = null;
    private boolean useCopy = false;

    public CodeGenerator() {
    }

    public CodeGenerator(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    public CodeGenerator(String template, String outputFile) {
        this(template, outputFile, false);
    }

    public CodeGenerator(String template, String outputFile, boolean useCopy) {
        this(template, outputFile, useCopy, DEFAULT_ENCODING);
    }

    public CodeGenerator(String template, String outputFile, boolean useCopy, String encoding) {
        this.template = template;
        this.outputFile = outputFile;
        this.useCopy = useCopy;
        this.encoding = encoding;
    }

    public CodeGenerator(String template, String outputFile, String encoding) {
        this(template, outputFile, false, encoding);
    }

    public void generate() throws Exception {
        String outputPath = this.getOutputDir();
        if (!ValueUtils.isEmpty(outputPath))
            outputPath += File.separator;
        if (!ValueUtils.isEmpty(this.getCodePath()))
            outputPath += this.getCodePath() + File.separator;
        outputPath += this.getOutputFile();
        File outputFile = new File(outputPath);
        if (outputFile.exists()) {
            String title = this.resourceReader.getText("prompt.title");
            String message = this.resourceReader.getText("prompt.file.exists", outputFile.getAbsolutePath());
            if (JOptionPane.showConfirmDialog(this.parentComponent, message, title, JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
                return;
        }

        FileUtils.mkdir(outputFile.getParent());

        Class<?> classType = this.classType == null ? this.getClass() : this.classType;
        if (this.isUseCopy()) {
            InputStream inputStream = classType.getResourceAsStream(this.getTemplate());
            if (inputStream != null)
                FileUtils.copy(inputStream, outputFile);
        } else {
            FileMarker fileMarker = new FileMarker(classType);
            fileMarker.getDataMap().putAll(this.params);
            if (this.getEncoding() != null)
                fileMarker.setEncoding(this.getEncoding());
            fileMarker.process(this.getTemplate(), outputFile);
        }

        this.logger.info(this.resourceReader.getText("Generate") + ": " + this.getOutputFile() + " OK!");
    }

    public Class<?> getClassType() {
        return this.classType;
    }

    public void setClassType(Class<?> classType) {
        this.classType = classType;
    }

    public String getCodePath() {
        return this.codePath;
    }

    public void setCodePath(String codePath) {
        this.codePath = codePath;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public String getOutputDir() {
        return this.outputDir;
    }

    public void setOutputDir(String outputDir) {
        this.outputDir = outputDir;
    }

    protected String getOutputFile() {
        return this.outputFile;
    }

    public String getPackageName() {
        return (String) this.params.get(PACKAGE_NAME);
    }

    public void setPackageName(String packageName) {
        this.params.put(PACKAGE_NAME, packageName);
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Component getParentComponent() {
        return this.parentComponent;
    }

    public void setParentComponent(Component parentComponent) {
        this.parentComponent = parentComponent;
    }

    protected String getTemplate() {
        return this.template;
    }

    public boolean isUseCopy() {
        return this.useCopy;
    }

    public void setUseCopy(boolean useCopy) {
        this.useCopy = useCopy;
    }

    public void put(String name, Object value) {
        this.params.put(name, value);
    }
}
