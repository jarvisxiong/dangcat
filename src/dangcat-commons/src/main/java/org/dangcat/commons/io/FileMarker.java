package org.dangcat.commons.io;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class FileMarker
{
    protected static final Logger logger = Logger.getLogger(FileMarker.class);
    private Class<?> classType = null;
    private Configuration configuration = null;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private String encoding = FileUtils.ENCODING_UTF_8;
    private Locale locale = Environment.getCurrentLocale();
    private String prefix = null;
    private File templateDir = null;
    private String templateName = null;

    public FileMarker(Class<?> classType)
    {
        this(classType, "");
    }

    public FileMarker(Class<?> classType, String prefix)
    {
        this.classType = classType;
        this.prefix = prefix;
    }

    public FileMarker(File template)
    {
        if (template.isFile())
        {
            this.templateDir = template.getParentFile();
            this.templateName = template.getName();
        }
        else if (template.isDirectory())
            this.templateDir = template;
    }

    private Configuration getConfiguration() throws IOException
    {
        if (this.configuration == null)
        {
            Configuration configuration = new Configuration();
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            configuration.setEncoding(this.getLocale(), this.getEncoding());
            if (this.classType != null)
                configuration.setClassForTemplateLoading(classType, this.prefix);
            else if (this.templateDir != null)
            {
                if (!this.templateDir.exists() || !this.templateDir.isDirectory())
                {
                    String error = "The template directory " + this.templateDir.getAbsolutePath() + " is invalid.";
                    logger.error(error);
                    throw new IOException(error);
                }
                configuration.setDirectoryForTemplateLoading(this.templateDir);
            }
            this.configuration = configuration;
        }
        return this.configuration;
    }

    public Map<String, Object> getDataMap()
    {
        return dataMap;
    }

    public String getEncoding()
    {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void process(File outputFile) throws IOException
    {
        this.process(this.templateName, outputFile);
    }

    public void process(OutputStream outputStream) throws IOException
    {
        this.process(this.templateName, outputStream);
    }

    public void process(String name, File outputFile) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            this.process(name, outputStream);
        }
        finally
        {
            if (outputStream != null)
                outputStream.close();
        }
    }

    public void process(String name, OutputStream outputStream) throws IOException
    {
        try
        {
            Configuration configuration = this.getConfiguration();
            if (configuration != null && !ValueUtils.isEmpty(name))
            {
                Template template = configuration.getTemplate(name);
                if (template != null)
                {
                    template.setEncoding(this.getEncoding());
                    Writer writer = new OutputStreamWriter(outputStream, this.getEncoding());
                    template.process(this.dataMap, writer);
                    writer.flush();
                }
            }
        }
        catch (TemplateException e)
        {
            logger.error(name, e);
            throw new IOException(e.getMessage());
        }
    }

    public void putData(String name, Object value)
    {
        this.dataMap.put(name, value);
    }
}
