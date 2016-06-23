package org.dangcat.web.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.ProgressListener;
import org.apache.log4j.Logger;
import org.dangcat.web.invoke.InvokeStep;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * ÉÏ´«ÄÚÈÝ¡£
 * @author dangcat
 * 
 */
public class UploadContent implements ProgressListener
{
    protected static Logger logger = Logger.getLogger(UploadContent.class);

    private long bytesReaded = 0;
    private long contentLength = -1;
    private FileItem fileItem = null;
    private InvokeStep invokeStep = null;
    private Map<String, String> params = new HashMap<String, String>();

    public byte[] getBytes()
    {
        return this.fileItem == null ? null : this.fileItem.get();
    }

    public long getBytesReaded()
    {
        return bytesReaded;
    }

    public long getContentLength()
    {
        return contentLength;
    }

    public String getContentType()
    {
        return this.fileItem == null ? null : this.fileItem.getContentType();
    }

    public String getFieldName()
    {
        return this.fileItem == null ? null : this.fileItem.getFieldName();
    }

    public File getFile()
    {
        File uploadFile = new File(UploadManager.getInstance().getRepository(), this.getFileName());
        return this.write(uploadFile);
    }

    public String getFileName()
    {
        return this.fileItem == null ? null : this.fileItem.getName();
    }

    public InputStream getInputStream() throws IOException
    {
        return this.fileItem == null ? null : this.fileItem.getInputStream();
    }

    public InvokeStep getInvokeStep()
    {
        return invokeStep;
    }

    public void setInvokeStep(InvokeStep invokeStep) {
        this.invokeStep = invokeStep;
    }

    public Map<String, String> getParams()
    {
        return this.params;
    }

    public long getSize()
    {
        return this.fileItem == null ? 0 : this.fileItem.getSize();
    }

    protected void setFileItem(FileItem fileItem)
    {
        this.fileItem = fileItem;
    }

    @Override
    public void update(long bytesReaded, long contentLength, int items)
    {
        this.bytesReaded = bytesReaded;
        this.contentLength = contentLength;
        if (this.invokeStep != null)
        {
            this.invokeStep.setCurrent(bytesReaded);
            this.invokeStep.setTotal(contentLength);
        }
    }

    public File write(File file)
    {
        if (this.fileItem != null)
        {
            try
            {
                if (file == null)
                    file = UploadManager.getInstance().createTmpFile();
                if (file != null)
                {
                    this.fileItem.write(file);
                    logger.info("Upload content save to " + file.getAbsolutePath());
                }
            }
            catch (Exception e)
            {
                logger.error(file, e);
            }
        }
        return file;
    }
}
