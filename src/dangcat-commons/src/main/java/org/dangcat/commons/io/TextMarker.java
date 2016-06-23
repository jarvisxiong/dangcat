package org.dangcat.commons.io;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.Environment;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TextMarker
{
    protected static final Logger logger = Logger.getLogger(TextMarker.class);
    private Configuration configuration = null;
    private Map<String, Object> dataMap = new HashMap<String, Object>();
    private String encoding = System.getProperty("file.encoding");
    private Locale locale = Environment.getCurrentLocale();
    private String template = null;

    public Configuration getConfiguration()
    {
        if (this.configuration == null)
        {
            Configuration configuration = new Configuration();
            configuration.setObjectWrapper(new DefaultObjectWrapper());
            configuration.setEncoding(this.getLocale(), this.getEncoding());
            this.configuration = configuration;
        }
        return this.configuration;
    }

    public Map<String, Object> getDataMap()
    {
        return this.dataMap;
    }

    public String getEncoding()
    {
        return this.encoding;
    }

    public Locale getLocale()
    {
        return this.locale;
    }

    public String getTemplate()
    {
        return this.template;
    }

    public String process() throws IOException
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        this.process(outputStream);
        return new String(outputStream.toByteArray());
    }

    public void process(File outputFile) throws IOException
    {
        OutputStream outputStream = null;
        try
        {
            outputStream = new BufferedOutputStream(new FileOutputStream(outputFile));
            this.process(outputStream);
        }
        finally
        {
            if (outputStream != null)
                outputStream.close();
        }
    }

    public void process(OutputStream outputStream) throws IOException
    {
        try
        {
            Template template = new Template(null, new StringReader(this.template), this.getConfiguration());
            template.setEncoding(this.getEncoding());
            Writer writer = new OutputStreamWriter(outputStream, this.getEncoding());
            template.process(this.dataMap, writer);
            writer.flush();
        }
        catch (TemplateException e)
        {
            logger.error(this.template, e);
            throw new IOException(e.getMessage());
        }
    }

    public void putData(String name, Object value)
    {
        this.dataMap.put(name, value);
    }

    public void setEncoding(String encoding)
    {
        this.encoding = encoding;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    public void setTemplate(String template)
    {
        this.template = template;
    }
}
