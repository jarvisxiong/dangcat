package org.dangcat.web.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.FileCleanerCleanup;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileCleaningTracker;
import org.apache.log4j.Logger;
import org.dangcat.boot.event.ChangeEventAdaptor;
import org.dangcat.commons.io.FileUtils;
import org.dangcat.commons.utils.ValueUtils;
import org.dangcat.framework.event.Event;
import org.dangcat.web.invoke.InvokeProcess;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 上传管理。
 *
 * @author dangcat
 */
public class UploadManager {
    private static final String FILE_PREFIX = "UPLOAD";
    private static final String FILE_SUFFIX = ".tmp";
    protected static Logger logger = Logger.getLogger(UploadManager.class);
    private static UploadManager instance = new UploadManager();
    private DiskFileItemFactory diskFileItemFactory = null;

    private UploadManager() {
    }

    public static UploadManager getInstance() {
        return instance;
    }

    /**
     * 判断是否是上传文件请求。
     *
     * @param request 上传请求。
     * @return 结果。
     */
    public static boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent(request);
    }

    private ServletFileUpload createServletFileUpload(HttpServletRequest request) {
        ServletFileUpload servletFileUpload = new ServletFileUpload(this.getDiskFileItemFactory(request));
        if (UploadConfig.getInstance().getMaxRequestSize() != null)
            servletFileUpload.setFileSizeMax(UploadConfig.getInstance().getMaxRequestSize());
        return servletFileUpload;
    }

    /**
     * 产生上传的临时文件。
     *
     * @return 临时文件对象。
     * @throws IOException
     */
    public File createTmpFile() {
        File tmpFile = null;
        try {
            if (this.diskFileItemFactory != null)
                tmpFile = File.createTempFile(FILE_PREFIX, FILE_SUFFIX, this.getRepository());
            else
                tmpFile = File.createTempFile(FILE_PREFIX, FILE_SUFFIX);
        } catch (Exception e) {
            logger.error("create temp file error", e);
        }
        return tmpFile;
    }

    private DiskFileItemFactory getDiskFileItemFactory(HttpServletRequest request) {
        if (this.diskFileItemFactory == null) {
            DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
            FileCleaningTracker fileCleaningTracker = FileCleanerCleanup.getFileCleaningTracker(request.getServletContext());
            diskFileItemFactory.setFileCleaningTracker(fileCleaningTracker);
            this.diskFileItemFactory = diskFileItemFactory;
            this.loadConfig();
            UploadConfig.getInstance().addChangeEventAdaptor(new ChangeEventAdaptor() {
                @Override
                public void afterChanged(Object sender, Event event) {
                    loadConfig();
                }
            });
        }
        return this.diskFileItemFactory;
    }

    /**
     * 上传目录。
     *
     * @return
     */
    public File getRepository() {
        if (this.diskFileItemFactory != null)
            return this.diskFileItemFactory.getRepository();
        return new File(UploadConfig.getInstance().getRepository());
    }

    private void loadConfig() {
        UploadConfig uploadConfig = UploadConfig.getInstance();
        this.diskFileItemFactory.setSizeThreshold(uploadConfig.getMaxMemorySize());

        if (FileUtils.mkdir(uploadConfig.getRepository()))
            this.diskFileItemFactory.setRepository(new File(uploadConfig.getRepository()));
    }

    /**
     * 处理上传请求，得到上传内容。
     *
     * @param request 请求对象。
     * @return 上传内容。
     * @throws Exception 上传异常。
     */
    public UploadContent upload(HttpServletRequest request) throws Exception {
        ServletFileUpload servletFileUpload = this.createServletFileUpload(request);

        UploadContent uploadContent = new UploadContent();
        servletFileUpload.setProgressListener(uploadContent);

        InvokeProcess invokeProcess = (InvokeProcess) request.getSession().getAttribute(InvokeProcess.class.getSimpleName());
        if (invokeProcess != null)
            uploadContent.setInvokeStep(invokeProcess.getInvokeStep(InvokeProcess.UPLOAD));

        List<FileItem> fileItems = servletFileUpload.parseRequest(request);
        if (fileItems != null) {
            for (FileItem fileItem : fileItems) {
                if (fileItem.isFormField()) {
                    String name = fileItem.getFieldName();
                    String value = fileItem.getString();
                    if (!ValueUtils.isEmpty(name))
                        uploadContent.getParams().put(name, value);
                } else
                    uploadContent.setFileItem(fileItem);
            }
        }
        return uploadContent;
    }
}
