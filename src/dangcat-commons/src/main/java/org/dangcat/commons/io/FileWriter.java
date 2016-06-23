package org.dangcat.commons.io;

import org.apache.log4j.Logger;
import org.dangcat.commons.utils.DateUtils;
import org.dangcat.commons.utils.Environment;
import org.dangcat.commons.utils.ValueUtils;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 文件定制写入器。
 * @author dangcat
 * 
 */
public class FileWriter
{
    protected static final Logger logger = Logger.getLogger(FileWriter.class);
    /** 起始时间。 */
    private long beginTime = 0l;
    /** 当前写入的记录数。 */
    private long count = 0;
    /** 当前文件。 */
    private File currentFile = null;
    /** 日期格式化。 */
    private DateFormat dateFormat = null;
    /** 最小周期，单位秒，默认30秒。 */
    private long interval = 30l;
    /** 最大记录数，默认10000。 */
    private long maxLength = 10000l;
    /** 输出流。 */
    private OutputStream outputStream = null;
    /** 文件名的日期模板。 */
    private String pattern = "yyyy_MM_dd_HH_mm_ss_SSS";
    /** 文件前缀。 */
    private String preFix = null;
    /** 写入路径。 */
    private String storePath;
    /** 文件后缀，默认txt。 */
    private String suffix = ".txt";

    /**
     * 关闭文件。
     * @throws IOException
     */
    public File close()
    {
        File storeFile = null;
        if (this.outputStream != null)
        {
            try
            {
                this.outputStream.flush();
                this.outputStream.close();
                this.outputStream = null;
                storeFile = new File(this.currentFile.getAbsolutePath().replace(".tmp", this.getSuffix()));
                this.currentFile.renameTo(storeFile);
            }
            catch (IOException e)
            {
                logger.error(this, e);
            }
            finally
            {
                this.currentFile = null;
                this.count = 0;
            }
        }
        return storeFile;
    }

    public long getCount()
    {
        return count;
    }

    public File getCurrentFile()
    {
        return this.currentFile;
    }

    private DateFormat getDateFormat()
    {
        if (this.dateFormat == null)
            this.dateFormat = new SimpleDateFormat(this.getPattern());
        return this.dateFormat;
    }

    /**
     * 计算文件名。
     * @return
     */
    private String getFileName()
    {
        StringBuilder filePath = new StringBuilder();
        if (this.getStorePath() != null)
        {
            File direcroty = new File(this.getStorePath());
            if (!direcroty.isDirectory() || !direcroty.exists())
            {
                FileUtils.mkdir(direcroty.getAbsolutePath());
                logger.info("The store path is created: " + direcroty.getAbsolutePath());
            }
            filePath.append(this.getStorePath());
            filePath.append(File.separator);
        }
        if (!ValueUtils.isEmpty(this.getPreFix()))
        {
            filePath.append(this.getPreFix());
            filePath.append("-");
        }
        filePath.append(this.getDateFormat().format(new Date()));
        filePath.append(".tmp");
        return filePath.toString();
    }

    public long getInterval()
    {
        return interval;
    }

    public void setInterval(long interval) {
        this.interval = interval;
    }

    public long getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(long maxLength) {
        this.maxLength = maxLength;
    }

    /**
     * 获得输出流对象。
     * @return
     * @throws FileNotFoundException
     */
    public OutputStream getOutputStream()
    {
        if (this.outputStream == null)
        {
            try
            {
                this.currentFile = new File(this.getFileName());
                this.outputStream = new BufferedOutputStream(new FileOutputStream(this.currentFile));
                this.beginTime = DateUtils.currentTimeMillis();
            }
            catch (FileNotFoundException e)
            {
                logger.error(this, e);
            }
        }
        return this.outputStream;
    }

    public String getPattern()
    {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public String getPreFix()
    {
        return preFix;
    }

    public void setPreFix(String preFix) {
        this.preFix = preFix;
    }

    public String getStorePath()
    {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getSuffix()
    {
        if (!this.suffix.startsWith("."))
            this.suffix = "." + this.suffix;
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void increaseCount(long count)
    {
        this.count += count;
    }

    /**
     * 检查文件是否符合关闭要求。
     */
    public boolean isTimeOut()
    {
        boolean isTimeOut = false;
        if (this.count > 0)
        {
            if (Environment.isTestEnabled())
                isTimeOut = true;
            else if (this.getMaxLength() > 0l && this.count > this.getMaxLength())
                isTimeOut = true;
            else if (this.getInterval() > 0l && DateUtils.currentTimeMillis() - this.beginTime > this.getInterval() * 1000l)
                isTimeOut = true;
        }
        return isTimeOut;
    }

    /**
     * 按照要求切换文件。
     */
    public boolean switchFile()
    {
        boolean reault = false;
        if (this.isTimeOut())
        {
            this.close();
            reault = true;
        }
        return reault;
    }

    /**
     * 写入数据到文件。
     * @throws IOException
     */
    public void write(Object data) throws IOException
    {
        if (data != null)
        {
            String value = null;
            if (data instanceof String)
                value = (String) data;
            else
                value = data.toString();
            this.getOutputStream().write(value.getBytes());
            this.count++;
        }
    }
}
