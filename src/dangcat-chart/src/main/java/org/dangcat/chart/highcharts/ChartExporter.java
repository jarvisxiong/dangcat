package org.dangcat.chart.highcharts;

import org.apache.log4j.Logger;
import org.dangcat.commons.io.FileMarker;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.CommandExecutor;
import org.dangcat.commons.utils.OSType;
import org.dangcat.commons.utils.ValueUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ChartExporter
{
    private static final int DEFAULT_HEIGHT = 600;
    private static final int DEFAULT_WIDTH = 800;
    private static final String HIGHCHARTS_PATH = "/js/highcharts-3.0.7";
    private static final Logger logger = Logger.getLogger(ChartExporter.class);
    private static final String PHANTOMJS_HOME = "PHANTOMJS_HOME";
    private File baseDir = null;
    private Integer height = null;
    private OutputStream outputStream = null;
    private boolean removeTemp = true;
    private String tempDir = null;
    private Integer width = null;

    public ChartExporter(File baseDir)
    {
        this.baseDir = baseDir;
    }

    public boolean export(String options, String data, File output) throws IOException
    {
        String basePath = FileUtils.getCanonicalPath(this.baseDir.getAbsolutePath());
        File convertFile = new File(basePath + HIGHCHARTS_PATH + "/highcharts-convert.js");
        File templateFile = new File(basePath + HIGHCHARTS_PATH + "/highcharts-convert.template");
        FileMarker fileMarker = new FileMarker(templateFile);
        fileMarker.putData("basedir", "file:///" + basePath);
        fileMarker.putData("width", this.getWidth());
        fileMarker.putData("height", this.getHeight());
        fileMarker.putData("options", options);
        fileMarker.putData("data", data);
        File temp = File.createTempFile("hightchats", ".html", this.getTempDir());
        fileMarker.process(temp);

        FileUtils.mkdir(output.getParent());
        CommandExecutor commandExecutor = new CommandExecutor();
        if (logger.isDebugEnabled())
            commandExecutor.setLogger(logger);
        commandExecutor.setOutputStream(this.getOutputStream());
        commandExecutor.setExeFile(this.getPhantomjs());
        commandExecutor.exec(convertFile.getAbsolutePath(), "file:///" + temp.getAbsolutePath(), output.getAbsolutePath(), this.getWidth().toString(), this.getHeight().toString());

        if (this.isRemoveTemp())
            FileUtils.delete(temp);
        return output.exists();
    }

    public Integer getHeight()
    {
        return this.height == null ? DEFAULT_HEIGHT : this.height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public OutputStream getOutputStream()
    {
        return outputStream;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    private File getPhantomjs()
    {
        File phantomjs = this.getPhantomjsFile(System.getProperty(PHANTOMJS_HOME));
        if (phantomjs == null || !phantomjs.exists())
            phantomjs = this.getPhantomjsFile(System.getenv(PHANTOMJS_HOME));
        if ((phantomjs == null || !phantomjs.exists()))
            phantomjs = this.getPhantomjsFile("./phantomjs");
        if (phantomjs == null || !phantomjs.exists())
            phantomjs = this.getPhantomjsFile(null);
        return phantomjs;
    }

    private File getPhantomjsFile(String phantomjsPath)
    {
        String phantomjs = "";
        if (!ValueUtils.isEmpty(phantomjsPath))
            phantomjs += phantomjsPath + File.separator;
        phantomjs += "phantomjs";
        if (OSType.Windows.equals(OSType.getOSType()))
            phantomjs += ".exe";
        return new File(phantomjs);
    }

    public File getTempDir()
    {
        File tempDir = null;
        if (this.tempDir == null)
            tempDir = new File(System.getProperty("java.io.tmpdir"));
        else
            tempDir = new File(this.tempDir);
        FileUtils.mkdir(tempDir.getAbsolutePath());
        return tempDir;
    }

    public void setTempDir(String tempDir)
    {
        this.tempDir = tempDir;
    }

    public Integer getWidth()
    {
        return this.width == null ? DEFAULT_WIDTH : this.width;
    }

    public void setWidth(Integer width)
    {
        this.width = width;
    }

    public boolean isRemoveTemp()
    {
        return removeTemp;
    }

    public void setRemoveTemp(boolean removeTemp)
    {
        this.removeTemp = removeTemp;
    }
}
